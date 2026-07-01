package com.example.ui.screens.ai

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.CalculatorViewModel
import com.example.R
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AiTabPortrait(viewModel: CalculatorViewModel, onOpenSettings: () -> Unit) {
    val aiQuery by viewModel.aiQuery.collectAsState()
    val aiResponse by viewModel.aiResponse.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val customApiKey by viewModel.customApiKey.collectAsState()
    
    val hasKey = customApiKey.isNotBlank() || (
        BuildConfig.GEMINI_API_KEY.isNotBlank() && 
        BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" && 
        BuildConfig.GEMINI_API_KEY != "GEMINI_API_KEY"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero card
        NeoBrutalCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = NeoPurple
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_neo_sparkle),
                    contentDescription = "AI Icon",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = "Asisten MTK AI Gaul",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Pecahin soal cerita lu pake langkah-langkah yang gampang dimengerti!",
                        fontSize = 11.sp,
                        color = Color.Black.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // API Key Section Toggle - Hidden if hasKey is active!
        if (!hasKey) {
            NeoBrutalCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeoWhite
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_neo_lock),
                                contentDescription = "Key Icon",
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Atur API Key Gemini Lu",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .background(NeoOrange, RoundedCornerShape(6.dp))
                                .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                .clickable { onOpenSettings() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Setting Icon",
                                tint = Color.Black,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "ATUR KEY",
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = Color.Black
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(NeoPink, CircleShape)
                            )
                            Text(
                                text = "Status: API Key Belum Ada",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeoPink
                            )
                        }

                        Text(
                            text = "AKTIVASI SEKARANG",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            modifier = Modifier
                                .background(NeoYellow, RoundedCornerShape(6.dp))
                                .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                .clickable { onOpenSettings() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Input Question Card
        NeoBrutalCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = NeoWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tulis Soal MTK / Cerita Lu",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )

                    // Status Indicator Badge
                    val statusText = if (hasKey) "READY" else "PENDING"
                    val statusBg = if (hasKey) NeoGreen else NeoPink

                    Row(
                        modifier = Modifier
                            .background(statusBg, RoundedCornerShape(8.dp))
                            .border(1.5.dp, Color.Black, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.Black, CircleShape)
                        )
                        Text(
                            text = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
                
                NeoWordProblemField(
                    value = aiQuery,
                    onValueChange = { viewModel.updateAiQuery(it) },
                    label = "",
                    placeholder = "Contoh: Budi beli 3 cilok harganya 5 rebu. Kalo bayar pake duit 20 rebu, kembaliannya dapet berapa cuy?",
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeoBrutalButton(
                        text = "PECAHIN PAKE AI",
                        onClick = { viewModel.solveWithAi() },
                        backgroundColor = NeoCyan,
                        isEnabled = !isAiLoading,
                        modifier = Modifier.weight(1.5f),
                        fontSize = 13
                    )
                    
                    NeoBrutalButton(
                        text = "BERSIHIN",
                        onClick = { viewModel.clearAiSolver() },
                        backgroundColor = NeoOrange,
                        isEnabled = !isAiLoading,
                        modifier = Modifier.weight(1f),
                        fontSize = 13
                    )
                }
            }
        }

        // Result Solution Card
        if (isAiLoading || aiResponse.isNotEmpty()) {
            NeoBrutalCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeoBg
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hasil Solusi AI",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (isAiLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                            } else if (aiResponse.isNotEmpty()) {
                                val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                                val context = androidx.compose.ui.platform.LocalContext.current
                                Row(
                                    modifier = Modifier
                                        .background(NeoGreen, RoundedCornerShape(6.dp))
                                        .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                        .clickable {
                                            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(aiResponse))
                                            android.widget.Toast.makeText(context, "Solusi berhasil disalin, cuy!", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    CopyIcon(modifier = Modifier.size(12.dp))
                                    Text(
                                        text = "SALIN SOLUSI",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }

                    if (isAiLoading) {
                        Text(
                            text = "Sabar cuy, AI lagi mikir keras nyari jawabannya...",
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color.DarkGray
                        )
                    } else {
                        Text(
                            text = aiResponse,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiLeftScreen(viewModel: CalculatorViewModel, onOpenSettings: () -> Unit) {
    val customApiKey by viewModel.customApiKey.collectAsState()
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("CalculatorPrefs", Context.MODE_PRIVATE) }

    val hasKey = customApiKey.isNotBlank() || (
        BuildConfig.GEMINI_API_KEY.isNotBlank() && 
        BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" && 
        BuildConfig.GEMINI_API_KEY != "GEMINI_API_KEY"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (!hasKey) {
            NeoBrutalCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                backgroundColor = NeoWhite
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_neo_lock),
                                contentDescription = "Key Icon",
                                modifier = Modifier.size(16.dp)
                            )
                            Text("Setelan API Key Gemini", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .background(NeoOrange, RoundedCornerShape(6.dp))
                                .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                .clickable { onOpenSettings() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Setting Icon",
                                tint = Color.Black,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "ATUR KEY",
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = Color.Black
                            )
                        }
                    }

                    Text(
                        text = "Aplikasi ini aslinya 100% luring (offline) cuy. Tapi buat ngaktifin asisten matematika kecerdasan buatan (AI Hybrid) biar bisa mikir gokil, lu butuh pasang API Key Gemini buatan Google AI Studio secara mandiri demi keamanan data lu, bro!",
                        fontSize = 10.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 13.sp
                    )

                    NeoBrutalButton(
                        text = "BUKA PENGATURAN API KEY",
                        onClick = { onOpenSettings() },
                        backgroundColor = NeoGreen,
                        fontSize = 11,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(NeoPink, CircleShape)
                        )
                        Text(
                            text = "Status: API Key Belum Ada",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoPink
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Tips: Google AI Studio itu resmi buatan Google, gratis, aman, dan cepet banget bro! Seluruh proses di atas cuma makan waktu 1 menit aja kok.",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        lineHeight = 11.sp
                    )
                }
            }
        } else {
            // Active AI state - hides key setup border and shows beautiful success panel
            NeoBrutalCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                backgroundColor = NeoGreen.copy(alpha = 0.15f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .background(NeoGreen, RoundedCornerShape(12.dp))
                            .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_neo_sparkle),
                            contentDescription = "Active Icon",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = "ASISTEN AI AKTIF",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Koneksi ke Gemini 1.5 Flash lancar jaya! Gunakan kolom input di sebelah kanan untuk memecahkan soal matematika dengan penjelasan langkah-demi-langkah super gampang.",
                        fontSize = 10.sp,
                        color = Color.Black.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Bold,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .background(NeoWhite, RoundedCornerShape(6.dp))
                            .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                            .clickable { onOpenSettings() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Setting Icon",
                            tint = Color.Black,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "ATUR KEMBALI KEY",
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiTabLandscape(viewModel: CalculatorViewModel) {
    val aiQuery by viewModel.aiQuery.collectAsState()
    val aiResponse by viewModel.aiResponse.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val customApiKey by viewModel.customApiKey.collectAsState()

    val hasKey = customApiKey.isNotBlank() || (
        BuildConfig.GEMINI_API_KEY.isNotBlank() && 
        BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" && 
        BuildConfig.GEMINI_API_KEY != "GEMINI_API_KEY"
    )

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NeoBrutalCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            backgroundColor = NeoWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tulis Soal MTK / Cerita Lu",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )

                        // Status Indicator Badge
                        val statusText = if (hasKey) "READY" else "PENDING"
                        val statusBg = if (hasKey) NeoGreen else NeoPink

                        Row(
                            modifier = Modifier
                                .background(statusBg, RoundedCornerShape(8.dp))
                                .border(1.5.dp, Color.Black, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color.Black, CircleShape)
                            )
                            Text(
                                text = statusText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }

                    NeoWordProblemField(
                        value = aiQuery,
                        onValueChange = { viewModel.updateAiQuery(it) },
                        label = "",
                        placeholder = "Masukin pertanyaan lu di sini ya cuy...",
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeoBrutalButton(
                        text = "PECAHIN PAKE AI",
                        onClick = { viewModel.solveWithAi() },
                        backgroundColor = NeoCyan,
                        isEnabled = !isAiLoading,
                        modifier = Modifier.weight(1.5f),
                        fontSize = 11
                    )

                    NeoBrutalButton(
                        text = "BERSIHIN",
                        onClick = { viewModel.clearAiSolver() },
                        backgroundColor = NeoOrange,
                        isEnabled = !isAiLoading,
                        modifier = Modifier.weight(1f),
                        fontSize = 11
                    )
                }
            }
        }

        NeoBrutalCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            backgroundColor = NeoBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hasil Solusi AI Langkah-Demi-Langkah",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (isAiLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else if (aiResponse.isNotEmpty()) {
                            val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                            val context = androidx.compose.ui.platform.LocalContext.current
                            Row(
                                modifier = Modifier
                                    .background(NeoGreen, RoundedCornerShape(6.dp))
                                    .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                    .clickable {
                                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(aiResponse))
                                        android.widget.Toast.makeText(context, "Solusi berhasil disalin, cuy!", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CopyIcon(modifier = Modifier.size(12.dp))
                                Text(
                                    text = "SALIN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (isAiLoading) {
                        Text(
                            text = "Sabar cuy, AI lagi mikir keras nyari jawabannya...",
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color.DarkGray
                        )
                    } else if (aiResponse.isNotEmpty()) {
                        Text(
                            text = aiResponse,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            lineHeight = 16.sp
                        )
                    } else {
                        Text(
                            text = "Masukin dulu soal cerita lu di kolom sebelah kiri, terus klik tombol PECAHIN PAKE AI biar AI pamer keenceran otaknya di sini, cuy!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CopyIcon(modifier: Modifier = Modifier, tint: Color = Color.Black) {
    Box(modifier = modifier.size(14.dp)) {
        // Back overlapping box
        Box(
            modifier = Modifier
                .size(9.dp)
                .align(Alignment.TopEnd)
                .border(1.5.dp, tint, RoundedCornerShape(2.dp))
        )
        // Front overlapping box
        Box(
            modifier = Modifier
                .size(9.dp)
                .align(Alignment.BottomStart)
                .background(Color.Transparent)
                .border(1.5.dp, tint, RoundedCornerShape(2.dp))
        )
    }
}
