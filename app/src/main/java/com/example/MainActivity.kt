package com.example

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.screens.ai.*
import com.example.ui.screens.base.*
import com.example.ui.screens.calc.*
import com.example.ui.screens.formulas.*
import com.example.ui.screens.solver.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val viewModel: CalculatorViewModel = viewModel()
    val activeTab by viewModel.activeTab.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("CalculatorPrefs", Context.MODE_PRIVATE) }
    var showSecretSettingsModal by remember { mutableStateOf(false) }
    var showFirstRunDialog by remember {
        mutableStateOf(sharedPref.getBoolean("is_first_run_api_key_prompt", true))
    }

    val customApiKey by viewModel.customApiKey.collectAsState()
    val isCheckingApiKey by viewModel.isCheckingApiKey.collectAsState()
    val apiKeyValidationResult by viewModel.apiKeyValidationResult.collectAsState()
    var keyInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val savedKey = sharedPref.getString("gemini_api_key", "") ?: ""
        viewModel.setCustomApiKey(savedKey)
    }

    LaunchedEffect(activeTab) {
        if (activeTab == 4) {
            val isFirstRunAiTab = sharedPref.getBoolean("is_first_run_ai_tab_prompt", true)
            val savedKey = sharedPref.getString("gemini_api_key", "") ?: ""
            val isFirstRunPrompt = sharedPref.getBoolean("is_first_run_api_key_prompt", true)
            if (isFirstRunAiTab && savedKey.isBlank() && !isFirstRunPrompt) {
                showSecretSettingsModal = true
                sharedPref.edit().putBoolean("is_first_run_ai_tab_prompt", false).apply()
            }
        }
    }

    LaunchedEffect(showSecretSettingsModal) {
        if (showSecretSettingsModal) {
            keyInput = customApiKey
            viewModel.clearApiKeyValidation()
        }
    }

    LaunchedEffect(apiKeyValidationResult) {
        if (apiKeyValidationResult == true && showSecretSettingsModal) {
            sharedPref.edit().putString("gemini_api_key", keyInput.trim()).apply()
        }
    }

    var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var isIdle by remember { mutableStateOf(false) }

    LaunchedEffect(lastInteractionTime) {
        isIdle = false
        delay(30000)
        isIdle = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "idle_anim")

    val idleBobbingOffsetFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isIdle) -5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isIdle) 1000 else 100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobbing"
    )

    val idleWiggleRotationFloat by infiniteTransition.animateFloat(
        initialValue = if (isIdle) -1.5f else 0f,
        targetValue = if (isIdle) 1.5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isIdle) 600 else 100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wiggle"
    )

    val idleShadowMultiplierFloat by infiniteTransition.animateFloat(
        initialValue = if (isIdle) 0.8f else 1.0f,
        targetValue = if (isIdle) 1.3f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isIdle) 800 else 100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shadow_pulse"
    )

    val activeBobbingOffset = if (isIdle) idleBobbingOffsetFloat.dp else 0.dp
    val activeWiggleRotation = if (isIdle) idleWiggleRotationFloat else 0f
    val activeShadowMultiplier = if (isIdle) idleShadowMultiplierFloat else 1f

    CompositionLocalProvider(
        LocalIdleState provides isIdle,
        LocalIdleBobbingOffset provides activeBobbingOffset,
        LocalIdleWiggleRotation provides activeWiggleRotation,
        LocalIdleShadowMultiplier provides activeShadowMultiplier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoBg)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent()
                            lastInteractionTime = System.currentTimeMillis()
                        }
                    }
                }
        ) {
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                visible = true
            }

            val alpha by animateFloatAsState(
                targetValue = if (visible) 1f else 0f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                label = "fade_in"
            )

            val scale by animateFloatAsState(
                targetValue = if (visible) 1f else 0.95f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                label = "scale_in"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        this.alpha = alpha
                        this.scaleX = scale
                        this.scaleY = scale
                    }
            ) {
                if (isLandscape) {
                    LandscapeLayout(viewModel, activeTab, onOpenSettings = { showSecretSettingsModal = true })
                } else {
                    PortraitLayout(viewModel, activeTab, onOpenSettings = { showSecretSettingsModal = true })
                }
            }

            if (showFirstRunDialog && activeTab == 4) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    NeoBrutalCard(
                        modifier = Modifier
                            .fillMaxWidth(if (isLandscape) 0.65f else 0.9f)
                            .padding(16.dp),
                        backgroundColor = NeoWhite,
                        borderWidth = 3.dp,
                        shadowOffset = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_neo_sparkle),
                                    contentDescription = "AI Sparkle",
                                    tint = NeoPurple,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "Aktivin Asisten AI, Cuy! 🤖🔥",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.5.dp)
                                    .background(Color.Black)
                            )
                            
                            Text(
                                text = "Biar kalkulator gokil ini makin encer otaknya buat mecahin soal cerita matematika serumit kisah cintamu pake asisten AI Gemini, yuk pasang API Key-mu dulu, bro! 😎",
                                fontSize = 12.sp,
                                color = Color.Black,
                                lineHeight = 16.sp
                            )
                            
                            NeoBrutalCard(
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = NeoYellow.copy(alpha = 0.15f),
                                borderWidth = 1.5.dp,
                                shadowOffset = 3.dp,
                                cornerRadius = 8.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_neo_lock),
                                        contentDescription = "Lock Secure",
                                        tint = NeoOrange,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Kenapa musti input manual?",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "Karena aturan keamanan Android yang ketat banget, aplikasi gak diijinin ngambil API Key secara otomatis dari sistem HP lu, cuy! Jadi lu musti input sendiri demi keamanan privasi lu. Tenang, kuncinya disimpen aman banget kok di lokal storage HP lu sendiri, gak bakal dikirim ke server manapun kecuali langsung ke Google Gemini!",
                                            fontSize = 9.5.sp,
                                            color = Color.DarkGray,
                                            lineHeight = 13.sp
                                        )
                                    }
                                }
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NeoBrutalButton(
                                    text = "Nanti Aja Offline",
                                    onClick = {
                                        sharedPref.edit().putBoolean("is_first_run_api_key_prompt", false).apply()
                                        showFirstRunDialog = false
                                    },
                                    backgroundColor = NeoPink,
                                    fontSize = 11,
                                    modifier = Modifier.weight(1f)
                                )
                                NeoBrutalButton(
                                    text = "Atur Sekarang! 🚀",
                                    onClick = {
                                        sharedPref.edit().putBoolean("is_first_run_api_key_prompt", false).apply()
                                        showFirstRunDialog = false
                                        showSecretSettingsModal = true
                                    },
                                    backgroundColor = NeoGreen,
                                    fontSize = 11,
                                    modifier = Modifier.weight(1.2f)
                                )
                            }
                        }
                    }
                }
            }

            if (showSecretSettingsModal) {
                val uriHandler = LocalUriHandler.current
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    NeoBrutalCard(
                        modifier = Modifier
                            .fillMaxWidth(if (isLandscape) 0.7f else 0.92f)
                            .padding(16.dp),
                        backgroundColor = NeoWhite,
                        borderWidth = 3.dp,
                        shadowOffset = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_neo_lock),
                                        contentDescription = "Secret Key",
                                        tint = NeoPurple,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Text(
                                        text = "Panel Rahasia API Key",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                }
                                IconButton(
                                    onClick = { showSecretSettingsModal = false },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(NeoPink, RoundedCornerShape(8.dp))
                                        .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Tutup",
                                        tint = Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.5.dp)
                                    .background(Color.Black)
                             )

                             NeoBrutalButton(
                                text = "DAPETIN API KEY GRATIS DI SINI, CUY!",
                                onClick = {
                                    try {
                                        uriHandler.openUri("https://aistudio.google.com/")
                                    } catch (e: Exception) {
                                        // Ignore or fallback
                                    }
                                },
                                backgroundColor = NeoYellow,
                                fontSize = 12,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = "Cara dapetnya gampang banget, tinggal login pake akun Google lu di web Google AI Studio, klik \"Create API Key\", terus copy kodenya kesini. Gratis kok, gak bayar sama sekali!",
                                fontSize = 11.sp,
                                color = Color.DarkGray,
                                lineHeight = 15.sp
                            )

                            NeoWordProblemField(
                                value = keyInput,
                                onValueChange = { keyInput = it },
                                label = "API Key Gemini Lu:",
                                placeholder = "AIzaSy...",
                                singleLine = true,
                                maxLines = 1,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (isCheckingApiKey) {
                                NeoBrutalCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    backgroundColor = NeoCyan.copy(alpha = 0.2f),
                                    borderWidth = 1.5.dp,
                                    shadowOffset = 3.dp,
                                    cornerRadius = 8.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Color.Black,
                                            strokeWidth = 2.5.dp
                                        )
                                        Text(
                                            text = "Lagi ngecek keaslian API Key-mu ke Google, sabar ya cuy...",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                }
                            } else if (apiKeyValidationResult != null) {
                                val isValid = apiKeyValidationResult == true
                                val cardBg = if (isValid) NeoGreen.copy(alpha = 0.25f) else NeoPink.copy(alpha = 0.25f)
                                val icon = if (isValid) Icons.Default.CheckCircle else Icons.Default.Warning
                                val msg = if (isValid) {
                                    if (keyInput.isBlank()) {
                                        "API Key berhasil dihapus/direset! Sekarang kembali menggunakan mode default/luring."
                                    } else {
                                        "MANTAP SURANTAP! API Key lu valid 100%! Selamat menikmati fitur AI super gokil ini!"
                                    }
                                } else {
                                    "WADUH ZONK! API Key lu salah, udah kedaluwarsa, atau gak aktif cuy. Coba cek lagi deh!"
                                }

                                NeoBrutalCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    backgroundColor = cardBg,
                                    borderWidth = 2.dp,
                                    shadowOffset = 3.dp,
                                    cornerRadius = 8.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = if (isValid) "Sukses" else "Gagal",
                                            tint = if (isValid) NeoGreen else NeoPink,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(
                                            text = msg,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NeoBrutalButton(
                                    text = "Batal",
                                    onClick = { showSecretSettingsModal = false },
                                    backgroundColor = NeoOrange,
                                    fontSize = 12,
                                    modifier = Modifier.weight(1f)
                                )
                                NeoBrutalButton(
                                    text = "Simpan & Verifikasi",
                                    onClick = {
                                        viewModel.validateAndSetApiKey(keyInput.trim())
                                    },
                                    backgroundColor = NeoGreen,
                                    fontSize = 12,
                                    isEnabled = !isCheckingApiKey,
                                    modifier = Modifier.weight(1.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PortraitLayout(viewModel: CalculatorViewModel, activeTab: Int, onOpenSettings: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val tabs = listOf("CALC", "BASE", "SOLV", "FORM", "AI")
            tabs.forEachIndexed { index, title ->
                NeoTabButton(
                    text = title,
                    isSelected = activeTab == index,
                    onClick = { viewModel.setActiveTab(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeTab) {
                0 -> CalcTabPortrait(viewModel)
                1 -> BaseTabPortrait(viewModel)
                2 -> SolverTabPortrait(viewModel)
                3 -> FormulasTabPortrait(viewModel)
                4 -> AiTabPortrait(viewModel, onOpenSettings)
            }
        }
    }
}

@Composable
fun LandscapeLayout(viewModel: CalculatorViewModel, activeTab: Int, onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(0.35f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf("CALC", "BASE", "SOLV", "FORM", "AI")
                tabs.forEachIndexed { index, title ->
                    NeoTabButton(
                        text = title,
                        isSelected = activeTab == index,
                        onClick = { viewModel.setActiveTab(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    0 -> CalcLeftScreen(viewModel)
                    1 -> BaseLeftScreen(viewModel)
                    2 -> SolverLeftScreen(viewModel)
                    3 -> FormulasLeftScreen(viewModel)
                    4 -> AiLeftScreen(viewModel, onOpenSettings)
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxHeight()
        ) {
            when (activeTab) {
                0 -> CalcTabLandscape(viewModel)
                1 -> BaseTabLandscape(viewModel)
                2 -> SolverTabLandscape(viewModel)
                3 -> FormulasTabLandscape(viewModel)
                4 -> AiTabLandscape(viewModel)
            }
        }
    }
}
