package com.radiantmood.bountybuddy

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.radiantmood.bountybuddy.data.MembershipDataResponse
import com.radiantmood.bountybuddy.network.RetrofitBuilder
import com.radiantmood.bountybuddy.util.toPrettyString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class OperationalDataFetcher(
    private val activity: Activity,
    private val statusCallback: ((String) -> Unit)
) {
    private val authManager get() = App.authManager
    private var authState by authManager::authState
    private var membershipData: MembershipDataResponse? = null
    private var manifestData: JsonElement? = null
    private var itemManifestData: JsonElement? = null
    private var profileData: JsonElement? = null

    private var mutex = Mutex()

    suspend fun fetch() {
        if (!mutex.isLocked) {
            mutex.withLock {
                runState()
            }
        }
    }

    suspend fun onNewIntent(intent: Intent) {
        authManager.parsePossibleAuthRedirect(intent)
        if (authState.code != null) {
            statusCallback.invoke("Code obtained.")
            fetchToken()
        }
    }

    private suspend fun runState() {
        withContext(Dispatchers.IO) {
            when {
                (authState.code == null || (authState.isTokenExpired())) -> {
                    withContext(Dispatchers.Main) {
                        authManager.requestAuthorization(activity)
                    }
                }
                authState.isTokenExpired() -> fetchToken()
                manifestData == null -> fetchManifest()
                itemManifestData == null -> fetchItemManifest()
//                membershipData == null -> fetchMembership()
//                profileData == null -> fetchProfile()
                else -> accessDb()
            }
        }
    }

    private suspend fun accessDb() {
        tryLog("failed to access db") {
            withContext(Dispatchers.IO) {
                val dbHelper = WorldContentDbHelper(App)
                val db = dbHelper.readableDatabase
                val projection = arrayOf(WorldContentContract.COLUMN_NAME_ID, WorldContentContract.COLUMN_NAME_JSON)
                val selection = "${WorldContentContract.COLUMN_NAME_ID} = ?"
                val selectionArgs = arrayOf("2147433548")
                val cursor = db.query(
                    WorldContentContract.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null,
                    "1"
                )

                val jsons = mutableListOf<JsonElement>()
                val lenientJson = Json { isLenient = true }
                with(cursor) {
                    while (moveToNext()) {
                        val string = getString(getColumnIndexOrThrow(WorldContentContract.COLUMN_NAME_JSON))
                        jsons.add(lenientJson.decodeFromString(string))
                    }
                }
                cursor.close()
                statusCallback.invoke(jsons.first().toPrettyString())
            }
        }
    }

    private suspend fun fetchToken() {
        tryLog("Failed to get new token") {
            authManager.requestToken()
            statusCallback.invoke("Token obtained.")
            runState()
        }
    }

    private suspend fun fetchManifest() {
        tryLog("Failed to get manifest data") {
            manifestData = RetrofitBuilder.bungieService.getManifest()
            statusCallback.invoke("Manifest data obtained.")
            runState()
        }
    }

    // TODO: only download if the current db is out of date
    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun fetchItemManifest() {
        tryLog("Failed to download world content manifest") {
            val req = Request.Builder()
                .url("https://www.bungie.net" + manifestData!!.jsonObject["Response"]!!.jsonObject["mobileWorldContentPaths"]!!.jsonObject["en"]!!.jsonPrimitive.content)
                .build()
            withContext(Dispatchers.IO) {
                val res = RetrofitBuilder.authClient.newCall(req).execute()
                val zip = File(activity.cacheDir, "world_content.zip")
                val sink = zip.sink().buffer()
                sink.writeAll(res.body!!.source())
                sink.close()
                statusCallback.invoke("World content zip downloaded.")

                unzipDb(zip, activity.getDatabasePath("asdf").parentFile)
                zip.delete() // no need to keep around zip download
                // maybe it makes sense to take notes from this to use the downloaded db?? https://stackoverflow.com/questions/10254872/download-sqlite-database-from-internet-and-load-into-android-application
                statusCallback.invoke("World content unzipped.")

                itemManifestData = JsonPrimitive(true)
            }
            runState()
        }
    }

    private suspend fun fetchMembership() {
        tryLog("Failed to get membership data") {
            membershipData = RetrofitBuilder.bungieService.getMembership()
            statusCallback.invoke("Membership data obtained.")
            runState()
        }
    }

    private suspend fun fetchProfile() {
        tryLog("Failed to get profile") {
            profileData = RetrofitBuilder.bungieService.getDestinyProfile(
                membershipType = 3, // TODO: don't hard-code membership type and figure out how to map the right id to the right type. profileData!!.Response.destinyMemberships.first().applicableMembershipTypes.first(),
                destinyMembershipId = membershipData!!.Response.destinyMemberships.first().membershipId,
                components = "201",//"100,101,102,103,104,105,200,201,202,203,204,205,300,301,302,303,304,305,306,307,208,309,310,400,401,402,500,600,700,800,900,1000,1100,1200",
            )
            statusCallback.invoke("Profile obtained.")
            runState()
        }
    }

    private inline fun tryLog(failMsg: String, block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            Log.e("araiff", e.message, e)
            statusCallback.invoke(failMsg)
        }
    }

    // Grabbed from https://stackoverflow.com/questions/3382996/how-to-unzip-files-programmatically-in-android
    @Throws(IOException::class)
    fun unzipDb(zipFile: File?, targetDirectory: File?) {
        ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zis ->
            var zentry: ZipEntry?
            var count: Int
            val buffer = ByteArray(8192)
            while (zis.nextEntry.also { zentry = it } != null) {
                val ze = zentry!!
                val name = ze.name.let {
                    if (it.startsWith("world_sql_content")) {
                        "WorldContent.db"
                    } else {
                        it
                    }
                }
                val file = File(targetDirectory, name)
                val dir = if (ze.isDirectory) file else file.parentFile
                if (!dir.isDirectory && !dir.mkdirs()) throw FileNotFoundException(
                    "Failed to ensure directory: " +
                        dir.absolutePath
                )
                if (ze.isDirectory) continue
                FileOutputStream(file).use { fout ->
                    while (zis.read(buffer).also { count = it } != -1) fout.write(buffer, 0, count)
                }
            }
        }
    }
}