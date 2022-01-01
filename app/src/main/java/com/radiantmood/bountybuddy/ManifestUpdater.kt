package com.radiantmood.bountybuddy

import android.app.Activity
import android.util.Log
import com.radiantmood.bountybuddy.data.ManifestDataResponse
import com.radiantmood.bountybuddy.network.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
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
 * Changes to definitions: https://archive.destiny.report/
 */
class ManifestUpdater(private val activity: Activity) {
    var isManifestUpdated: Boolean = false
        private set
    private var manifestResponse: ManifestDataResponse? = null
    var version: String by ManifestVersionDelegate()

    suspend fun updateManifest() = withContext(Dispatchers.IO) {
        manifestResponse = RetrofitBuilder.bungieService.getManifest()
        val newestVersion = manifestResponse?.Response?.version
        Log.v("araiff", "Current version is $version.")
        Log.v("araiff", "Newest version is $newestVersion.")
        if (newestVersion != null && newestVersion != version) {
            Log.v("araiff", "Version is older than currently downloaded version. Updating content.")
            fetchItemManifest()
            version = newestVersion
            Log.v("araiff", "Manifest data obtained.")
        } else {
            Log.v("araiff", "No Manifest update needed.")
        }
        isManifestUpdated = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Throws(Exception::class)
    private suspend fun fetchItemManifest() = withContext(Dispatchers.IO) {
        val req = Request.Builder()
            .url("https://www.bungie.net" + getWorldContentPath(manifestResponse))
            .build()
        val res = RetrofitBuilder.authClient.newCall(req).execute()
        val zip = File(activity.cacheDir, "world_content.zip")
        val sink = zip.sink().buffer()
        sink.writeAll(res.body!!.source())
        sink.close()
        Log.v("araiff", "World content zip downloaded.")

        unzipDb(zip, activity.getDatabasePath("asdf").parentFile)
        zip.delete() // no need to keep around zip download
        // maybe it makes sense to take notes from this to use the downloaded db?? https://stackoverflow.com/questions/10254872/download-sqlite-database-from-internet-and-load-into-android-application
        Log.v("araiff", "World content unzipped.")
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
    private fun getWorldContentPath(response: ManifestDataResponse?): String {
        return checkNotNull(response?.Response?.mobileWorldContentPaths?.en)
    }
}