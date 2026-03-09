package com.example.flora.ScreenMain

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Button
import android.widget.Space
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.flora.Authentication.AuthViewModel
import com.example.flora.Firebase.StoreViewModel
import com.example.flora.Firebase.UserState
import com.example.flora.Firebase.UserViewModel
import com.example.flora.R

import java.io.File
import java.util.Calendar

// ── Brand Colors ──
private val PrimaryPurple   = Color(0xFF6B21A8)
private val DeepPurple      = Color(0xFF4C1D95)
private val AccentViolet    = Color(0xFF8B5CF6)
private val LightLavender   = Color(0xFFF5F3FF)
private val CardSurface     = Color(0xFFFFFFFF)
private val SubtleGray      = Color(0xFFF8F7FA)
private val TextPrimary     = Color(0xFF1C1033)
private val TextSecondary   = Color(0xFF6B7280)
private val DividerColor    = Color(0xFFEDE9FE)

private val Purple700     = Color(0xFF7C3AED)
private val Purple500     = Color(0xFFA855F7)
private val Pink500       = Color(0xFFEC4899)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier     : Modifier       = Modifier,
    navController: NavController,
    userVM       : UserViewModel   = viewModel(factory = UserViewModel.Factory),
    storeVM      : StoreViewModel  = viewModel(factory = StoreViewModel.Factory),
    authVM: AuthViewModel,

    ) {
    val  userEmail = authVM.currentUser?.email.toString()
    LaunchedEffect(Unit) {

        userVM.loadUserByEmail(userEmail)
        storeVM.fetchStoreByEmail(userEmail)

    }

    val currentUser  by userVM.currentUser.collectAsState()
    val userState    by userVM.userState.collectAsState()
    val isLoading    = userState is UserState.Loading

    val currentStore by storeVM.currentStore.collectAsState()
    val hasStore     = currentStore.isNotEmpty()
    val myStore      = currentStore.firstOrNull()

    var showRegisterDialog by remember { mutableStateOf(false) }

    if (showRegisterDialog) {
        RegisterStoreDialog(
            userEmail = userEmail,
            storeVM   = storeVM,
            onDismiss = { showRegisterDialog = false }
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(userState) {
        when (userState) {
            is UserState.Success -> snackbarHostState.showSnackbar("✅ บันทึกข้อมูลสำเร็จ!")
            is UserState.Error   -> snackbarHostState.showSnackbar("❌ ${(userState as UserState.Error).message}")
            else -> Unit
        }
        userVM.resetUserState()
    }

    var username by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf(userEmail) }
    var password by remember { mutableStateOf("") }
//    var birthday by remember { mutableStateOf("") }
    var tel      by remember { mutableStateOf("") }
    var sex      by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val sexOption = listOf("Male", "Female", "Other")

    LaunchedEffect(currentUser) {
        currentUser?.let {
            username = it.username
            email    = it.email
            tel      = it.phoneNumber
            sex      = it.sex
        }
    }

    val context = LocalContext.current

//    val calendar = Calendar.getInstance()
//    val interactionSource = remember { MutableInteractionSource() }
//
//    val datePickerDialog = remember {
//        DatePickerDialog(
//            context,
//            { _, year, month, dayOfMonth ->
//                birthday = "$dayOfMonth/${month + 1}/$year"
//            },
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//        ).apply {
//            datePicker.maxDate = System.currentTimeMillis()
//        }
//    }

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor   = LightLavender,
        unfocusedContainerColor = SubtleGray,
        disabledContainerColor  = SubtleGray,
        focusedIndicatorColor   = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedTextColor        = TextPrimary,
        unfocusedTextColor      = TextPrimary
    )

    var showForgotDialog by remember{mutableStateOf(false)}
    var resetEmail by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF9F7FF)
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // ── Hero Header Banner ──
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(DeepPurple, PrimaryPurple, AccentViolet),
                                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                    end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .offset(x = 80.dp, y = (-40).dp)
                            .align(Alignment.TopEnd)
                            .background(Color.White.copy(alpha = 0.06f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .offset(x = 30.dp, y = 80.dp)
                            .align(Alignment.TopEnd)
                            .background(Color.White.copy(alpha = 0.06f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .offset(x = (-20).dp, y = 120.dp)
                            .align(Alignment.TopStart)
                            .background(Color.White.copy(alpha = 0.05f), CircleShape)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp)
                            .align(Alignment.TopStart),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "แก้ไขโปรไฟล์",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = Color.White
                            )
                            Text(
                                text = "อัปเดตข้อมูลส่วนตัวของคุณ",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .clickable { }
                                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.page_info),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 55.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        val photoFile = remember {
                            File.createTempFile("profile_", ".jpg", context.cacheDir)
                        }
                        val photoUri = androidx.core.content.FileProvider.getUriForFile(
                            context, "${context.packageName}.provider", photoFile
                        )

                        var imageUri   by remember { mutableStateOf<Uri?>(null) }
                        var showDialog by remember { mutableStateOf(false) }

                        val cameraLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.TakePicture()
                        ) { success -> if (success) imageUri = photoUri }

                        val galleryLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.GetContent()
                        ) { uri: Uri? -> imageUri = uri }

                        val permissionLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.RequestPermission()
                        ) { isGranted -> if (isGranted) cameraLauncher.launch(photoUri) }

                        Box(
                            modifier = Modifier
                                .size(116.dp)
                                .background(
                                    Brush.linearGradient(listOf(AccentViolet, PrimaryPurple)),
                                    CircleShape
                                )
                                .padding(3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(imageUri),
                                    contentDescription = null,
                                    modifier = Modifier.size(110.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.fl),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(CircleShape)
                                        .background(Color.White, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        IconButton(
                            onClick = { showDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .shadow(4.dp, CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(PrimaryPurple, AccentViolet)),
                                    CircleShape
                                )
                        ) {
                            Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }

                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                confirmButton = {},
                                containerColor = CardSurface,
                                shape = RoundedCornerShape(20.dp),
                                title = {
                                    Text("เลือกรูปภาพ", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable {
                                                    showDialog = false
                                                    if (ContextCompat.checkSelfPermission(
                                                            context, Manifest.permission.CAMERA
                                                        ) == PackageManager.PERMISSION_GRANTED
                                                    ) {
                                                        cameraLauncher.launch(photoUri)
                                                    } else {
                                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                                    }
                                                }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Icon(Icons.Default.CameraAlt, null, tint = PrimaryPurple)
                                            Text("กล้อง", color = TextPrimary, fontWeight = FontWeight.Medium)
                                        }
                                        HorizontalDivider(color = DividerColor)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable {
                                                    showDialog = false
                                                    galleryLauncher.launch("image/*")
                                                }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Icon(Icons.Default.Image, null, tint = PrimaryPurple)
                                            Text("คลังภาพ", color = TextPrimary, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(64.dp))
            }

            // ── Form Card ──

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "ข้อมูลส่วนตัว",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PrimaryPurple,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Username
                        ProfileFieldLabel("Username")
                        OutlinedTextField(
                            value = username, onValueChange = { username = it },
                            placeholder = { Text("กรอกชื่อผู้ใช้", color = TextSecondary, fontSize = 15.sp) },
                            textStyle = TextStyle(fontSize = 16.sp, color = TextPrimary),
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = PrimaryPurple) },
                            shape = RoundedCornerShape(14.dp),
                            colors = fieldColors,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                        )

                        // Email
                        ProfileFieldLabel("Email")
                        OutlinedTextField(
                            value = email, onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("email", color = TextSecondary, fontSize = 15.sp) },
                            textStyle = TextStyle(fontSize = 16.sp, color = TextSecondary),
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = PrimaryPurple) },
                            trailingIcon = {
                                Icon(Icons.Default.Lock, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = fieldColors,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                        )


//                        LaunchedEffect(interactionSource) {
//                            interactionSource.interactions.collect { interaction ->
//                                if (interaction is PressInteraction.Release) datePickerDialog.show()
//                            }
//                        }

                        // ShowDialog ResetPassword

                        if (showForgotDialog) {
                            AlertDialog(
                                onDismissRequest = {
                                    showForgotDialog = false
                                    resetEmail = ""
                                },
                                shape = RoundedCornerShape(20.dp),
                                title = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LockReset,
                                            contentDescription = null,
                                            tint = Color(0xFF6D9E51)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "รีเซ็ตรหัสผ่าน",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                },
                                text = {
                                    Column {
                                        Text(
                                            "กรอก Email ที่ใช้สมัครสมาชิก\nระบบจะส่งลิงก์สำหรับตั้งรหัสผ่านใหม่ให้",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray
                                        )

                                        Spacer(Modifier.height(16.dp))

                                        OutlinedTextField(
                                            value = resetEmail,
                                            onValueChange = { resetEmail = it },
                                            label = { Text("Email") },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Default.Email,
                                                    contentDescription = null
                                                )
                                            },
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Email
                                            ),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            authVM.resetPassword(resetEmail)
                                            showForgotDialog = false
                                        },
                                        enabled = resetEmail.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF6D9E51)
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("ส่งลิงก์รีเซ็ต")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            showForgotDialog = false
                                            resetEmail = ""
                                        }
                                    ) {
                                        Text(
                                            "ยกเลิก",
                                            color = Color.Gray
                                        )
                                    }
                                }
                            )
                        }


                        // Password
                        ProfileFieldLabel("รหัสผ่าน")
