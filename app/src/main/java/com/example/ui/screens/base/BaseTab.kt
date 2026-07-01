package com.example.ui.screens.base

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.CalculatorViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun BaseLeftScreen(viewModel: CalculatorViewModel) {
    val selectedBase by viewModel.selectedBase.collectAsState()
    val hexVal by viewModel.hexValue.collectAsState()
    val decVal by viewModel.decValue.collectAsState()
    val octVal by viewModel.octValue.collectAsState()
    val binVal by viewModel.binValue.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val bases = listOf(
            Triple(CalculatorViewModel.BaseType.HEX, "HEX", hexVal),
            Triple(CalculatorViewModel.BaseType.DEC, "DEC", decVal),
            Triple(CalculatorViewModel.BaseType.OCT, "OCT", octVal),
            Triple(CalculatorViewModel.BaseType.BIN, "BIN", binVal)
        )

        bases.forEach { (type, label, value) ->
            val isSel = selectedBase == type
            val bg = if (isSel) NeoYellow else Color.White
            val borderW = if (isSel) 3.dp else 1.5.dp

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable { viewModel.setBaseType(type) }
                    .background(bg, RoundedCornerShape(10.dp))
                    .border(borderW, Color.Black, RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Text(label, fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Gray)
                    Text(
                        value,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun BaseKeypad(viewModel: CalculatorViewModel, selectedBase: CalculatorViewModel.BaseType) {
    val buttons = listOf(
        listOf("D", "E", "F", "AC"),
        listOf("A", "B", "C", "DEL"),
        listOf("7", "8", "9", "4"),
        listOf("5", "6", "1", "2"),
        listOf("3", "0", "00", "000")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { char ->
                    val isAction = char == "AC" || char == "DEL"
                    val isLetter = char in listOf("A", "B", "C", "D", "E", "F")

                    val isEnabled = when (selectedBase) {
                        CalculatorViewModel.BaseType.HEX -> true
                        CalculatorViewModel.BaseType.DEC -> !isLetter
                        CalculatorViewModel.BaseType.OCT -> !isLetter && char != "8" && char != "9"
                        CalculatorViewModel.BaseType.BIN -> char == "0" || char == "1" || char == "00" || char == "000" || isAction
                    }

                    val bg = when {
                        isAction -> if (char == "AC") NeoOrange else NeoPink
                        isLetter -> NeoPurple
                        else -> Color.White
                    }

                    NeoBrutalButton(
                        text = char,
                        onClick = {
                            when (char) {
                                "AC" -> viewModel.clearBaseConverter()
                                "DEL" -> viewModel.deleteBaseChar()
                                else -> viewModel.inputBaseChar(char)
                            }
                        },
                        isEnabled = isEnabled,
                        backgroundColor = bg,
                        fontSize = 15,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        iconRes = getNeoIconForChar(char)
                    )
                }
            }
        }
    }
}

@Composable
fun BaseTabPortrait(viewModel: CalculatorViewModel) {
    val selectedBase by viewModel.selectedBase.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(0.35f)) {
            BaseLeftScreen(viewModel)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.weight(0.65f)) {
            BaseKeypad(viewModel, selectedBase)
        }
    }
}

@Composable
fun BaseTabLandscape(viewModel: CalculatorViewModel) {
    val selectedBase by viewModel.selectedBase.collectAsState()
    BaseKeypad(viewModel, selectedBase)
}
