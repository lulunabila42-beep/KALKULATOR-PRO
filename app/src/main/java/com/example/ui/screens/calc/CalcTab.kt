package com.example.ui.screens.calc

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.CalculatorViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun CalcTabPortrait(viewModel: CalculatorViewModel) {
    val expression by viewModel.expression.collectAsState()
    val result by viewModel.result.collectAsState()
    val isDegreeMode by viewModel.isDegreeMode.collectAsState()
    val history by viewModel.history.collectAsState()

    var showHistory by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        CalcDisplay(
            expression = expression,
            result = result,
            isDegreeMode = isDegreeMode,
            onToggleDegree = { viewModel.toggleDegreeMode() },
            onClear = { viewModel.clearExpression() },
            modifier = Modifier.weight(0.24f)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Horizontal scientific dock
        ScientificDock(onScientificKey = { key -> viewModel.appendExpression(key) })

        Spacer(modifier = Modifier.height(4.dp))

        if (showHistory && history.isNotEmpty()) {
            // History list inside a Card
            NeoBrutalCard(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxWidth(),
                backgroundColor = NeoWhite
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("History Log", fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text(
                            "Close",
                            color = NeoPink,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { showHistory = false }
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color.Black, thickness = 1.dp)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(history) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.useHistoryItem(item)
                                        showHistory = false
                                    }
                                    .background(NeoBg.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Text(item.expression, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(item.result, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }
        } else {
            // Standard Keyboard Grid in Portrait (5 Columns)
            val buttons = listOf(
                listOf("MC", "MR", "M+", "M-", "H"),
                listOf("AC", "DEL", "(", ")", "÷"),
                listOf("7", "8", "9", "×", "^"),
                listOf("4", "5", "6", "−", "mod"),
                listOf("1", "2", "3", "+", "%"),
                listOf("0", ".", "e", "pi", "=")
            )

            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                buttons.forEach { row ->
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        row.forEach { char ->
                            val isAction = char == "AC" || char == "DEL"
                            val isOperator = char == "÷" || char == "×" || char == "−" || char == "+" || char == "mod" || char == "^" || char == "%"
                            val isMemory = char == "MC" || char == "MR" || char == "M+" || char == "M-" || char == "H"
                            val isEquals = char == "="

                            val bg = when {
                                isAction -> if (char == "AC") NeoOrange else NeoPink
                                isEquals -> Color.Black
                                isOperator -> NeoCyan
                                isMemory -> NeoYellow
                                else -> Color.White
                            }

                            val textCol = if (isEquals) Color.White else Color.Black

                            NeoBrutalButton(
                                text = char,
                                onClick = {
                                    when (char) {
                                        "AC" -> viewModel.clearExpression()
                                        "DEL" -> viewModel.deleteLastChar()
                                        "=" -> viewModel.evaluate()
                                        "H" -> { showHistory = true }
                                        "MC" -> viewModel.memoryClear()
                                        "MR" -> viewModel.memoryRecall()
                                        "M+" -> viewModel.memoryAdd()
                                        "M-" -> viewModel.memorySubtract()
                                        else -> viewModel.appendExpression(char)
                                    }
                                },
                                backgroundColor = bg,
                                textColor = textCol,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                iconRes = getNeoIconForChar(char)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalcLeftScreen(viewModel: CalculatorViewModel) {
    val expression by viewModel.expression.collectAsState()
    val result by viewModel.result.collectAsState()
    val isDegreeMode by viewModel.isDegreeMode.collectAsState()
    val memory by viewModel.memory.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        CalcDisplay(
            expression = expression,
            result = result,
            isDegreeMode = isDegreeMode,
            onToggleDegree = { viewModel.toggleDegreeMode() },
            onClear = { viewModel.clearExpression() },
            modifier = Modifier.weight(0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        NeoBrutalCard(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxWidth(),
            backgroundColor = NeoWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Memory Store:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text(
                    text = String.format(Locale.US, "%.4f", memory).replace("0*$".toRegex(), "").replace("\\.$".toRegex(), ""),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val actions = listOf("MC", "MR", "M+", "M-")
                    actions.forEach { action ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    when (action) {
                                        "MC" -> viewModel.memoryClear()
                                        "MR" -> viewModel.memoryRecall()
                                        "M+" -> viewModel.memoryAdd()
                                        "M-" -> viewModel.memorySubtract()
                                    }
                                }
                                .background(NeoYellow, RoundedCornerShape(6.dp))
                                .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(action, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalcTabLandscape(viewModel: CalculatorViewModel) {
    // 8 columns, 5 rows scientific layout
    val landscapeButtons = listOf(
        listOf("sin(", "cos(", "tan(", "AC", "DEL", "(", ")", "÷"),
        listOf("asin(", "acos(", "atan(", "7", "8", "9", "×", "^"),
        listOf("ln(", "log(", "log2(", "4", "5", "6", "−", "√"),
        listOf("sinh(", "cosh(", "tanh(", "1", "2", "3", "+", "mod"),
        listOf("pi", "e", "!", "0", ".", "%", "H", "=")
    )

    val history by viewModel.history.collectAsState()
    var showHistory by rememberSaveable { mutableStateOf(false) }

    if (showHistory && history.isNotEmpty()) {
        NeoBrutalCard(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = NeoWhite
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Calculation History", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Text(
                        "Close History",
                        color = NeoPink,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { showHistory = false }
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color.Black, thickness = 1.dp)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(history) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.useHistoryItem(item)
                                    showHistory = false
                                }
                                .background(NeoBg.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.expression, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(item.result, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            landscapeButtons.forEach { row ->
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row.forEach { char ->
                        val isSci = char.endsWith("(") || char == "pi" || char == "e" || char == "!" || char == "√"
                        val isAction = char == "AC" || char == "DEL" || char == "H"
                        val isOperator = char == "÷" || char == "×" || char == "−" || char == "+" || char == "mod" || char == "^" || char == "%"
                        val isEquals = char == "="

                        val bg = when {
                            isAction -> if (char == "AC") NeoOrange else NeoPink
                            isEquals -> Color.Black
                            isOperator -> NeoCyan
                            isSci -> NeoPurple
                            else -> Color.White
                        }

                        val textCol = if (isEquals) Color.White else Color.Black

                        NeoBrutalButton(
                            text = char.replace("(", ""),
                            onClick = {
                                when (char) {
                                    "AC" -> viewModel.clearExpression()
                                    "DEL" -> viewModel.deleteLastChar()
                                    "=" -> viewModel.evaluate()
                                    "H" -> { showHistory = true }
                                    else -> viewModel.appendExpression(char)
                                }
                            },
                            backgroundColor = bg,
                            textColor = textCol,
                            fontSize = 15,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            iconRes = getNeoIconForChar(char.replace("(", ""))
                        )
                    }
                }
            }
        }
    }
}
