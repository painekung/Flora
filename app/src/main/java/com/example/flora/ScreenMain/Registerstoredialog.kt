package com.example.flora.ScreenMain

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flora.Firebase.StoreState
import com.example.flora.Firebase.StoreViewModel

private val PrimaryPurple = Color(0xFF6B21A8)
private val DeepPurple    = Color(0xFF4C1D95)
private val AccentViolet  = Color(0xFF8B5CF6)
private val LightLavender = Color(0xFFF5F3FF)
private val CardSurface   = Color(0xFFFFFFFF)
private val SubtleGray    = Color(0xFFF8F7FA)
private val TextPrimary   = Color(0xFF1C1033)
private val TextSecondary = Color(0xFF6B7280)
private val SuccessGreen  = Color(0xFF16A34A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterStoreDialog(
    userEmail: String,
    storeVM  : StoreViewModel = viewModel(factory = StoreViewModel.Factory),
    onDismiss: () -> Unit
) {
    var storeName   by remember { mutableStateOf("") }
    var location    by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val storeState by storeVM.storeState.collectAsState()
    val isLoading  = storeState is StoreState.Loading
    val isSuccess  = storeState is StoreState.Success
    val errorMsg   = (storeState as? StoreState.Error)?.message

    // Auto dismiss หลัง success 1.8 วิ
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            kotlinx.coroutines.delay(1800)
            storeVM.resetStoreState()
            onDismiss()
        }
    }

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor   = LightLavender,
        unfocusedContainerColor = SubtleGray,
        disabledContainerColor  = SubtleGray,
        focusedIndicatorColor   = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedTextColor        = TextPrimary,
        unfocusedTextColor      = TextPrimary
    )

    Dialog(
        onDismissRequest = { if (!isLoading) { storeVM.resetStoreState(); onDismiss() } },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape     = RoundedCornerShape(28.dp),
            colors    = CardDefaults.cardColors(containerColor = CardSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                // ── Gradient Header ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .background(
                            Brush.linearGradient(listOf(DeepPurple, PrimaryPurple, AccentViolet))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .offset(x = 70.dp, y = (-35).dp)
                            .background(Color.White.copy(alpha = 0.06f), CircleShape)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Store, null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("สมัครเปิดร้านค้า", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                            Text("กรอกข้อมูลร้านของคุณ", fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f))
                        }
                    }
                }

                // ── Form ──
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    // ── Success State ──
                    if (isSuccess) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(SuccessGreen.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(40.dp))
                            }
                            Text("สมัครร้านค้าสำเร็จ!", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                            Text("ร้านของคุณถูกสร้างเรียบร้อยแล้ว", fontSize = 14.sp, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                        return@Column
                    }

                    // ── Error Banner ──
                    if (errorMsg != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFEE2E2))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                            Text(errorMsg, color = Color(0xFFDC2626), fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // ── Fields ──
                    DialogFieldLabel("ชื่อร้านค้า *")
                    OutlinedTextField(
                        value = storeName, onValueChange = { storeName = it },
                        placeholder = { Text("เช่น Flora Flower Shop", color = TextSecondary, fontSize = 14.sp) },
                        textStyle   = TextStyle(fontSize = 15.sp, color = TextPrimary),
                        leadingIcon = { Icon(Icons.Default.Store, null, tint = AccentViolet) },
                        shape = RoundedCornerShape(14.dp), colors = fieldColors, singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )

                    DialogFieldLabel("ที่อยู่ / สถานที่ *")
                    OutlinedTextField(
                        value = location, onValueChange = { location = it },
                        placeholder = { Text("เช่น กรุงเทพฯ, ลาดพร้าว", color = TextSecondary, fontSize = 14.sp) },
                        textStyle   = TextStyle(fontSize = 15.sp, color = TextPrimary),
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = AccentViolet) },
                        shape = RoundedCornerShape(14.dp), colors = fieldColors, singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )

                    DialogFieldLabel("รายละเอียดร้าน (ไม่บังคับ)")
                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        placeholder = { Text("แนะนำร้านของคุณสั้นๆ...", color = TextSecondary, fontSize = 14.sp) },
                        textStyle   = TextStyle(fontSize = 15.sp, color = TextPrimary),
                        leadingIcon = { Icon(Icons.Default.Description, null, tint = AccentViolet) },
                        shape = RoundedCornerShape(14.dp), colors = fieldColors,
                        minLines = 3, maxLines = 4,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )

                    // ── Buttons ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick  = { storeVM.resetStoreState(); onDismiss() },
                            enabled  = !isLoading,
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape    = RoundedCornerShape(14.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryPurple)
                        ) {
                            Text("ยกเลิก", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                        }

                        Button(
                            onClick = {
                                storeVM.registerStore(
                                    email       = userEmail,
                                    storeName   = storeName.trim(),
                                    location    = location.trim(),
                                    description = description.trim()
                                )
                            },
                            enabled  = storeName.isNotBlank() && location.isNotBlank() && !isLoading,
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape    = RoundedCornerShape(14.dp),
                            colors   = ButtonDefaults.buttonColors(
                                containerColor         = PrimaryPurple,
                                contentColor           = Color.White,
                                disabledContainerColor = PrimaryPurple.copy(alpha = 0.4f)
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text("สมัครเลย", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun DialogFieldLabel(label: String) {
    Text(
        text = label,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 13.sp,
        color      = TextSecondary,
        modifier   = Modifier.padding(bottom = 4.dp, start = 2.dp)
    )
}