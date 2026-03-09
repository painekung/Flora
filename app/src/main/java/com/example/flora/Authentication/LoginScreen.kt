package com.example.flora.Authentication

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import kotlin.math.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flora.R


private val Purple700    = Color(0xFF7C3AED)
private val Purple500    = Color(0xFFA855F7)
private val Pink500      = Color(0xFFEC4899)
private val Pink200      = Color(0xFFF9A8D4)
private val Pink100      = Color(0xFFFBCFE8)
private val Lilac200     = Color(0xFFE9D5FF)
private val Lilac100     = Color(0xFFF3E8FF)
private val TextDark     = Color(0xFF1E1B4B)
private val TextGray     = Color(0xFF9CA3AF)
private val TextMuted    = Color(0xFFC4B5FD)
private val BgStart      = Color(0xFFFFF0F6)
private val BgMid        = Color(0xFFFDF2FF)
private val BgEnd        = Color(0xFFF0F7FF)
private val GoogleBlue   = Color(0xFF4285F4)
private val GoogleGreen  = Color(0xFF34A853)
private val GoogleYellow = Color(0xFFFBBC05)
private val GoogleRed    = Color(0xFFEA4335)

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authVM: AuthViewModel
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe      by remember { mutableStateOf(false) }
    var emailFocused    by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }

    val authState by authVM.authState.collectAsState()
    val context = LocalContext.current

    // Navigate เมื่อ login สำเร็จ
    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            navController.navigate("MainScreen") {
                popUpTo("login") { inclusive = true }
            }
            authVM.resetState()
        }
    }

    val alphaAnim by animateFloatAsState(
        targetValue = 1f, animationSpec = tween(800), label = "alpha"
    )
    val offsetAnim by animateFloatAsState(
        targetValue = 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "offset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to BgStart,
                        0.4f to BgMid,
                        1.0f to BgEnd
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) { drawDotPattern() }
        GlowOrbs()
        FloatingPetals()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .graphicsLayer {
                        alpha        = alphaAnim
                        translationY = offsetAnim
                    }
                    .shadow(
                        elevation    = 24.dp,
                        shape        = RoundedCornerShape(32.dp),
                        ambientColor = Purple500.copy(alpha = .18f),
                        spotColor    = Pink200.copy(alpha = .25f)
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = .85f))
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(listOf(Pink200.copy(.4f), Lilac200.copy(.4f))),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(horizontal = 32.dp, vertical = 36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-36).dp)
                        .width(60.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                        .background(Brush.horizontalGradient(listOf(Pink200, TextMuted)))
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        FlowerLogo()
                    }

                    Spacer(Modifier.height(26.dp))

                    Text(
                        "Sign In 🌸",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextDark
                    )

                    Spacer(Modifier.height(16.dp))

                    // Email
                    FieldLabel("Email")
                    Spacer(Modifier.height(6.dp))
                    FloraTextField(
                        value         = email,
                        onValueChange = { email = it },
                        placeholder   = "Enter your email",
                        isFocused     = emailFocused,
                        onFocusChange = { emailFocused = it },
                        leadingIcon   = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint     = if (emailFocused) Purple700 else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    // Password
                    FieldLabel("Password")
                    Spacer(Modifier.height(6.dp))
                    FloraTextField(
                        value                = password,
                        onValueChange        = { password = it },
                        placeholder          = "Enter your password",
                        isFocused            = passwordFocused,
                        onFocusChange        = { passwordFocused = it },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint     = if (passwordFocused) Purple700 else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint     = TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(14.dp))

                    // Remember / Forgot row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        if (rememberMe)
                                            Brush.linearGradient(listOf(TextMuted, Pink500))
                                        else
                                            Brush.linearGradient(listOf(Color.White, Color.White))
                                    )
                                    .border(
                                        1.5.dp,
                                        if (rememberMe) Purple500 else Lilac200,
                                        RoundedCornerShape(5.dp)
                                    )
                                    .clickable { rememberMe = !rememberMe },
                                contentAlignment = Alignment.Center
                            ) {
                                if (rememberMe) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint     = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Text("Remember me", fontSize = 13.sp, color = TextGray)
                        }

                        TextButton(onClick = { /* forgot password */ }) {
                            Text(
                                "Forgot password?",
                                fontSize   = 9.sp,
                                color      = Pink500,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Error message
                    if (authState is AuthViewModel.AuthState.Error) {
                        Text(
                            text     = (authState as AuthViewModel.AuthState.Error).message,
                            color    = Color.Red,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Loading
                    if (authState is AuthViewModel.AuthState.Loading) {
                        CircularProgressIndicator(
                            color    = Purple500,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
                        )
                    }

                    // Sign In Button
                    GradientButton(
                        text    = "Sign In",
                        onClick = { authVM.loginWithEmail(email, password) }
                    )

                    Spacer(Modifier.height(18.dp))

                    DividerWithText("or continue with")

                    Spacer(Modifier.height(16.dp))

                    // Google Button
                    GoogleSignInButton(onClick = { authVM.loginWithGoogle(context) })

                    Spacer(Modifier.height(22.dp))

                    // Register link
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text("Don't have an account?", fontSize = 13.sp, color = TextGray)
                        Spacer(Modifier.width(4.dp))
                        TextButton(
                            onClick        = { navController.navigate("register") { launchSingleTop = true } },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                "Create Account",
                                fontSize   = 13.sp,
                                color      = Pink500,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FlowerLogo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(16.dp, CircleShape, ambientColor = Pink200.copy(.5f))
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFFFCE7F3), Color(0xFFEDE9FE)))),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter      = painterResource(R.drawable.logo_no_bg_flora),
                contentDescription = "Flora Logo",
                modifier     = Modifier.scale(1.2f),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text       = "FLORA",
            fontSize   = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 3.sp,
            style      = TextStyle(brush = Brush.linearGradient(colors = listOf(Purple700, Pink500)))
        )
    }
}

