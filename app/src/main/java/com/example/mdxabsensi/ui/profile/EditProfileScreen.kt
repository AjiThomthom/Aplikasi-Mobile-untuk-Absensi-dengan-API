package com.example.mdxabsensi.ui.profile

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.mdxabsensi.ui.theme.ElegantPurple
import com.example.mdxabsensi.ui.theme.DeepIndigo
import com.example.mdxabsensi.ui.theme.LocalIsDarkTheme
import com.example.mdxabsensi.utils.ImageUtils
import com.example.mdxabsensi.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    nama: String,
    email: String,
    foto: String,
    fotoLastUpdated: Long = 0L,
    isLoading: Boolean,
    error: String?,
    isSuccess: Boolean,
    onSaveClick: (String, String, String) -> Unit,
    onNavigateBack: () -> Unit,
    onResetStatus: () -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current

    val contentColor = if (isDark) Color.White else Color(0xFF1A1A1A)
    val subContentColor = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF666666)

    var inputNama by remember { mutableStateOf(nama) }
    var inputEmail by remember { mutableStateOf(email) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var fotoBase64 by remember { mutableStateOf("") }

    // State untuk memunculkan pilihan Kamera atau Galeri
    var showImageSourceDialog by remember { mutableStateOf(false) }
    // State untuk menyimpan URI sementara saat kamera sedang mengambil foto
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(nama, email) {
        if (inputNama.isEmpty()) inputNama = nama
        if (inputEmail.isEmpty()) inputEmail = email
    }

    LaunchedEffect(isSuccess, error) {
        if (isSuccess) {
            Toast.makeText(context, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
            onResetStatus()
            onNavigateBack()
        }
        error?.let {
            Toast.makeText(context, "Gagal: $it", Toast.LENGTH_LONG).show()
            onResetStatus()
        }
    }

    // Launcher untuk memilih dari Galeri
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            val base64 = ImageUtils.uriToBase64(context, it)
            if (base64 != null) {
                fotoBase64 = base64
            } else {
                Toast.makeText(context, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Launcher untuk mengambil foto dari Kamera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            tempCameraUri?.let { uri ->
                selectedImageUri = uri
                val base64 = ImageUtils.uriToBase64(context, uri)
                if (base64 != null) {
                    fotoBase64 = base64
                } else {
                    Toast.makeText(context, "Gagal memproses gambar kamera", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val bgGradient = if (isDark) {
        Brush.verticalGradient(colors = listOf(DeepIndigo, Color(0xFF090814)))
    } else {
        Brush.verticalGradient(colors = listOf(Color(0xFFF8F0FA), Color(0xFFFDFDFD)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Edit Profil",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            fontFamily = FontFamily.SansSerif
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
                        containerColor = Color.Transparent,
                        titleContentColor = contentColor,
                        navigationIconContentColor = contentColor
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .shadow(12.dp, CircleShape, spotColor = ElegantPurple.copy(alpha = 0.2f))
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF1E1E2E) else Color.White)
                            .border(4.dp, if (isDark) Color.White.copy(alpha = 0.05f) else Color.White, CircleShape)
                            .clickable { showImageSourceDialog = true }, // Menampilkan dialog saat ditekan
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Foto Baru",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (foto.isNotEmpty()) {
                            val baseImageUrl = ImageUtils.getAbsoluteUrl(foto)
                            val imageUrl = if (fotoLastUpdated > 0L) "$baseImageUrl?t=$fotoLastUpdated" else baseImageUrl
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Foto saat ini",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Placeholder",
                                modifier = Modifier.size(70.dp),
                                tint = subContentColor
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .shadow(8.dp, CircleShape)
                            .background(ElegantPurple, CircleShape)
                            .border(3.dp, if (isDark) DeepIndigo else Color.White, CircleShape)
                            .clickable { showImageSourceDialog = true }, // Menampilkan dialog saat ditekan
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Ubah Foto",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "Ketuk ikon untuk mengubah foto",
                    fontSize = 13.sp,
                    color = subContentColor,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(40.dp))

                val surfaceColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White
                val inputContainerColor = if (isDark) Color.Black.copy(alpha = 0.2f) else Color(0xFFF5F6F8)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(if (isDark) 8.dp else 4.dp, RoundedCornerShape(28.dp), spotColor = ElegantPurple.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    border = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)) else BorderStroke(1.dp, Color.Black.copy(alpha = 0.03f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        OutlinedTextField(
                            value = inputNama,
                            onValueChange = { inputNama = it },
                            label = { Text("Nama Lengkap") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = ElegantPurple)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantPurple,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = ElegantPurple,
                                unfocusedLabelColor = subContentColor,
                                focusedTextColor = contentColor,
                                unfocusedTextColor = contentColor,
                                focusedContainerColor = inputContainerColor,
                                unfocusedContainerColor = inputContainerColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = inputEmail,
                            onValueChange = { inputEmail = it },
                            label = { Text("Email") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = ElegantPurple)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantPurple,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = ElegantPurple,
                                unfocusedLabelColor = subContentColor,
                                focusedTextColor = contentColor,
                                unfocusedTextColor = contentColor,
                                focusedContainerColor = inputContainerColor,
                                unfocusedContainerColor = inputContainerColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        if (inputNama.trim().isEmpty() || inputEmail.trim().isEmpty()) {
                            Toast.makeText(context, "Nama dan Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        } else {
                            onSaveClick(inputNama.trim(), inputEmail.trim(), fotoBase64)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = ElegantPurple.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElegantPurple)
                ) {
                    Text(
                        "Simpan Perubahan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Dialog Pilihan Sumber Gambar (Kamera atau Galeri)
        if (showImageSourceDialog) {
            Dialog(onDismissRequest = { showImageSourceDialog = false }) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E2E) else Color.White),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ubah Foto Profil",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = contentColor,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Tombol Kamera
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        showImageSourceDialog = false
                                        val uri = createImageUri(context)
                                        if (uri != null) {
                                            tempCameraUri = uri
                                            cameraLauncher.launch(uri)
                                        } else {
                                            Toast.makeText(context, "Gagal menyiapkan kamera", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(ElegantPurple.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = "Kamera", tint = ElegantPurple, modifier = Modifier.size(30.dp))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Kamera", color = contentColor, fontWeight = FontWeight.SemiBold)
                            }

                            // Tombol Galeri
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        showImageSourceDialog = false
                                        galleryLauncher.launch("image/*")
                                    }
                                    .padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(Color(0xFF4CAF50).copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Galeri", tint = Color(0xFF4CAF50), modifier = Modifier.size(30.dp))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Galeri", color = contentColor, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = { showImageSourceDialog = false },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("BATAL", color = subContentColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Overlay Loading
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                val surfaceColorLoader = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColorLoader)
                ) {
                    Box(modifier = Modifier.padding(32.dp)) {
                        CircularProgressIndicator(color = ElegantPurple)
                    }
                }
            }
        }
    }
}

// Fungsi bantuan untuk membuat file URI yang aman untuk kamera
fun createImageUri(context: Context): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "profile_pic_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}

@Composable
fun EditProfileRoute(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val nama by viewModel.nama.collectAsState()
    val email by viewModel.email.collectAsState()
    val foto by viewModel.foto.collectAsState()
    val fotoLastUpdated by viewModel.fotoLastUpdated.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSuccess by viewModel.isUpdateSuccess.collectAsState()

    EditProfileScreen(
        nama = nama,
        email = email,
        foto = foto,
        fotoLastUpdated = fotoLastUpdated,
        isLoading = isLoading,
        error = error,
        isSuccess = isSuccess,
        onSaveClick = { namaBaru, emailBaru, fotoBase64 ->
            viewModel.updateProfile(namaBaru, emailBaru, fotoBase64)
        },
        onNavigateBack = onNavigateBack,
        onResetStatus = {
            viewModel.resetUpdateStatus()
        }
    )
}