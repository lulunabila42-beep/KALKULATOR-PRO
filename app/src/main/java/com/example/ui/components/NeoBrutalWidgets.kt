package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

fun getNeoIconForChar(char: String): Int? {
    return when (char.trim()) {
        "+" -> R.drawable.ic_neo_plus
        "−" -> R.drawable.ic_neo_minus
        "×" -> R.drawable.ic_neo_multiply
        "÷" -> R.drawable.ic_neo_divide
        "=" -> R.drawable.ic_neo_equals
        "DEL" -> R.drawable.ic_neo_delete
        "AC" -> R.drawable.ic_neo_clear
        "H" -> R.drawable.ic_neo_history
        "^" -> R.drawable.ic_neo_power
        "%" -> R.drawable.ic_neo_percent
        "pi" -> R.drawable.ic_neo_pi
        "√" -> R.drawable.ic_neo_sqrt
        else -> null
    }
}

@Composable
fun NeoBrutalCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    shadowColor: Color = Color.Black,
    borderWidth: Dp = 3.dp,
    shadowOffset: Dp = 6.dp,
    cornerRadius: Dp = 16.dp,
    seed: Int = 0,
    content: @Composable BoxScope.() -> Unit
) {
    val isIdle = LocalIdleState.current

    val computedSeed = remember(seed) {
        val s = seed * 37 + 17
        if (s < 0) -s else s
    }

    val cardDelay = remember(computedSeed) { (computedSeed % 4) * 2000 } // 0ms, 2000ms, 4000ms, 6000ms
    val cardDuration = remember(computedSeed) { 1000 + (computedSeed % 3) * 400 } // 1000ms, 1400ms, 1800ms

    val infiniteTransition = rememberInfiniteTransition(label = "card_idle_$seed")

    val localBobbingFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isIdle) -5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isIdle) cardDuration else 100,
                delayMillis = if (isIdle) cardDelay else 0,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "card_bobbing"
    )

    val localShadowMult by infiniteTransition.animateFloat(
        initialValue = if (isIdle) 0.8f else 1.0f,
        targetValue = if (isIdle) 1.3f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isIdle) (cardDuration + 200) else 100,
                delayMillis = if (isIdle) cardDelay else 0,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "card_shadow"
    )

    val activeOffset = if (isIdle) localBobbingFloat.dp else 0.dp
    val activeShadowOffset = if (isIdle) shadowOffset * localShadowMult else shadowOffset

    Box(
        modifier = modifier
            .offset(y = activeOffset)
            .drawBehind {
                drawRoundRect(
                    color = shadowColor,
                    topLeft = Offset(activeShadowOffset.toPx(), activeShadowOffset.toPx()),
                    size = size,
                    cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
                )
            }
            .background(backgroundColor, RoundedCornerShape(cornerRadius))
            .border(borderWidth, Color.Black, RoundedCornerShape(cornerRadius))
    ) {
        content()
    }
}