@Composable
private fun FloraTextField(
    value                : String,
    onValueChange        : (String) -> Unit,
    placeholder          : String,
    isFocused            : Boolean,
    onFocusChange        : (Boolean) -> Unit,
    leadingIcon          : @Composable (() -> Unit)? = null,
    trailingIcon         : @Composable (() -> Unit)? = null,
    visualTransformation : VisualTransformation = VisualTransformation.None
) {
    val borderColor by animateColorAsState(
        targetValue   = if (isFocused) Purple500 else Lilac200,
        animationSpec = tween(200), label = "border"
    )
    val bgColor by animateColorAsState(
        targetValue   = if (isFocused) Lilac100.copy(.5f) else Color(0xFFF9FAFB),
        animationSpec = tween(200), label = "bg"
    )

    OutlinedTextField(
        value                = value,
        onValueChange        = onValueChange,
        placeholder          = { Text(placeholder, fontSize = 14.sp, color = TextGray.copy(.7f)) },
        textStyle            = TextStyle(fontSize = 15.sp, color = TextDark),
        leadingIcon          = leadingIcon,
        trailingIcon         = trailingIcon,
        visualTransformation = visualTransformation,
        shape                = RoundedCornerShape(14.dp),
        colors               = OutlinedTextFieldDefaults.colors(
            focusedBorderColor        = borderColor,
            unfocusedBorderColor      = borderColor,
            focusedContainerColor     = bgColor,
            unfocusedContainerColor   = bgColor,
            cursorColor               = Purple700,
            focusedLeadingIconColor   = Purple700,
            unfocusedLeadingIconColor = TextMuted,
            focusedTrailingIconColor  = TextMuted,
            unfocusedTrailingIconColor= TextMuted,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { onFocusChange(it.isFocused) }
    )
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text          = text.uppercase(),
        fontSize      = 11.sp,
        fontWeight    = FontWeight.SemiBold,
        letterSpacing = 0.8.sp,
        color         = Purple700
    )
}

@Composable
private fun GradientButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(listOf(Purple700, Purple500, Pink500)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text          = text,
            color         = Color.White,
            fontSize      = 16.sp,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick  = onClick ,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape    = RoundedCornerShape(14.dp),
        border   = BorderStroke(1.5.dp, Lilac200),
        colors   = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
    ) {
        Canvas(modifier = Modifier.size(20.dp)) { drawGoogleIcon() }
        Spacer(Modifier.width(10.dp))
        Text(
            text       = "Sign in with Google",
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color      = Color(0xFF374151)
        )
    }
}

