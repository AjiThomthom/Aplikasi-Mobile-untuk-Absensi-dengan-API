package com.example.mdxabsensi.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.InputStream
import com.example.mdxabsensi.data.remote.RetrofitClient

object ImageUtils {

    fun getAbsoluteUrl(path: String?): String {
        if (path.isNullOrEmpty()) return ""
        
        // 1. If it doesn't start with http, prepend the BASE_URL
        val resolvedUrl = if (path.startsWith("http")) {
            path
        } else {
            val baseUrl = RetrofitClient.BASE_URL.removeSuffix("/")
            val cleanPath = if (path.startsWith("/")) path else "/uploads/$path"
            "$baseUrl$cleanPath"
        }

        // 2. If it is an emulator or localhost URL, replace it dynamically with the host of BASE_URL
        return try {
            val baseUri = Uri.parse(RetrofitClient.BASE_URL)
            val baseHost = baseUri.host ?: "10.0.2.2"
            val basePort = baseUri.port
            
            val replacement = if (basePort != -1) "$baseHost:$basePort" else baseHost
            if (resolvedUrl.contains("10.0.2.2") || resolvedUrl.contains("localhost") || resolvedUrl.contains("127.0.0.1")) {
                resolvedUrl.replace("10.0.2.2", replacement)
                           .replace("localhost", replacement)
                           .replace("127.0.0.1", replacement)
            } else {
                resolvedUrl
            }
        } catch (e: Exception) {
            resolvedUrl
        }
    }

    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            // 1. Read EXIF Orientation
            val exif = context.contentResolver.openInputStream(uri)?.use { stream ->
                try {
                    ExifInterface(stream)
                } catch (e: Exception) {
                    null
                }
            }
            val orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            ) ?: ExifInterface.ORIENTATION_NORMAL

            // 2. Decode original bitmap
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return null

            // 3. Resize bitmap to a reasonable size (max 500x500)
            val maxDimension = 500
            val width = originalBitmap.width
            val height = originalBitmap.height
            val (newWidth, newHeight) = if (width > height) {
                val ratio = height.toFloat() / width
                Pair(maxDimension, (maxDimension * ratio).toInt())
            } else {
                val ratio = width.toFloat() / height
                Pair((maxDimension * ratio).toInt(), maxDimension)
            }

            val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            if (resizedBitmap != originalBitmap) {
                originalBitmap.recycle()
            }

            // 4. Map EXIF Orientation to Matrix operations
            val matrix = Matrix()
            var needsTransformation = false
            when (orientation) {
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                    matrix.postScale(-1f, 1f)
                    needsTransformation = true
                }
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                    matrix.postScale(1f, -1f)
                    needsTransformation = true
                }
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    matrix.postRotate(90f)
                    matrix.postScale(-1f, 1f)
                    needsTransformation = true
                }
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    matrix.postRotate(270f)
                    matrix.postScale(-1f, 1f)
                    needsTransformation = true
                }
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    matrix.postRotate(90f)
                    needsTransformation = true
                }
                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    matrix.postRotate(180f)
                    needsTransformation = true
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    matrix.postRotate(270f)
                    needsTransformation = true
                }
            }

            // 5. Apply transformations if needed
            val finalBitmap = if (needsTransformation) {
                val rotated = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.width, resizedBitmap.height, matrix, true)
                if (rotated != resizedBitmap) {
                    resizedBitmap.recycle()
                }
                rotated
            } else {
                resizedBitmap
            }

            val outputStream = ByteArrayOutputStream()
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val byteArray = outputStream.toByteArray()
            finalBitmap.recycle()
            
            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
