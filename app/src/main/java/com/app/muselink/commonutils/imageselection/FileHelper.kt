package com.app.muselink.commonutils.imageselection

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws

class FileHelper private constructor(private val context: Context) {

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageTempFile(filePathDir: File): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        val imageFileName = "JPEG_" + timeStamp + "_"
        return File.createTempFile(
            imageFileName,
            ".jpg",
            filePathDir
        )
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private lateinit var sSingleton: FileHelper

        fun getInstance(ctx: Context): FileHelper {
            synchronized(FileHelper::class.java) {
                sSingleton =
                    FileHelper(ctx)
            }

            return sSingleton
        }

        //get Path
        @TargetApi(Build.VERSION_CODES.KITKAT)
        fun getRealPathFromURI(context: Context, uri: Uri): String? {

            // DocumentProvider
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(
                        uri
                    )
                ) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                } else if (isDownloadsDocument(
                        uri
                    )
                ) {
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))

                    return getDataColumn(
                        context,
                        contentUri,
                        null,
                        null
                    )
                } else if (isMediaDocument(
                        uri
                    )
                ) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])

                    return getDataColumn(
                        context,
                        contentUri,
                        selection,
                        selectionArgs
                    )
                }// MediaProvider
                // DownloadsProvider
            } else return if ("content".equals(uri.scheme!!, ignoreCase = true)) {
                // Return the remote address
                if (isGooglePhotosUri(
                        uri
                    )
                ) uri.lastPathSegment else getDataColumn(
                    context,
                    uri,
                    null,
                    null
                )

            } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
                uri.path
            } else
                getRealPathFromURIDB(
                    context,
                    uri
                )// File
            // MediaStore (and general)

            return null
        }

        /**
         * Gets real path from uri.
         *
         *
         * @param context
         * @param contentUri the content uri
         * @return the real path from uri
         */
        fun getRealPathFromURIDB(context: Context, contentUri: Uri): String? {
            val cursor = context.contentResolver.query(contentUri, null, null, null, null)
            if (cursor == null) {
                return contentUri.path
            } else {
                cursor.moveToFirst()
                val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                val realPath = cursor.getString(index)
                cursor.close()
                return realPath
            }
        }

        private fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                                  selectionArgs: Array<String>?): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)

            try {
                cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }

        /**
         * Is external storage document boolean.
         *
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        /**
         * Is downloads document boolean.
         *
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        /**
         * Is media document boolean.
         *
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        /**
         * Is google photos uri boolean.
         *
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        private fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.authority
        }
    }
}
