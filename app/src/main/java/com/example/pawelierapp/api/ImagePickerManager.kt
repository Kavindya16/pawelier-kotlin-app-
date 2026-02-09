package com.example.pawelierapp.api

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * ImagePickerManager
 *
 * Purpose:
 * - Encapsulates camera and gallery access with runtime permission checks.
 * - Creates a temp image file and obtains a content Uri via FileProvider for camera captures.
 *
 * Implementation notes:
 * - STORAGE_PERMISSIONS adapt to Android 13+ (READ_MEDIA_IMAGES) vs legacy READ_EXTERNAL_STORAGE.
 * - Behavior is preserved. This pass only adds comments and small readability tweaks.
 */
class ImagePickerManager(private val context: Context) {

    companion object {
        // Camera permission constant used when requesting runtime permission
        const val CAMERA_PERMISSION = Manifest.permission.CAMERA

        // Required storage/gallery permissions differ by SDK level
        val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    /** Returns true if CAMERA permission is granted. */
    fun checkCameraPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED

    /** Returns true if all required storage/gallery permissions are granted. */
    fun checkStoragePermission(): Boolean =
        STORAGE_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

    /**
     * Creates a temporary image file to store a camera capture.
     * Caller is responsible for cleanup on failure (we delete on cancel in the launcher).
     */
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    /**
     * Obtains a content Uri for the given file using the app's FileProvider.
     * Ensure a matching provider authority and <paths> are declared in AndroidManifest.xml.
     */
    fun getUriForFile(file: File): Uri =
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
}

/**
 * Composable helper that wires image selection UI (dialog + launchers) into app state.
 *
 * Parameters:
 * - onImageSelected: callback with the selected image Uri (or null on failure/cancel).
 *
 * Behavior:
 * - Shows a dialog with two actions: Camera and Gallery.
 * - Handles camera and storage permissions at runtime.
 * - On camera success, returns the temp file's content Uri; on failure, temp file is deleted.
 */
@Composable
fun rememberImagePicker(
    onImageSelected: (Uri?) -> Unit
): ImagePickerState {
    val context = LocalContext.current
    val imagePickerManager = remember { ImagePickerManager(context) }

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var currentPhotoFile by remember { mutableStateOf<File?>(null) }

    // Camera launcher: uses ActivityResultContracts.TakePicture with a content Uri target
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoUri?.let { onImageSelected(it) }
        } else {
            // Cleanup temp file on failure/cancel to avoid orphan files
            currentPhotoFile?.delete()
            onImageSelected(null)
        }
    }

    // Gallery launcher: opens a system picker for images
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        onImageSelected(uri)
    }

    // Camera permission launcher: requests CAMERA and then proceeds to capture
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val photoFile = imagePickerManager.createImageFile()
                currentPhotoFile = photoFile
                val photoUri = imagePickerManager.getUriForFile(photoFile)
                currentPhotoUri = photoUri
                cameraLauncher.launch(photoUri)
            } catch (e: Exception) {
                e.printStackTrace()
                onImageSelected(null)
            }
        }
    }

    // Storage permission launcher: requests all required gallery permissions
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            galleryLauncher.launch("image/*")
        }
    }

    // Image source selection dialog (Camera/Gallery)
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Choose Image Source") },
            text = { Text("Select where you want to pick the image from") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false
                        if (imagePickerManager.checkCameraPermission()) {
                            try {
                                val photoFile = imagePickerManager.createImageFile()
                                currentPhotoFile = photoFile
                                val photoUri = imagePickerManager.getUriForFile(photoFile)
                                currentPhotoUri = photoUri
                                cameraLauncher.launch(photoUri)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                onImageSelected(null)
                            }
                        } else {
                            cameraPermissionLauncher.launch(ImagePickerManager.CAMERA_PERMISSION)
                        }
                    }
                ) {
                    Text("Camera")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false
                        if (imagePickerManager.checkStoragePermission()) {
                            galleryLauncher.launch("image/*")
                        } else {
                            storagePermissionLauncher.launch(ImagePickerManager.STORAGE_PERMISSIONS)
                        }
                    }
                ) {
                    Text("Gallery")
                }
            }
        )
    }

    return ImagePickerState(
        showDialog = { showImageSourceDialog = true }
    )
}

/** Holds state for the image picker UI trigger. */
data class ImagePickerState(
    val showDialog: () -> Unit
)

