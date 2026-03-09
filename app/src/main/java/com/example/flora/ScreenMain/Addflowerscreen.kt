package com.example.flora.ScreenMain

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.flora.Firebase.Flowers
import com.example.flora.Firebase.FlowersViewModel
import com.example.flora.Firebase.InsertState
import com.example.flora.Firebase.StoreEmbedded
import com.example.flora.Firebase.Stores
import com.example.flora.Firebase.StoreViewModel
import com.example.flora.R

private val Primary      = Color(0xFF6400B2)
private val PrimaryLight = Color(0xFF820DDD)
private val PrimaryFaint = Color(0xFFF3F0FA)
private val BgWhite      = Color(0xFFF9F7FF)
private val CardWhite    = Color(0xFFFFFFFF)
private val BorderPurple = Color(0xFFD4CCE3)
private val TextDark     = Color(0xFF1A1A2E)
private val TextGray     = Color(0xFF6E6A7C)
private val ErrorColor   = Color(0xFFE53935)
private val SuccessColor = Color(0xFF2E7D32)

private val HeaderGradient = Brush.verticalGradient(listOf(Color(0xFF6400B2), Color(0xFF820DDD)))

data class FlowerPreset(
    val imageRes     : Int,
    val imageName    : String,
    val flowerID     : String,
    val name         : String,
    val category     : String,
    val categoryColor: String,
    val color        : String,
    val colorBtn     : String,
    val price        : String,
    val desc         : String
)

private val flowerPresets = listOf(
    FlowerPreset(R.drawable.fl,   "fl.png",   "F01", "ดอกลาเวนเดอร์มิกซ์", "พรีเมียม",     "#AB47BC", "#EDE7F6", "#6400B2", "650", "ช่อดอกลาเวนเดอร์ผสมดอกไม้หลากสี กลิ่นหอมอ่อนๆ เหมาะสำหรับของขวัญพิเศษ"),
    FlowerPreset(R.drawable.fl1,  "fl1.png",  "F02", "ดอกกุหลาบฟ้า",       "พรีเมียม",     "#1E88E5", "#E3F2FD", "#1565C0", "750", "ดอกกุหลาบสีฟ้าหายาก นำเข้าพิเศษ สัญลักษณ์แห่งความลึกลับและความโรแมนติก"),
    FlowerPreset(R.drawable.fl2,  "fl2.png",  "F03", "ดอกทานตะวันทอง",     "ยอดนิยม",      "#FDD835", "#FFFDE7", "#F57F17", "400", "ช่อดอกทานตะวันสีเหลืองทอง สดใส ให้ความรู้สึกอบอุ่นและมีชีวิตชีวา"),
    FlowerPreset(R.drawable.fl3,  "fl3.png",  "F04", "ดอกกุหลาบชมพู",      "ยอดนิยม",      "#FF006E", "#FCE4EC", "#C2185B", "550", "ดอกกุหลาบชมพูหวานละมุน เหมาะสำหรับวันวาเลนไทน์และโอกาสพิเศษต่างๆ"),
    FlowerPreset(R.drawable.fl4,  "fl4.png",  "F05", "ช่อดอกมิกซ์พาสเทล", "ตามฤดูกาล",   "#AB47BC", "#F8F0FF", "#7B1FA2", "480", "ช่อดอกไม้ผสมโทนพาสเทล สวยงาม เหมาะสำหรับตกแต่งและเป็นของขวัญ"),
    FlowerPreset(R.drawable.fl5,  "fl5.png",  "F06", "ดอกไม้สีฟ้าพาสเทล", "ตามฤดูกาล",   "#1E88E5", "#BBDEFB", "#1976D2", "520", "ช่อดอกไม้โทนฟ้าอ่อน ให้ความรู้สึกสดชื่นและเบาสบาย เหมาะกับทุกโอกาส"),
    FlowerPreset(R.drawable.fl6,  "fl6.png",  "F07", "ดอกออร์คิดขาว",      "พรีเมียม",     "#AB47BC", "#FAF0E6", "#4A148C", "890", "ดอกออร์คิดขาวบริสุทธิ์ สง่างาม เหมาะสำหรับงานพิธีและของขวัญผู้ใหญ่"),
    FlowerPreset(R.drawable.fl7,  "fl7.png",  "F08", "ดอกกุหลาบชมพูอ่อน", "ยอดนิยม",      "#FF006E", "#FFF0F5", "#AD1457", "600", "ดอกกุหลาบชมพูอ่อนนุ่ม กลิ่นหอมอ่อนๆ เหมาะสำหรับวันเกิดและครบรอบ"),
    FlowerPreset(R.drawable.fl8,  "fl8.png",  "F09", "ดอกไม้ฟ้าคราม",      "ราคาประหยัด", "#1E88E5", "#E8EAF6", "#283593", "350", "ช่อดอกไม้โทนฟ้าคราม ดูสง่างามในราคาย่อมเยา เหมาะสำหรับทุกโอกาส"),
    FlowerPreset(R.drawable.fl9,  "fl9.png",  "F10", "ดอกเพิร์ชพาสเทล",   "ตามฤดูกาล",   "#FFB74D", "#FFF8E1", "#E65100", "430", "ช่อดอกไม้โทนพีชอ่อนๆ ให้ความรู้สึกอบอุ่นและหวาน เหมาะกับฤดูใบไม้ผลิ"),
    FlowerPreset(R.drawable.fl10, "fl10.png", "F11", "ดอกเดซี่มิกซ์",       "ราคาประหยัด", "#FDD835", "#FFFFF0", "#827717", "290", "ช่อดอกเดซี่หลากสี สดใสร่าเริง ราคาเป็นมิตร เหมาะสำหรับของขวัญทั่วไป")
)

