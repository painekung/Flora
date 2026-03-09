package com.example.flora.Authentication

import android.app.DatePickerDialog
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import java.util.Calendar

private val BloomRose     = Color(0xFFE8556D)
private val BloomDeepRose = Color(0xFFC23556)
private val BloomGreen    = Color(0xFF4A7C59)
private val BloomCream    = Color(0xFFFDF6F0)
private val BloomGray     = Color(0xFF8A7F88)
private val BloomDark     = Color(0xFF2D1F2E)
private val BloomBorder   = Color(0xFFEDD5DC)
private val BgStart       = Color(0xFFFFF0F6)
private val BgMid         = Color(0xFFFDF2FF)
private val BgEnd         = Color(0xFFF0F7FF)
private val Purple700     = Color(0xFF7C3AED)
private val Purple500     = Color(0xFFA855F7)
private val Pink500       = Color(0xFFEC4899)

@Composable
private fun FloraField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        placeholder = { Text(placeholder, color = BloomGray.copy(alpha = 0.7f), fontSize = 15.sp) },
        textStyle = TextStyle(fontSize = 15.sp, color = BloomDark),
        leadingIcon = { Icon(icon, null, tint = Purple700, modifier = Modifier.size(20.dp)) },
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Purple700,
            unfocusedBorderColor = BloomBorder,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = BloomRose
        ),
        modifier = modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(14.dp),
            ambientColor = BloomRose.copy(alpha = 0.08f), spotColor = BloomRose.copy(alpha = 0.08f))
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = BloomGreen,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authVM: AuthViewModel
) {
    var fullName        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var phone           by remember { mutableStateOf("") }
    //var birthday        by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var address         by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible  by remember { mutableStateOf(false) }

    val sexOptions = listOf("Female", "Male", "Other")
    var sexExpanded by remember { mutableStateOf(false) }
    var sex         by remember { mutableStateOf("") }

    val authState by authVM.authState.collectAsState()
    val context   = LocalContext.current

    // Navigate เมื่อ register สำเร็จ
    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            navController.navigate("MainScreen") {
                popUpTo("register") { inclusive = true }
                popUpTo("login")    { inclusive = true }
            }
            authVM.resetState()
        }
    }

