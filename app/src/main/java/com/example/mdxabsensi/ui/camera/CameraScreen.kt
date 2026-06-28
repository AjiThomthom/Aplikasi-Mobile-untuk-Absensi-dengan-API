package com.example.mdxabsensi.ui.camera

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.mdxabsensi.utils.ImageUtils
import com.example.mdxabsensi.viewmodel.AbsensiViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    type: String,
    nik: String,
    isLoading: Boolean,
    error: String?,
    isSuccess: Boolean,
    onCaptureSuccess: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onResetStatus: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    LaunchedEffect(isSuccess, error) {
        if (isSuccess) {
            Toast.makeText(context, "Absen ${type.replaceFirstChar { it.uppercase() }} Berhasil!", Toast.LENGTH_SHORT).show()
            onResetStatus()
            onNavigateBack()
        }
        error?.let {
            Toast.makeText(context, "Gagal Absen: $it", Toast.LENGTH_LONG).show()
            onResetStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Selfie Absen ${type.replaceFirstChar { it.uppercase() }}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { ctx ->
                    val view = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(view.surfaceProvider)
                        }

                        val capture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()
                        imageCapture = capture

                        val cameraSelector = try {
                            if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            } else {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            }
                        } catch (exc: Exception) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                capture
                            )
                        } catch (exc: Exception) {
                            Log.e("CameraX", "Binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    view
                },
                modifier = Modifier.fillMaxSize()
            )

            // Face Guide Overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val ovalWidth = canvasWidth * 0.65f
                val ovalHeight = ovalWidth * 1.35f
                val ovalLeft = (canvasWidth - ovalWidth) / 2f
                val ovalTop = (canvasHeight - ovalHeight) / 2.3f

                val path = Path().apply {
                    addOval(
                        Rect(
                            ovalLeft,
                            ovalTop,
                            ovalLeft + ovalWidth,
                            ovalTop + ovalHeight
                        )
                    )
                }

                // Darken external area
                clipPath(path, clipOp = ClipOp.Difference) {
                    drawRect(color = Color.Black.copy(alpha = 0.5f))
                }

                // Dashed green border for target face position
                drawOval(
                    color = Color(0xFF00C853), // Modern bright emerald green for alignment guidance
                    topLeft = Offset(ovalLeft, ovalTop),
                    size = Size(ovalWidth, ovalHeight),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    )
                )
            }

            // Floating instruction text
            Text(
                text = "Posisikan wajah Anda di dalam oval",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
                    .background(Color.Black.copy(alpha = 0.6f), shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Capture Controls overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        val capture = imageCapture ?: return@IconButton
                        val photoFile = File(
                            context.cacheDir,
                            "selfie_${System.currentTimeMillis()}.jpg"
                        )

                        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                        capture.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onError(exc: ImageCaptureException) {
                                    Log.e("CameraX", "Photo capture failed: ${exc.message}", exc)
                                    Toast.makeText(context, "Gagal mengambil gambar", Toast.LENGTH_SHORT).show()
                                }

                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    val savedUri = Uri.fromFile(photoFile)
                                    val base64 = ImageUtils.uriToBase64(context, savedUri)
                                    if (base64 != null) {
                                        onCaptureSuccess(base64)
                                    } else {
                                        Toast.makeText(context, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
                                    }
                                    if (photoFile.exists()) {
                                        photoFile.delete()
                                    }
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Ambil Foto",
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun CameraRoute(
    type: String,
    nik: String,
    viewModel: AbsensiViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context) }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    var showVerification by remember { mutableStateOf(false) }
    var capturedLat by remember { mutableStateOf<Double?>(null) }
    var capturedLon by remember { mutableStateOf<Double?>(null) }
    var pendingBase64 by remember { mutableStateOf("") }

    CameraScreen(
        type = type,
        nik = nik,
        isLoading = isLoading,
        error = error,
        isSuccess = isSuccess,
        onCaptureSuccess = { base64 ->
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        capturedLat = location?.latitude
                        capturedLon = location?.longitude
                        pendingBase64 = base64
                        showVerification = true
                    }.addOnFailureListener {
                        capturedLat = null
                        capturedLon = null
                        pendingBase64 = base64
                        showVerification = true
                    }
                } catch (e: SecurityException) {
                    capturedLat = null
                    capturedLon = null
                    pendingBase64 = base64
                    showVerification = true
                }
            } else {
                capturedLat = null
                capturedLon = null
                pendingBase64 = base64
                showVerification = true
            }
        },
        onNavigateBack = onNavigateBack,
        onResetStatus = {
            viewModel.resetStatus()
        }
    )

    if (showVerification) {
        AlertDialog(
            onDismissRequest = { showVerification = false },
            title = { Text("Verifikasi Lokasi", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Lokasi Anda saat ini telah dideteksi:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (capturedLat != null && capturedLon != null) 
                            "Koordinat: $capturedLat, $capturedLon" 
                        else "Lokasi tidak terdeteksi",
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Apakah Anda yakin ingin mengirim absensi dengan lokasi ini?")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showVerification = false
                        viewModel.absensi(nik, type, pendingBase64, capturedLat, capturedLon)
                    }
                ) {
                    Text("Konfirmasi & Kirim")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVerification = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
