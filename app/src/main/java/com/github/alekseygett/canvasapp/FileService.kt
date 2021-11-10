package com.github.alekseygett.canvasapp

import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.github.alekseygett.canvasapp.utils.attempt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class FileService : Service() {

    companion object {
        const val BITMAP_KEY = "bitmap"
    }

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getByteArrayExtra(BITMAP_KEY)?.let { data ->
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.count())
            save(bitmap)
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    private fun save(bitmap: Bitmap) {
        serviceScope.launch {
            val filename = generateFilename()

            val result = if (Build.VERSION.SDK_INT >= 29) {
                saveWithMediaStore(bitmap, filename)
            } else {
                saveWithPlainFilesApi(bitmap, filename)
            }

            result.fold(
                onSuccess = {
                    Log.d(FileService::class.simpleName, "success")
                },
                onError = {
                    Log.d(FileService::class.simpleName, "failure")
                    println(it.localizedMessage)
                }
            )
        }
    }

    private fun generateFilename(): String {
        val currentTime = Date()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm")
        val timestamp = formatter.format(currentTime)
        val uuid = UUID.randomUUID().toString()

        return "$uuid-$timestamp"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveWithMediaStore(bitmap: Bitmap, filename: String) = attempt {
        val mimeType = "image/png"
        val relativePath = Environment.DIRECTORY_PICTURES + File.separator + "masterpieces"

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
        }

        val contentResolver = applicationContext.contentResolver

        val uri = contentResolver.insert(MediaStore.Images.Media.getContentUri("external"), values)

        uri?.let { contentResolver.openOutputStream(it) }
            .use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

        return@attempt true
    }

    private fun saveWithPlainFilesApi(bitmap: Bitmap, filename: String) = attempt {
        val directoryPath = Environment.DIRECTORY_PICTURES + File.separator + "masterpieces"
        val filePath = directoryPath + File.separator + filename + ".png"

        val directory = File(directoryPath)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(filePath)

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }

        return@attempt true
    }

}