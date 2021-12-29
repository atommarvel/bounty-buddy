package com.radiantmood.bountybuddy

import android.app.Activity
import com.radiantmood.bountybuddy.network.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonElement
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

/**
 * Some Manifest resources:
 * About the manifest: https://github.com/Bungie-net/api/wiki/Obtaining-Destiny-Definitions-%22The-Manifest%22
 * A site explore manifest data: https://data.destinysets.com/
 * Someone's exploration of the api: https://github.com/vpzed/Destiny2-API-Info-wiki/blob/master/API-Introduction-Part-3-Manifest.md
 * Official api doc: https://bungie-net.github.io/multi/index.html
 */
class ManifestUpdater(private val statusCallback: (String) -> Unit, private val activity: Activity) {
    var manifestMap: JsonElement? = null
    var version: String by ManifestVersionDelegate()

    suspend fun updateManifest() = withContext(Dispatchers.IO) {
        manifestMap = RetrofitBuilder.bungieService.getManifest()
        val newestVersion = manifestMap?.jsonObject?.get("Response")?.jsonObject?.get("version")?.jsonPrimitive?.content
        statusCallback.invoke("Current version is $version.")
        statusCallback.invoke("Newest version is $newestVersion.")
        if (newestVersion != null && newestVersion != version) {
            statusCallback.invoke("Version is older than currently downloaded version. Updating content.")
            fetchItemManifest()
            version = newestVersion
            statusCallback.invoke("Manifest data obtained.")
        } else {
            statusCallback.invoke("No Manifest update needed.")
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Throws(Exception::class)
    private suspend fun fetchItemManifest() = withContext(Dispatchers.IO) {
        val req = Request.Builder()
            .url("https://www.bungie.net" + getWorldContentPath(manifestMap))
            .build()
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

    @Throws(Exception::class)
    private fun getWorldContentPath(jsonElement: JsonElement?): String {
        return checkNotNull(jsonElement?.jsonObject?.get("Response")?.jsonObject?.get("mobileWorldContentPaths")?.jsonObject?.get("en")?.jsonPrimitive?.content)
    }
}