@Composable
fun NeoBrutalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    textColor: Color = Color.Black,
    isEnabled: Boolean = true,
    fontSize: Int = 18,
    testTag: String = "",
    iconRes: Int? = null,
    seed: Int = 0
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val isIdle = LocalIdleState.current

    val computedSeed = remember(text, seed) {
        val h = text.hashCode() + seed * 31
        if (h < 0) -h else h
    }

    val btnDelay = remember(computedSeed) { (computedSeed % 4) * 2000 } // 0ms, 2000ms, 4000ms, 6000ms
    val btnDuration = remember(computedSeed) { 800 + (computedSeed % 3) * 300 } // 800ms, 1100ms, 1400ms
    val rotationMultiplier = remember(computedSeed) { ((computedSeed % 11) / 10f) + 0.5f } // 0.5 to 1.5
    val direction = remember(computedSeed) { if (computedSeed % 2 == 0) 1f else -1f }
    val shadowPulseFactor = remember(computedSeed) { ((computedSeed % 5) / 10f) + 0.8f } // 0.8 to 1.2

    val infiniteTransition = rememberInfiniteTransition(label = "btn_idle_$text")

    val localWiggle by infiniteTransition.animateFloat(
        initialValue = if (isIdle) -1.8f else 0f,
        targetValue = if (isIdle) 1.8f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isIdle) btnDuration else 100,
                delayMillis = if (isIdle) btnDelay else 0,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btn_wiggle"
    )

    val localShadowMult by infiniteTransition.animateFloat(
        initialValue = if (isIdle) 0.8f else 1.0f,
        targetValue = if (isIdle) 1.3f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isIdle) (btnDuration + 200) else 100,
                delayMillis = if (isIdle) btnDelay else 0,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btn_shadow"
    )

    val translationX by animateDpAsState(targetValue = if (isEnabled && pressed) 3.dp else 0.dp, label = "tx")
    val translationY by animateDpAsState(targetValue = if (isEnabled && pressed) 3.dp else 0.dp, label = "ty")
    
    val baseShadowOffset = if (isEnabled && pressed) 1.5.dp else 5.dp
    val activeShadowOffset = if (isIdle && isEnabled && !pressed) baseShadowOffset * localShadowMult * shadowPulseFactor else baseShadowOffset
    val activeRotation = if (isIdle && isEnabled && !pressed) localWiggle * rotationMultiplier * direction else 0f

    Box(
        modifier = modifier
            .testTag(testTag.ifEmpty { "btn_$text" })
            .offset(x = translationX, y = translationY)
            .graphicsLayer {
                rotationZ = activeRotation
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEnabled,
                onClick = onClick
            )
            .drawBehind {
                if (activeShadowOffset > 0.dp) {
                    drawRoundRect(
                        color = Color.Black,
                        topLeft = Offset(activeShadowOffset.toPx(), activeShadowOffset.toPx()),
                        size = size,
                        cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                    )
                }
            }
            .background(
                if (isEnabled) backgroundColor else Color.LightGray.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .border(
                2.5.dp,
                if (isEnabled) Color.Black else Color.Gray.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = if (isEnabled) textColor else Color.Gray,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Text(
                text = text,
                color = if (isEnabled) textColor else Color.Gray,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
        }
    }
}

@Composable
fun NeoTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    seed: Int = 0
) {
    val isIdle = LocalIdleState.current

    val computedSeed = remember(text, seed) {
        val h = text.hashCode() + seed * 31
        if (h < 0) -h else h
    }

    val tabDelay = remember(computedSeed) { (computedSeed % 4) * 2000 } // 0ms, 2000ms, 4000ms, 6000ms
    val tabDuration = remember(computedSeed) { 700 + (computedSeed % 3) * 300 } // 700ms, 1000ms, 1300ms
    val rotationMultiplier = remember(computedSeed) { ((computedSeed % 9) / 10f) + 0.4f } // 0.4 to 1.2
    val direction = remember(computedSeed) { if (computedSeed % 2 == 0) -1f else 1f }
    val shadowPulseFactor = remember(computedSeed) { ((computedSeed % 7) / 10f) + 0.7f } // 0.7 to 1.3

    val infiniteTransition = rememberInfiniteTransition(label = "tab_idle_$text")

    val localWiggle by infiniteTransition.animateFloat(
        initialValue = if (isIdle) -1.5f else 0f,
        targetValue = if (isIdle) 1.5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isIdle) tabDuration else 100,
                delayMillis = if (isIdle) tabDelay else 0,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tab_wiggle"
    )

    val localShadowMult by infiniteTransition.animateFloat(
        initialValue = if (isIdle) 0.8f else 1.0f,
        targetValue = if (isIdle) 1.3f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isIdle) (tabDuration + 200) else 100,
                delayMillis = if (isIdle) tabDelay else 0,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tab_shadow"
    )

    val translationX by animateDpAsState(targetValue = if (isSelected) 3.dp else 0.dp, label = "tx")
    val translationY by animateDpAsState(targetValue = if (isSelected) 3.dp else 0.dp, label = "ty")
    
    val baseShadowOffset = if (isSelected) 1.dp else 4.dp
    val activeShadowOffset = if (isIdle && !isSelected) baseShadowOffset * localShadowMult * shadowPulseFactor else baseShadowOffset
    val activeRotation = if (isIdle && !isSelected) localWiggle * 0.7f * rotationMultiplier * direction else 0f
    val bg = if (isSelected) NeoYellow else Color.White

    Box(
        modifier = modifier
            .offset(x = translationX, y = translationY)
            .graphicsLayer {
                rotationZ = activeRotation
            }
            .clickable(onClick = onClick)
            .drawBehind {
                if (activeShadowOffset > 0.dp) {
                    drawRoundRect(
                        color = Color.Black,
                        topLeft = Offset(activeShadowOffset.toPx(), activeShadowOffset.toPx()),
                        size = size,
                        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                    )
                }
            }
            .background(bg, RoundedCornerShape(10.dp))
            .border(2.5.dp, Color.Black, RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
        )
    }
}

@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "0",
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(10.dp))
                .border(2.5.dp, Color.Black, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun NeoWordProblemField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    maxLines: Int = 5,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(10.dp))
                .border(2.5.dp, Color.Black, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                maxLines = maxLines,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CalcDisplay(
    expression: String,
    result: String,
    isDegreeMode: Boolean,
    onToggleDegree: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeoBrutalCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color.White,
        shadowOffset = 6.dp,
        cornerRadius = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // DEG/RAD & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clickable(onClick = onToggleDegree)
                        .background(if (isDegreeMode) NeoGreen else NeoPurple, RoundedCornerShape(8.dp))
                        .border(2.2.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isDegreeMode) "DEG" else "RAD",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }

                Text(
                    text = "NEO•CALC",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    letterSpacing = 1.sp
                )

                IconButton(
                    onClick = onClear,
                    modifier = Modifier
                        .size(26.dp)
                        .background(NeoOrange, RoundedCornerShape(6.dp))
                        .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_neo_refresh),
                        contentDescription = "Clear",
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expression scrolling row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                val scrollState = rememberScrollState()
                LaunchedEffect(expression) {
                    scrollState.animateScrollTo(scrollState.maxValue)
                }
                Text(
                    text = expression.ifEmpty { " " },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.horizontalScroll(scrollState)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Giant Result View
            Text(
                text = result.ifEmpty { "0" },
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ScientificDock(onScientificKey: (String) -> Unit) {
    val keys = listOf(
        "sin(", "cos(", "tan(", "asin(", "acos(", "atan(",
        "ln(", "log(", "log2(", "sqrt(", "cbrt(", "abs(",
        "nCr(", "nPr(", "gcd(", "lcm(", "pi", "e"
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(keys) { key ->
            Box(
                modifier = Modifier
                    .clickable { onScientificKey(key) }
                    .background(NeoPurple.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = key.replace("(", ""),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}