private fun parseColor(hex: String, fallback: Color = BorderPurple): Color {
    if (hex.isBlank()) return fallback
    return try {
        Color(android.graphics.Color.parseColor(if (hex.startsWith("#")) hex else "#$hex"))
    } catch (e: Exception) { fallback }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlowerScreen(
    vm            : FlowersViewModel = viewModel(factory = FlowersViewModel.Factory),
    storeVm       : StoreViewModel   = viewModel(factory = StoreViewModel.Factory),
    ownerEmail    : String           = "aa@example.com",
    onNavigateBack: () -> Unit       = {}
) {
    LaunchedEffect(Unit) { storeVm.fetchStoreByEmail(ownerEmail) }
    val allStores by storeVm.currentStore.collectAsState()

    var selectedStore  by remember { mutableStateOf<Stores?>(null) }
    var storeExpanded  by remember { mutableStateOf(false) }
    var selectedPreset by remember { mutableStateOf<FlowerPreset?>(null) }

    LaunchedEffect(allStores) {
        if (allStores.size == 1 && selectedStore == null) selectedStore = allStores.first()
    }

    var flowerID      by remember { mutableStateOf("") }
    var name          by remember { mutableStateOf("") }
    var category      by remember { mutableStateOf("") }
    var categoryColor by remember { mutableStateOf("") }
    var color         by remember { mutableStateOf("") }
    var colorBtn      by remember { mutableStateOf("") }
    var price         by remember { mutableStateOf("") }
    var storeID       by remember { mutableStateOf("") }
    var image         by remember { mutableStateOf("") }
    var desc          by remember { mutableStateOf("") }

    var categoryExpanded      by remember { mutableStateOf(false) }
    var categoryColorExpanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val insertState by vm.insertState.collectAsState()
    val isLoading   = insertState is InsertState.Loading
    val errorMsg    = (insertState as? InsertState.Error)?.message

    val onReset: () -> Unit = remember {{
        flowerID = ""; name = ""; category = ""; categoryColor = ""
        color = ""; colorBtn = ""; price = ""; storeID = ""
        image = ""; desc = ""; selectedStore = null; selectedPreset = null
    }}

    LaunchedEffect(insertState) {
        when (insertState) {
            is InsertState.Success -> {
                snackbarHostState.showSnackbar("✅ บันทึกข้อมูลสำเร็จ!")
                onReset()
                vm.resetInsertState()
            }
            is InsertState.Error -> {
                snackbarHostState.showSnackbar("❌ ${(insertState as InsertState.Error).message}")
                vm.resetInsertState()
            }
            else -> Unit
        }
    }

    val categories = remember { listOf("ยอดนิยม", "ตามฤดูกาล", "พรีเมียม", "ราคาประหยัด") }
    val categoryColorOptions = remember {
        listOf(
            "เหลือง" to "#FDD835", "ม่วง" to "#AB47BC", "แดง" to "#EF5350",
            "น้ำเงิน" to "#1E88E5", "ฟ้า" to "#BBDEFB", "ขาว" to "#FAF0E6",
            "ส้ม" to "#FFB74D", "ชมพู" to "#FF006E"
        )
    }

    // ── parse สีแค่เมื่อ string เปลี่ยน ──
    val parsedColor      = remember(color)         { parseColor(color) }
    val parsedColorBtn   = remember(colorBtn)      { parseColor(colorBtn) }
    val colorValid       = remember(color)         { color.isNotBlank() && parsedColor != BorderPurple }
    val colorBtnValid    = remember(colorBtn)      { colorBtn.isNotBlank() && parsedColorBtn != BorderPurple }
    val parsedCatColor   = remember(categoryColor) { parseColor(categoryColor) }
    val selectedCatLabel = remember(categoryColor, categoryColorOptions) {
        categoryColorOptions.find { it.second == categoryColor }?.first ?: ""
    }

    Scaffold(
        containerColor = BgWhite,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(snackbarData = data, containerColor = Primary, contentColor = Color.White, shape = RoundedCornerShape(14.dp))
            }
        }
    ) { innerPadding ->

        // ══════════════════════════════════════════
        // แก้ไขหลัก: Column+verticalScroll → LazyColumn
        // เหตุผล: verticalScroll วาด children ทั้งหมดพร้อมกัน
        // รวมกับ shadow()+offset()+clip() 3 ชั้น = หนักมากระหว่าง scroll
        // LazyColumn วาดเฉพาะ item ที่อยู่บนหน้าจอ
        // ══════════════════════════════════════════
        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {

            // ══ HEADER ══
            item(key = "header") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(HeaderGradient)
                ) {
                    IconButton(
                        onClick  = onNavigateBack,
                        modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
                            .size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                        Text("เพิ่มดอกไม้ใหม่", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("เลือกภาพดอกไม้เพื่อกรอกข้อมูลอัตโนมัติ", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            // ══ CARD 0 — เลือกภาพ ══
            item(key = "card_image") {
                SectionCard(topPadding = 12.dp) {
                    AppSectionLabel("📷", "เลือกภาพดอกไม้")
                    Spacer(Modifier.height(4.dp))
                    Text("กดที่ภาพเพื่อกรอกข้อมูลอัตโนมัติ", fontSize = 12.sp, color = TextGray, modifier = Modifier.padding(bottom = 14.dp))

                    flowerPresets.chunked(5).forEachIndexed { rowIdx, rowItems ->
                        if (rowIdx > 0) Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowItems.forEach { preset ->
                                key(preset.flowerID) {
                                    FlowerPresetCell(
                                        preset     = preset,
                                        isSelected = selectedPreset?.imageName == preset.imageName,
                                        onSelect   = {
                                            selectedPreset = preset
                                            flowerID = preset.flowerID; name = preset.name
                                            category = preset.category; categoryColor = preset.categoryColor
                                            color = preset.color; colorBtn = preset.colorBtn
                                            price = preset.price; image = preset.imageName; desc = preset.desc
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            repeat(5 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }

                    AnimatedVisibility(visible = selectedPreset != null, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                        selectedPreset?.let { p ->
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                    .background(Primary.copy(alpha = 0.08f)).padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = Primary, modifier = Modifier.size(16.dp))
                                Text("เลือกแล้ว: ${p.name}", fontSize = 12.sp, color = Primary, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // ══ CARD 1 — ข้อมูลหลัก ══
            item(key = "card_main") {
                SectionCard {
                    Text("ข้อมูลหลัก")
                    Spacer(Modifier.height(14.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppField(Modifier.weight(1f),   flowerID, { flowerID = it }, "Flower ID *",  "F03",        Icons.Outlined.Tag)
                        AppField(Modifier.weight(1.6f), name,     { name = it },     "ชื่อดอกไม้ *", "ดอกฟ้าคราม", Icons.Outlined.LocalFlorist)
                    }
                    Spacer(Modifier.height(14.dp))

                    AppFieldLabel("หมวดหมู่ *")
                    ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                        OutlinedTextField(
                            value = category, onValueChange = {}, readOnly = true,
                            placeholder  = { Text("เลือกหมวดหมู่", color = TextGray, fontSize = 14.sp) },
                            leadingIcon  = { Icon(Icons.Outlined.Category, null, tint = Primary, modifier = Modifier.size(18.dp)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), colors = appFieldColors()
                        )
                        ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }, modifier = Modifier.background(CardWhite)) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text         = { Text(cat, color = TextDark, fontSize = 14.sp) },
                                    onClick      = { category = cat; categoryExpanded = false },
                                    trailingIcon = { if (category == cat) Icon(Icons.Default.Check, null, tint = Primary, modifier = Modifier.size(16.dp)) }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))

                    AppFieldLabel("สี Category")
                    ExposedDropdownMenuBox(expanded = categoryColorExpanded, onExpandedChange = { categoryColorExpanded = it }) {
                        OutlinedTextField(
                            value = selectedCatLabel, onValueChange = {}, readOnly = true,
                            placeholder  = { Text("เลือกสีหมวดหมู่", color = TextGray, fontSize = 14.sp) },
                            leadingIcon  = {
                                Box(Modifier.padding(start = 12.dp).size(22.dp).clip(RoundedCornerShape(6.dp))
                                    .background(parsedCatColor).border(1.dp, BorderPurple, RoundedCornerShape(6.dp)))
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryColorExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), colors = appFieldColors()
                        )
                        ExposedDropdownMenu(expanded = categoryColorExpanded, onDismissRequest = { categoryColorExpanded = false }, modifier = Modifier.background(CardWhite)) {
                            categoryColorOptions.forEach { (label, hex) ->
                                val optColor = remember(hex) { parseColor(hex, Color.Gray) }
                                DropdownMenuItem(
                                    text         = { Text(label, color = TextDark, fontSize = 14.sp) },
                                    onClick      = { categoryColor = hex; categoryColorExpanded = false },
                                    leadingIcon  = { Box(Modifier.size(18.dp).clip(CircleShape).background(optColor).border(1.dp, BorderPurple, CircleShape)) },
                                    trailingIcon = { if (categoryColor == hex) Icon(Icons.Default.Check, null, tint = Primary, modifier = Modifier.size(16.dp)) }
                                )
                            }
                        }
                    }
                }
            }

            // ══ CARD 2 — รายละเอียด ══
            item(key = "card_detail") {
                SectionCard {
                   Text("รายละเอียด")
                    Spacer(Modifier.height(14.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            AppFieldLabel("สีพื้นหลัง")
                            OutlinedTextField(
                                value = color, onValueChange = { color = it },
                                placeholder = { Text("#A8D5BA", color = TextGray, fontSize = 14.sp) },
                                leadingIcon = { Box(Modifier.padding(start = 12.dp).size(22.dp).clip(RoundedCornerShape(6.dp)).background(parsedColor).border(1.dp, BorderPurple, RoundedCornerShape(6.dp))) },
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true, colors = appFieldColors()
                            )
                            when {
                                colorValid         -> Text("✓ สีถูกต้อง",       fontSize = 11.sp, color = SuccessColor, modifier = Modifier.padding(top = 4.dp, start = 4.dp))
                                color.isNotBlank() -> Text("รูปแบบ: #RRGGBB", fontSize = 11.sp, color = ErrorColor,   modifier = Modifier.padding(top = 4.dp, start = 4.dp))
                            }
                        }
                        Column(Modifier.weight(1f)) {
                            AppFieldLabel("สีปุ่ม")
                            OutlinedTextField(
                                value = colorBtn, onValueChange = { colorBtn = it },
                                placeholder = { Text("#2E7D52", color = TextGray, fontSize = 14.sp) },
                                leadingIcon = { Box(Modifier.padding(start = 12.dp).size(22.dp).clip(RoundedCornerShape(6.dp)).background(parsedColorBtn).border(1.dp, BorderPurple, RoundedCornerShape(6.dp))) },
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true, colors = appFieldColors()
                            )
                            when {
                                colorBtnValid         -> Text("✓ สีถูกต้อง",       fontSize = 11.sp, color = SuccessColor, modifier = Modifier.padding(top = 4.dp, start = 4.dp))
                                colorBtn.isNotBlank() -> Text("รูปแบบ: #RRGGBB", fontSize = 11.sp, color = ErrorColor,   modifier = Modifier.padding(top = 4.dp, start = 4.dp))
                            }
                        }
                    }

                    AnimatedVisibility(visible = colorValid || colorBtnValid) {
                        Box(
                            modifier = Modifier.padding(top = 12.dp).fillMaxWidth().height(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (colorValid) parsedColor else PrimaryFaint)
                                .border(1.dp, BorderPurple, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("🌸", fontSize = 20.sp)
                                Box(Modifier.clip(RoundedCornerShape(8.dp)).background(if (colorBtnValid) parsedColorBtn else Primary).padding(horizontal = 16.dp, vertical = 6.dp)) {
                                    Text("ตัวอย่างปุ่ม", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    AppField(Modifier.fillMaxWidth(), price, { price = it.filter { c -> c.isDigit() } }, "ราคา (บาท) *", "400", Icons.Outlined.Sell, KeyboardType.Number)
                    Spacer(Modifier.height(14.dp))

                    AppFieldLabel("ร้านค้า *")
                    ExposedDropdownMenuBox(expanded = storeExpanded, onExpandedChange = { storeExpanded = it }, modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedStore?.storeName ?: "", onValueChange = {}, readOnly = true,
                            placeholder  = { Text("เลือกร้านค้า", color = TextGray, fontSize = 14.sp) },
                            leadingIcon  = { Icon(Icons.Outlined.Store, null, tint = Primary, modifier = Modifier.size(18.dp)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(storeExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), colors = appFieldColors()
                        )
                        ExposedDropdownMenu(expanded = storeExpanded, onDismissRequest = { storeExpanded = false }, modifier = Modifier.background(CardWhite)) {
                            if (allStores.isEmpty()) {
                                DropdownMenuItem(text = { Text("กำลังโหลด...", color = TextGray) }, onClick = {})
                            } else {
                                allStores.forEach { store ->
                                    DropdownMenuItem(
                                        text = { Column { Text(store.storeName, color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.Medium); Text(store.location, color = TextGray, fontSize = 11.sp) } },
                                        onClick      = { selectedStore = store; storeID = store.storeID; storeExpanded = false },
                                        leadingIcon  = { Icon(Icons.Outlined.Store, null, tint = if (selectedStore?.storeID == store.storeID) Primary else TextGray, modifier = Modifier.size(16.dp)) },
                                        trailingIcon = { if (selectedStore?.storeID == store.storeID) Icon(Icons.Default.Check, null, tint = Primary, modifier = Modifier.size(16.dp)) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    AppField(Modifier.fillMaxWidth(), image, { image = it }, "ชื่อไฟล์รูปภาพ", "fl1.png", Icons.Outlined.Image)
                    Spacer(Modifier.height(14.dp))

                    AppFieldLabel("คำอธิบาย")
                    OutlinedTextField(
                        value = desc, onValueChange = { desc = it },
                        placeholder = { Text("รายละเอียดดอกไม้...", color = TextGray, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(12.dp), maxLines = 4, colors = appFieldColors()
                    )
                }
            }

            // ══ ERROR + BUTTONS ══
            item(key = "card_buttons") {
                Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp)) {
                    AnimatedVisibility(visible = errorMsg != null, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFEBEE)).border(1.dp, ErrorColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)).padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, null, tint = ErrorColor, modifier = Modifier.size(16.dp))
                            Text(errorMsg ?: "", color = ErrorColor, fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = onReset, modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(14.dp), border = BorderStroke(1.5.dp, BorderPurple),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray)
                        ) {
                            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("รีเซ็ต", fontSize = 14.sp)
                        }
                        Button(
                            onClick = {
                                vm.insertFlower(
                                    Flowers(
                                        flowerID = flowerID.trim(), name = name.trim(),
                                        category = category.trim(), categoryColor = categoryColor.trim(),
                                        color = color.trim(), colorBtn = colorBtn.trim(),
                                        price = price.toIntOrNull() ?: 0,
                                        store = StoreEmbedded(
                                            storeID = selectedStore?.storeID ?: "",
                                            storeName = selectedStore?.storeName ?: "",
                                            location = selectedStore?.location ?: ""
                                        ),
                                        image = image.trim(), desc = desc.trim(), rating = 0.0
                                    )
                                )
                            },
                            enabled = !isLoading, modifier = Modifier.weight(2f).height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White, disabledContainerColor = Primary.copy(alpha = 0.4f))
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.CloudUpload, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("บันทึก", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── SectionCard: ใช้ Card elevation แทน Modifier.shadow()+offset()+clip() ──
// ── ลด GPU overdraw จาก 3 pass → 1 pass ──
@Composable
private fun SectionCard(
    topPadding: Dp = 8.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = topPadding),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), content = content)
    }
}

@Composable
private fun FlowerPresetCell(
    preset    : FlowerPreset,
    isSelected: Boolean,
    onSelect  : () -> Unit,
    modifier  : Modifier = Modifier
) {
    Box(
        modifier = modifier.aspectRatio(1f).clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Primary.copy(alpha = 0.1f) else Color(0xFFF5F5F5))
            .border(if (isSelected) 2.dp else 1.dp, if (isSelected) Primary else BorderPurple, RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
    ) {
        AsyncImage(
            model = preset.imageRes,
            contentDescription = preset.name,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        if (isSelected) {
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(18.dp).background(Primary, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(11.dp))
            }
        }
        Box(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.38f)).padding(vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(preset.imageName.removeSuffix(".png"), fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AppField(
    modifier     : Modifier = Modifier,
    value        : String,
    onValueChange: (String) -> Unit,
    label        : String,
    placeholder  : String,
    icon         : ImageVector,
    keyboardType : KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier) {
        AppFieldLabel(label)
        OutlinedTextField(
            value = value, onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextGray, fontSize = 14.sp) },
            leadingIcon = { Icon(icon, null, tint = Primary, modifier = Modifier.size(18.dp)) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType), colors = appFieldColors()
        )
    }
}

@Composable
private fun AppFieldLabel(text: String) {
    Text(text, fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 6.dp))
}

@Composable
private fun AppSectionLabel(icon: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(icon, fontSize = 15.sp)
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(Modifier.weight(1f))
        HorizontalDivider(modifier = Modifier.weight(3f), color = BorderPurple, thickness = 1.dp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun appFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Primary, unfocusedBorderColor = BorderPurple,
    focusedTextColor = TextDark, unfocusedTextColor = TextDark, cursorColor = Primary,
    focusedContainerColor = CardWhite, unfocusedContainerColor = BgWhite,
    focusedPlaceholderColor = TextGray, unfocusedPlaceholderColor = TextGray
)