@Composable
private fun DividerWithText(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier          = Modifier.fillMaxWidth()
    ) {
        Box(Modifier.weight(1f).height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, Lilac200))))
        Text(text = text, fontSize = 12.sp, color = TextMuted, modifier = Modifier.padding(horizontal = 12.dp))
        Box(Modifier.weight(1f).height(1.dp).background(Brush.horizontalGradient(listOf(Lilac200, Color.Transparent))))
    }
}

@Composable
private fun GlowOrbs() {
    Box(
        modifier = Modifier
            .offset(x = (-80).dp, y = (-80).dp)
            .size(280.dp)
            .background(Brush.radialGradient(listOf(Pink200.copy(.35f), Color.Transparent)), CircleShape)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomEnd)
            .offset(x = 60.dp, y = 60.dp)
            .size(240.dp)
            .background(Brush.radialGradient(listOf(Purple500.copy(.27f), Color.Transparent)), CircleShape)
    )
}

@Composable
private fun FloatingPetals() {
    val infiniteTransition = rememberInfiniteTransition(label = "petals")
    val float by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label         = "petalFloat"
    )

    data class Petal(val xFraction: Float, val yFraction: Float, val sizeDp: Float, val rotateDeg: Float, val color: Color, val phaseOffset: Float)

    val petals = remember {
        listOf(
            Petal(.07f, .10f, 38f, -20f, Pink200, 0.0f),
            Petal(.88f, .14f, 26f,  30f, Pink100, 0.3f),
            Petal(.03f, .52f, 20f,  60f, Color(0xFFF0ABFC), 0.6f),
            Petal(.92f, .68f, 30f, -45f, Pink200, 0.2f),
            Petal(.13f, .87f, 18f,  15f, Pink100, 0.5f),
            Petal(.85f, .40f, 22f, -30f, Color(0xFFFDA4AF), 0.4f),
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        petals.forEach { p ->
            val phase   = (float + p.phaseOffset) % 1f
            val yOffset = (sin(phase * 2 * PI).toFloat()) * 18.dp.toPx()
            drawPetal(size.width * p.xFraction, size.height * p.yFraction + yOffset, p.sizeDp.dp.toPx(), p.rotateDeg, p.color)
        }
    }
}

private fun DrawScope.drawDotPattern() {
    val spacing = 28.dp.toPx()
    val cols = (size.width  / spacing).toInt() + 1
    val rows = (size.height / spacing).toInt() + 1
    repeat(rows) { r -> repeat(cols) { c ->
        drawCircle(color = Color(0xFFE879F9).copy(.08f), radius = 1.5.dp.toPx(), center = Offset(c * spacing, r * spacing))
    }}
}

private fun DrawScope.drawPetal(cx: Float, cy: Float, size: Float, rotateDeg: Float, color: Color) {
    drawOval(color = color.copy(.55f), topLeft = Offset(cx - size * .28f, cy - size * .5f), size = androidx.compose.ui.geometry.Size(size * .56f, size))
    drawOval(color = color.copy(.4f),  topLeft = Offset(cx - size * .28f, cy - size * .5f), size = androidx.compose.ui.geometry.Size(size * .56f, size))
}

private fun DrawScope.drawGoogleIcon() {
    val w = size.width; val h = size.height
    val cx = w / 2f;    val cy = h / 2f
    val r  = w * .46f
    val stroke = androidx.compose.ui.graphics.drawscope.Stroke(width = w * .18f)
    val arcSize = androidx.compose.ui.geometry.Size(r * 2, r * 2)
    val topLeft = Offset(cx - r, cy - r)

    drawArc(GoogleBlue,   -30f, 120f, false, topLeft, arcSize, style = stroke)
    drawArc(GoogleGreen,   90f,  90f, false, topLeft, arcSize, style = stroke)
    drawArc(GoogleYellow, 180f,  90f, false, topLeft, arcSize, style = stroke)
    drawArc(GoogleRed,    270f,  60f, false, topLeft, arcSize, style = stroke)
    drawRect(GoogleBlue, topLeft = Offset(cx, cy - w * .09f), size = androidx.compose.ui.geometry.Size(r, w * .18f))
}