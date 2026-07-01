package com.example.ui.screens.formulas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.CalculatorViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun FormulasLeftScreen(viewModel: CalculatorViewModel) {
    val formulaType by viewModel.formulaType.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val formulas = listOf("Hukum Ohm", "Geometri Lingkaran", "Pythagoras", "Status BMI")
        formulas.forEachIndexed { index, name ->
            val isSel = formulaType == index
            val bg = if (isSel) NeoYellow else Color.White

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable { viewModel.setFormulaType(index) }
                    .background(bg, RoundedCornerShape(10.dp))
                    .border(if (isSel) 3.dp else 1.5.dp, Color.Black, RoundedCornerShape(10.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(name, fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun FormulasTabPortrait(viewModel: CalculatorViewModel) {
    val formulaType by viewModel.formulaType.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val tabs = listOf("Ohm", "Bulat", "Siku", "BMI")
            tabs.forEachIndexed { index, name ->
                NeoTabButton(
                    text = name,
                    isSelected = formulaType == index,
                    onClick = { viewModel.setFormulaType(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (formulaType) {
                0 -> OhmPanel(viewModel)
                1 -> CirclePanel(viewModel)
                2 -> PythagPanel(viewModel)
                3 -> BmiPanel(viewModel)
            }
        }
    }
}

@Composable
fun FormulasTabLandscape(viewModel: CalculatorViewModel) {
    val formulaType by viewModel.formulaType.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        when (formulaType) {
            0 -> OhmPanel(viewModel)
            1 -> CirclePanel(viewModel)
            2 -> PythagPanel(viewModel)
            3 -> BmiPanel(viewModel)
        }
    }
}

@Composable
fun OhmPanel(viewModel: CalculatorViewModel) {
    val v by viewModel.ohmVoltage.collectAsState()
    val i by viewModel.ohmCurrent.collectAsState()
    val r by viewModel.ohmResistance.collectAsState()
    val res by viewModel.ohmResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        NeoBrutalCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = NeoWhite
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Kalkulator Hukum Ohm (V = I × R)", fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("Isi pas 2 kolom aja buat nyari nilai yang ke-3, cuy:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NeoTextField(value = v, onValueChange = { viewModel.updateOhmInputs(it, i, r) }, label = "Tegangan V (Volt)", modifier = Modifier.weight(1f))
                    NeoTextField(value = i, onValueChange = { viewModel.updateOhmInputs(v, it, r) }, label = "Arus I (Ampere)", modifier = Modifier.weight(1f))
                    NeoTextField(value = r, onValueChange = { viewModel.updateOhmInputs(v, i, it) }, label = "Hambatan R (Ω)", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NeoBrutalButton(text = "HITUNG", onClick = { viewModel.solveOhm() }, backgroundColor = NeoYellow, modifier = Modifier.weight(1f))
                    NeoBrutalButton(text = "BERSIHIN", onClick = { viewModel.clearOhm() }, backgroundColor = NeoOrange, modifier = Modifier.weight(1f))
                }
            }
        }

        if (res.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            NeoBrutalCard(modifier = Modifier.fillMaxWidth(), backgroundColor = NeoBg) {
                Text(res, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Black, fontSize = 13.sp, color = NeoOrange)
            }
        }
    }
}

@Composable
fun CirclePanel(viewModel: CalculatorViewModel) {
    val radius by viewModel.circleRadius.collectAsState()
    val area by viewModel.circleArea.collectAsState()
    val perimeter by viewModel.circlePerimeter.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        NeoBrutalCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = NeoWhite
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Geometri Lingkaran 🔵", fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("Masukin jari-jari buat nyari luas & keliling:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))

                NeoTextField(
                    value = radius,
                    onValueChange = { viewModel.updateCircleRadius(it) },
                    label = "Jari-jari (r)",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (area.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            NeoBrutalCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeoBg
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Rumus & Hasil Lingkaran:", fontWeight = FontWeight.Black, fontSize = 12.sp, color = NeoPurple)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Luas L = πr² = π × $radius² = $area satuan luas", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Keliling K = 2πr = 2 × π × $radius = $perimeter satuan panjang", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun PythagPanel(viewModel: CalculatorViewModel) {
    val pA by viewModel.pythagA.collectAsState()
    val pB by viewModel.pythagB.collectAsState()
    val pC by viewModel.pythagC.collectAsState()
    val res by viewModel.pythagResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        NeoBrutalCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = NeoWhite
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Teorema Pythagoras (a² + b² = c²)", fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("Masukin 2 nilai aja biar dapet sisi ketiganya, cuy:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NeoTextField(value = pA, onValueChange = { viewModel.updatePythagInputs(it, pB, pC) }, label = "Sisi Tegak a", modifier = Modifier.weight(1f))
                    NeoTextField(value = pB, onValueChange = { viewModel.updatePythagInputs(pA, it, pC) }, label = "Sisi Alas b", modifier = Modifier.weight(1f))
                    NeoTextField(value = pC, onValueChange = { viewModel.updatePythagInputs(pA, pB, it) }, label = "Sisi Miring c", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NeoBrutalButton(text = "HITUNG", onClick = { viewModel.solvePythag() }, backgroundColor = NeoCyan, modifier = Modifier.weight(1f))
                    NeoBrutalButton(text = "BERSIHIN", onClick = { viewModel.clearPythag() }, backgroundColor = NeoPink, modifier = Modifier.weight(1f))
                }
            }
        }

        if (res.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            NeoBrutalCard(modifier = Modifier.fillMaxWidth(), backgroundColor = NeoBg) {
                Text(res, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Black, fontSize = 13.sp, color = NeoPink)
            }
        }
    }
}

@Composable
fun BmiPanel(viewModel: CalculatorViewModel) {
    val weight by viewModel.bmiWeight.collectAsState()
    val height by viewModel.bmiHeight.collectAsState()
    val score by viewModel.bmiResultVal.collectAsState()
    val category by viewModel.bmiResultCategory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        NeoBrutalCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = NeoWhite
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Indeks Massa Tubuh (BMI)", fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("Masukin data badan lu di bawah:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NeoTextField(value = weight, onValueChange = { viewModel.updateBmiInputs(it, height) }, label = "Berat Badan (kg)", modifier = Modifier.weight(1f))
                    NeoTextField(value = height, onValueChange = { viewModel.updateBmiInputs(weight, it) }, label = "Tinggi Badan (cm)", modifier = Modifier.weight(1f))
                }
            }
        }

        if (score.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            NeoBrutalCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeoBg
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Hasil Analisis BMI Lu:", fontWeight = FontWeight.Black, fontSize = 12.sp, color = NeoGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Skor BMI: $score kg/m²", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(2.dp))

                    val (color, desc) = when (category) {
                        "Underweight" -> Pair(NeoCyan, "Kurus bat, cungkring cuy! (< 18.5)")
                        "Normal weight" -> Pair(NeoGreen, "Mantap, berat lu normal cuy! (18.5 - 24.9)")
                        "Overweight" -> Pair(NeoYellow, "Waduh, agak gemuk nih bro! (25.0 - 29.9)")
                        else -> Pair(NeoOrange, "Bahaya cuy, udah obesitas nih! (≥ 30.0)")
                    }

                    Box(
                        modifier = Modifier
                            .background(color, RoundedCornerShape(8.dp))
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(desc, fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color.Black)
                    }
                }
            }
        }
    }
}
