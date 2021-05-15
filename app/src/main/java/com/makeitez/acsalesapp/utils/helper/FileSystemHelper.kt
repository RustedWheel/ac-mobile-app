package com.makeitez.acsalesapp.utils.helper

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.makeitez.acsalesapp.BuildConfig
import com.makeitez.acsalesapp.core.ACFragment
import java.io.File
import java.io.InputStream

object FileSystemHelper {

    private const val PERMISSION_REQUEST_CODE = 900
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    fun openFile(fragment: Fragment, fileUri: Uri, mimeType: String) {
        fragment.context?.let {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, mimeType)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            fragment.startActivity(intent)
        }
    }

    fun openPdf(fragment: Fragment, fileUri: Uri) {
        openFile(fragment, fileUri, "application/pdf")
    }

    fun isWriteAllowed(context: Context) = try {
        REQUIRED_PERMISSIONS.any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
    } catch (ignored: RuntimeException) {
        false // on some devices checkSelfPermission might fail instead of returning false
    }

    fun requestWritePermission(fragment:  ACFragment) {
        fragment.requestPermissions(REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE)
    }

    fun checkWritePermissionGranted(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) =
        requestCode == PERMISSION_REQUEST_CODE && grantResults.firstOrNull() == PermissionChecker.PERMISSION_GRANTED

    fun savePdfFileToDownloadsFolder(context: Context, inputStream: InputStream, fileName: String, appendCurrentTime: Boolean = true): Uri? {
        val pdfFileName = fileName + if(appendCurrentTime) "-${System.currentTimeMillis()}" else ""
        return saveFileToExternalFolder(context, inputStream, Environment.DIRECTORY_DOWNLOADS, "files/pdf", "${pdfFileName}.pdf")
    }

    fun saveFileToExternalFolder(context: Context, inputStream: InputStream, location: String, fileType: String, fileName: String): Uri? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, fileType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, location)
            }
            val fileUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            fileUri?.let { uri ->
                resolver.openOutputStream(uri)?.use { output ->
                    inputStream.copyTo(output)
                    output.close()
                    return uri
                }
            }
            return null
        } else {
            val folder = Environment.getExternalStoragePublicDirectory(location)
            val file = File(folder, fileName)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            file.outputStream().use { output ->
                inputStream.copyTo(output)
                output.close()
            }
            return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
        }
    }

}