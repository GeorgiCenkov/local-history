package com.example.localhistory.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.localhistory.data.remote.UploadService
import com.example.localhistory.model.request.CreateUploadUrlRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ImageUploadRepository(
    @ApplicationContext private val context: Context,
    private val uploadService: UploadService,
    @UploadHttpClient private val uploadHttpClient: OkHttpClient
) {
    suspend fun uploadImage(uriString: String): LandmarkResult<String> = withContext(Dispatchers.IO) {
        runCatching {
            val uri = Uri.parse(uriString)
            val contentType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val fileName = context.contentResolver.getDisplayName(uri) ?: "landmark-image.jpg"
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: return@runCatching LandmarkResult.Error("Could not read selected image")

            val signedUrlResponse = uploadService.createSignedUploadUrl(
                CreateUploadUrlRequest(
                    fileName = fileName,
                    contentType = contentType
                )
            )
            val uploadTarget = signedUrlResponse.body()

            if (!signedUrlResponse.isSuccessful || uploadTarget == null) {
                return@runCatching LandmarkResult.Error(
                    signedUrlResponse.errorBody()?.string() ?: "Could not create upload URL"
                )
            }

            // The backend signs this URL for storage directly, so upload with a plain client
            // and keep backend auth headers out of the Supabase request.
            val token = uploadTarget.signedUrl
                .substringAfter("token=")
                .substringBefore("&") // signed url contains an auth token we must use

            val uploadRequest = Request.Builder()
                .url(uploadTarget.signedUrl)
                .header("Authorization", "Bearer $token")
                .put(bytes.toRequestBody(contentType.toMediaTypeOrNull()))
                .build()

            uploadHttpClient.newCall(uploadRequest).execute().use { uploadResponse ->
                if (!uploadResponse.isSuccessful) {
                    val uploadError = uploadResponse.body?.string().orEmpty()
                    return@runCatching LandmarkResult.Error(
                        "Could not upload image (${uploadResponse.code})${
                            uploadError.takeIf { it.isNotBlank() }?.let { ": $it" }.orEmpty()
                        }"
                    )
                }
            }

            LandmarkResult.Success(uploadTarget.publicUrl)
        }.getOrElse {
            LandmarkResult.Error(it.message ?: "Image upload failed")
        }
    }

    private fun android.content.ContentResolver.getDisplayName(uri: Uri): String? {
        return query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null

            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0) cursor.getString(index) else null
        }
    }
}
