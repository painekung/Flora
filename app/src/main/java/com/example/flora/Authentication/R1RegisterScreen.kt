package com.example.flora.Authentication

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(modifier: Modifier = Modifier,navController: NavController){
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }


    val sexOption = listOf("Male","Female","Other")
    var expanded  by remember { mutableStateOf(false) }
    var sex by remember { mutableStateOf("") }


    // BirthDay
    var birthday by remember { mutableStateOf("") }

    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                birthday = "$dayOfMonth/${month + 1}/$year"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis() // กันเลือกวันอนาคต
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xFF6400B2).copy(alpha = 0.15f),
                        0.15f to Color(0xFFEBD9FF),
                        0.6f to Color.White,
                        1.0f to Color.White
                    )
                )
            )
    ) {
        Column( //Column Main
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)

        ) {
            Row( // Row Back Home
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    shape = RoundedCornerShape(10.dp),   // ความโค้ง
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6400B2), // สีพื้นหลัง
                        contentColor = Color.White  // สีตัวอักษร
                    ),
                    modifier = Modifier.size(52.dp),     // ขนาดสี่เหลี่ยม
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            } // End Row Back Home
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Row( // Row Title Create Account
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Sign Up",
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))

                // Username
                OutlinedTextField( // Enter Username
                    value = username,
                    onValueChange = {username = it } ,
                    placeholder = {Text("Username")},
                    textStyle = TextStyle(
                        fontSize = 23.sp
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    shape = RoundedCornerShape(10.dp), // 👈 ปรับตรงนี้
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) // End Enter Username


                // Email
                OutlinedTextField( // Enter Email
                    value = email,
                    onValueChange = {email = it } ,
                    placeholder = {Text("Email")},
                    textStyle = TextStyle(
                        fontSize = 23.sp
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )
                    },
                    shape = RoundedCornerShape(10.dp), // 👈 ปรับตรงนี้
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) // End Enter Email


                // Birthday
                OutlinedTextField(
                    value = birthday,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Birthday") },
                    textStyle = TextStyle(fontSize = 23.sp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    interactionSource = interactionSource
                )

                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect { interaction ->
                        if (interaction is PressInteraction.Release) {
                            datePickerDialog.show()
                        }
                    }
                }
                // End Birthday


                // Password
                OutlinedTextField( // Enter Password
                    value = password,
                    onValueChange = {password = it } ,
                    placeholder = {Text("Password")},
                    textStyle = TextStyle(
                        fontSize = 23.sp
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    shape = RoundedCornerShape(10.dp), // 👈 ปรับตรงนี้
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) // End Enter Password

                // Confirm Password
                OutlinedTextField( // Enter Confirm Password
                    value = confirmPassword,
                    onValueChange = {confirmPassword = it } ,
                    placeholder = {Text("Confirm Password")},
                    textStyle = TextStyle(
                        fontSize = 23.sp
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    shape = RoundedCornerShape(10.dp), // 👈 ปรับตรงนี้
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) // End Enter Confirm Password


                // Sex
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {

                    OutlinedTextField(
                        value = sex,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select Sex") },
                        textStyle = TextStyle(fontSize = 23.sp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        sexOption.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    sex = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                // End Sex


                // --------------- Button Next ----------------- //
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF6400B2),   // ม่วงหลัก
                                    Color(0xFF9C27B0)    // ม่วงชมพูอ่อน
                                )
                            )
                        )
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = {
                            navController.navigate("registerTwo")
                        },
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            text = "Next",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                    }
                }

            } // End Column Create Account


        } // End Column Main
    }
}