//    val interactionSource = remember { MutableInteractionSource() }
//    val calendar = Calendar.getInstance()
//    val datePickerDialog = remember {
//        DatePickerDialog(
//            context,
//            { _, year, month, day -> birthday = "$day/${month + 1}/$year" },
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//        ).apply { datePicker.maxDate = System.currentTimeMillis() }
//    }
//
//    LaunchedEffect(interactionSource) {
//        interactionSource.interactions.collect { interaction ->
//            if (interaction is PressInteraction.Release) datePickerDialog.show()
//        }
//    }

    Box(
        modifier = modifier.fillMaxSize().background(
            Brush.verticalGradient(colorStops = arrayOf(0.0f to BgStart, 0.4f to BgMid, 1.0f to BgEnd))
        )
    ) {
        // Decorative blobs
        Box(modifier = Modifier.size(260.dp).offset(x = (-60).dp, y = (-40).dp)
            .background(Brush.radialGradient(listOf(BloomRose.copy(alpha = 0.18f), Color.Transparent)), CircleShape))
        Box(modifier = Modifier.size(180.dp).align(Alignment.TopEnd).offset(x = 60.dp, y = 20.dp)
            .background(Brush.radialGradient(listOf(BloomGreen.copy(alpha = 0.12f), Color.Transparent)), CircleShape))

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                        .background(Color.White).border(1.dp, BloomBorder, RoundedCornerShape(12.dp))
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = BloomDark, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(BloomCream),
                    contentAlignment = Alignment.Center
                ) { Text("✿", fontSize = 18.sp) }
            }

            // Hero Text
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text("Create Account", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = BloomDark, letterSpacing = (-0.5).sp)
                Spacer(Modifier.height(4.dp))
                Text("Hi, Enjoy your Life 🌸", fontSize = 14.sp, color = BloomGray, lineHeight = 20.sp)
            }

            Spacer(Modifier.height(28.dp))

            // Form Card
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(24.dp)).background(Color.White)
                    .border(1.dp, BloomBorder, RoundedCornerShape(24.dp)).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionLabel("PERSONAL INFO")

                FloraField(value = fullName, onValueChange = { fullName = it },
                    placeholder = "User name", icon = Icons.Outlined.Person)

                FloraField(value = email, onValueChange = { email = it },
                    placeholder = "Email Address", icon = Icons.Outlined.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))

                FloraField(value = phone, onValueChange = { phone = it },
                    placeholder = "Phone Number", icon = Icons.Outlined.Phone,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))

                // Birthday + Sex
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                    // Birthday
//                    FloraField(
//                        value = birthday, onValueChange = {}, placeholder = "Birthday",
//                        icon = Icons.Outlined.DateRange, readOnly = true,
//                        interactionSource = interactionSource, modifier = Modifier.weight(1f)
//                    )

                    ExposedDropdownMenuBox(
                        expanded = sexExpanded,
                        onExpandedChange = { sexExpanded = !sexExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = sex, onValueChange = {}, readOnly = true,
                            placeholder = { Text("Sex", color = BloomGray.copy(alpha = 0.7f), fontSize = 15.sp) },
                            textStyle = TextStyle(fontSize = 15.sp, color = BloomDark),
                            leadingIcon = { Icon(Icons.Outlined.People, null, tint = Purple700, modifier = Modifier.size(20.dp)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexExpanded) },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BloomRose, unfocusedBorderColor = BloomBorder,
                                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                            ),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(14.dp))
                        )
                        ExposedDropdownMenu(expanded = sexExpanded, onDismissRequest = { sexExpanded = false },
                            modifier = Modifier.background(Color.White)) {
                            sexOptions.forEach { option ->
                                DropdownMenuItem(text = { Text(option, color = BloomDark) },
                                    onClick = { sex = option; sexExpanded = false })
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
                SectionLabel("DELIVERY ADDRESS")

                OutlinedTextField(
                    value = address, onValueChange = { address = it },
                    placeholder = {
                        Text("Enter your full delivery address\n(house no., street, sub-district, district, province, postal code)",
                            color = BloomGray.copy(alpha = 0.7f), fontSize = 14.sp, lineHeight = 20.sp)
                    },
                    textStyle = TextStyle(fontSize = 15.sp, color = BloomDark, lineHeight = 22.sp),
                    leadingIcon = {
                        Icon(Icons.Outlined.Home, null, tint = Purple700,
                            modifier = Modifier.size(20.dp).offset(y = (-32).dp))
                    },
                    singleLine = false, maxLines = 5, minLines = 4,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple700, unfocusedBorderColor = BloomBorder,
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, cursorColor = Purple700
                    ),
                    modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(14.dp))
                )

                Spacer(Modifier.height(4.dp))
                SectionLabel("SECURITY")

                FloraField(
                    value = password, onValueChange = { password = it },
                    placeholder = "Password", icon = Icons.Outlined.Lock,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                null, tint = BloomGray, modifier = Modifier.size(18.dp))
                        }
                    }
                )

                FloraField(
                    value = confirmPassword, onValueChange = { confirmPassword = it },
                    placeholder = "Confirm Password", icon = Icons.Outlined.Lock,
                    visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmVisible = !confirmVisible }) {
                            Icon(if (confirmVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                null, tint = BloomGray, modifier = Modifier.size(18.dp))
                        }
                    }
                )

                if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                    Text("⚠ Passwords do not match", fontSize = 12.sp, color = BloomRose, modifier = Modifier.padding(start = 4.dp))
                }

                // Error message
                if (authState is AuthViewModel.AuthState.Error) {
                    Text(
                        text = (authState as AuthViewModel.AuthState.Error).message,
                        color = Color.Red, fontSize = 13.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Loading
            if (authState is AuthViewModel.AuthState.Loading) {
                CircularProgressIndicator(
                    color = Purple500,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 12.dp)
                )
            }

            // Register Button
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.horizontalGradient(listOf(Purple500, Purple700, Pink500)))
                    .clickable {
                        if (email.isNotBlank() && password.isNotBlank() && password == confirmPassword) {
                            authVM.register(
                                email    = email,
                                password = password,
                                name     = fullName,
                                phone    = phone,
                                sex      = sex
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Create Account", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp)
                    Text("✦", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Sign In link
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account? ", fontSize = 14.sp, color = BloomGray)
                Text("Sign In", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BloomRose,
                    modifier = Modifier.clickable { navController.popBackStack() })
            }

            Spacer(Modifier.height(36.dp))
        }
    }
}