//                        OutlinedTextField(
//                            value = password, onValueChange = { password = it },
//                            placeholder = { Text("เปลี่ยนรหัสผ่านใหม่", color = TextSecondary, fontSize = 15.sp) },
//                            textStyle = TextStyle(fontSize = 16.sp, color = TextPrimary),
//                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = PrimaryPurple) },
//                            shape = RoundedCornerShape(14.dp),
//                            colors = fieldColors,
//                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
//                        )
                        Button(
                            onClick = {
                                showForgotDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(65.dp)
                                .padding(bottom = 10.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Purple700,
                                contentColor = Color.White
                            )

                        ){
                            Icon(
                                imageVector = Icons.Default.LockReset,
                                contentDescription = "ปุ่มการรีเซ็ต Password",
                                tint =  Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "เปลี่ยนรหัสผ่านใหม่",
//                                style = TextStyle(
//                                    brush = Brush.horizontalGradient(
//                                        colors = listOf(
//                                            Purple500,
//                                            Purple700,
//                                            Pink500
//                                        )
//                                    )
//                                )
                            )
                        }

                        // Phone
                        ProfileFieldLabel("เบอร์โทรศัพท์")
                        OutlinedTextField(
                            value = tel, onValueChange = { tel = it },
                            placeholder = { Text("กรอกเบอร์โทรศัพท์", color = TextSecondary, fontSize = 15.sp) },
                            textStyle = TextStyle(fontSize = 16.sp, color = TextPrimary),
                            leadingIcon = { Icon(Icons.Default.Phone, null, tint = PrimaryPurple) },
                            shape = RoundedCornerShape(14.dp),
                            colors = fieldColors,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                        )

                        // Sex Dropdown
                        ProfileFieldLabel("เพศ")
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = sex, onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("เลือกเพศ", color = TextSecondary, fontSize = 15.sp) },
                                textStyle = TextStyle(fontSize = 16.sp, color = TextPrimary),
                                leadingIcon = { Icon(Icons.Default.Wc, null, tint = PrimaryPurple) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                shape = RoundedCornerShape(14.dp),
                                colors = fieldColors,
                                modifier = Modifier.menuAnchor().fillMaxWidth().padding(bottom = 4.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(CardSurface)
                            ) {
                                sexOption.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = TextPrimary) },
                                        onClick = { sex = option; expanded = false }
                                    )
                                }
                            }

                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                userVM.saveProfile(
                                    email       = userEmail,
                                    name        = username,
                                    username    = username,
                                    phoneNumber = tel,
                                    sex         = sex
                                )
                                authVM.resetPassword(email)
                            },
                            enabled  = !isLoading,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape    = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                            colors   = ButtonDefaults.buttonColors(
                                containerColor         = PrimaryPurple,
                                contentColor           = Color.White,
                                disabledContainerColor = PrimaryPurple.copy(alpha = 0.5f)
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Save, null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("บันทึกข้อมูล", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }

                    }

                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Action Buttons ──
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // ── Store Info Card (แสดงเหนือปุ่มบันทึก เมื่อมีร้านค้า) ──
                    if (hasStore && myStore != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp, 20.dp)
                                        .background(PrimaryPurple, RoundedCornerShape(2.dp))
                                )
                                Text(
                                    "ข้อมูลร้านค้าของฉัน",
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 16.sp,
                                    color      = TextPrimary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF16A34A).copy(alpha = 0.12f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "เปิดร้านแล้ว",
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = Color(0xFF16A34A)
                                )
                            }
                        }

                        Card(
                            modifier  = Modifier.fillMaxWidth(),
                            shape     = RoundedCornerShape(20.dp),
                            colors    = CardDefaults.cardColors(containerColor = CardSurface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .background(
                                            Brush.linearGradient(listOf(DeepPurple, PrimaryPurple, AccentViolet))
                                        )
                                )
                                Column(
                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(SubtleGray)
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Tag, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                        Text(
                                            "Store ID: ${myStore.storeID.take(16)}...",
                                            fontSize = 12.sp,
                                            color    = TextSecondary
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .background(LightLavender, RoundedCornerShape(12.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Store, null, tint = PrimaryPurple, modifier = Modifier.size(22.dp))
                                        }
                                        Column {
                                            Text("ชื่อร้านค้า", fontSize = 16.sp, color = TextSecondary)
                                            Text(
                                                myStore.storeName.ifBlank { "-" },
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize   = 12.sp,
                                                color      = TextPrimary
                                            )
                                        }
                                    }

                                    HorizontalDivider(color = DividerColor, thickness = 1.dp)

                                    ProfileStoreInfoRow(
                                        icon  = Icons.Default.LocationOn,
                                        label = "ที่อยู่",
                                        value = myStore.location.ifBlank { "-" }
                                    )

                                    if (myStore.description.isNotBlank()) {
                                        ProfileStoreInfoRow(
                                            icon  = Icons.Default.Description,
                                            label = "รายละเอียด",
                                            value = myStore.description
                                        )
                                    }

                                    if (hasStore) {
                                        Button(
                                            onClick = { navController.navigate("AddFlowerScreen/$userEmail") },
                                            modifier = Modifier.fillMaxWidth().height(56.dp),
                                            shape    = RoundedCornerShape(16.dp),
                                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                                            colors   = ButtonDefaults.buttonColors(
                                                containerColor = PrimaryPurple,
                                                contentColor   = Color.White
                                            )
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(Icons.Default.Add, null, modifier = Modifier.size(36.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("เพิ่มดอกไม้", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Save Button ──

                    // ── ถ้ามีร้านแล้ว → โชว์ปุ่มเพิ่มดอกไม้ ──


                    if (!hasStore) {
                        OutlinedButton(
                            onClick = { showRegisterDialog = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp,
                                Brush.linearGradient(listOf(PrimaryPurple, AccentViolet))
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = LightLavender,
                                contentColor   = PrimaryPurple
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Store, null, tint = PrimaryPurple, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("สมัครเปิดร้านค้า", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryPurple)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        } // end LazyColumn
    } // end Scaffold
} // end ProfileScreen

// ── Helper Composables (private to this file) ──
@Composable
private fun ProfileFieldLabel(label: String) {
    Text(
        text = label,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
    )
}

@Composable
private fun ProfileStoreInfoRow(
    icon : androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(LightLavender, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = AccentViolet, modifier = Modifier.size(18.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, fontSize = 16.sp, color = TextSecondary)
            Text(value, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
        }
    }
}