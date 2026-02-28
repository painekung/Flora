package com.example.flora.Authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flora.R

@Composable
fun LoginScreen(modifier: Modifier = Modifier,navController: NavController){

    // varible Var
    var username by remember{ mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var checked by remember { mutableStateOf(false) }

    // Varible Val

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
        // Layout Logo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.logo_no_bg_flora),
                contentDescription = null
            )
        } // End Layout Logo


        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sign In",
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            )
        }

        // Column Enter User and Password
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            Text(
                text = "Username",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(1.dp))

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

            Spacer(modifier = Modifier.height(10.dp))


            Text(
                text = "Password",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(1.dp))

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
                trailingIcon = {
                    val image = if(passwordVisible)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff
                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {
                        Icon(
                            imageVector = image,
                            contentDescription = null
                        )
                    }

                },
                visualTransformation = if(passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                shape = RoundedCornerShape(10.dp), // 👈 ปรับตรงนี้
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) // End Enter Password

        }// End Column Enter User and Password

        // --------------- Button Remember and Forget Password? ----------------- //
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal  = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Switch(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    modifier = Modifier.size(width = 52.dp, height = 32.dp),
                    colors = SwitchDefaults.colors(

                        // ตอนเปิด
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF6400B2),      // ชมพูดอกไม้
                        checkedBorderColor = Color(0xFF6400B2),

                        // ตอนปิด
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFEBD9FF),    // ชมพูอ่อนละมุน
                        uncheckedBorderColor = Color(0xFFD8BFD8)    // lavender อ่อน
                    )
                )

                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (checked) "Not Remember" else "Remember",
                    modifier = Modifier.padding(end = 8.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF6A1B9A),   // ม่วงเข้มหวาน ๆ
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            TextButton(
                onClick = { }
            ) {
                Text(
                    text = "Forgot password?",
                    fontSize = 16.sp,
                    color = Color(0xFFE91E63) // โทนชมพูแอปดอกไม้
                )
            }
        } // End Row


        // --------------- Button Sign In ----------------- //
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
                .clickable {
                    navController.navigate("MainScreen")
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sign In",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        // --------------- Button Register ----------------- //
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account?"
            )
            Spacer(modifier = Modifier.width(3.dp))
            TextButton(
                onClick = {
                    navController.navigate("register"){
                        launchSingleTop = true  // ป้องกันการสร้างหน้าซ้ำ
                    }
                }
            ) {
                Text(
                    text = "Register",
                    fontSize = 16.sp,
                    color = Color(0xFFE91E63) // โทนชมพูแอปดอกไม้

                )
            }
        }


    } // End Column Main
}
