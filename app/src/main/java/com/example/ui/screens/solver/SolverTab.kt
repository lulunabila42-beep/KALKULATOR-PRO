package com.example.ui.screens.solver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun SolverLeftScreen(viewModel: CalculatorViewModel) {
    val solverType by viewModel.solverType.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        NeoBrutalCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            backgroundColor = NeoWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Setelan Solver Cuy", fontWeight = FontWeight.Black, fontSize = 14.sp)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    NeoBrutalButton(
                        text = "Kuadrat ax²+bx+c 📉",
                        onClick = { viewModel.setSolverType(0) },
                        backgroundColor = if (solverType == 0) NeoYellow else Color.White,
                        fontSize = 12
                    )
                    NeoBrutalButton(
                        text = "Sistem Linear 2x2 📊",
                        onClick = { viewModel.setSolverType(1) },
                        backgroundColor = if (solverType == 1) NeoYellow else Color.White,
                        fontSize = 12
                    )
                }

                Text(
                    "Massive parser calculates roots instantly.",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SolverTabPortrait(viewModel: CalculatorViewModel) {
    val solverType by viewModel.solverType.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Toggle Buttons at Top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NeoBrutalButton(
                text = "Persamaan Kuadrat 📉",
                onClick = { viewModel.setSolverType(0) },
                backgroundColor = if (solverType == 0) NeoYellow else Color.White,
                fontSize = 12,
                modifier = Modifier.weight(1f)
            )
            NeoBrutalButton(
                text = "Sistem Linear 2x2 📊",
                onClick = { viewModel.setSolverType(1) },
                backgroundColor = if (solverType == 1) NeoYellow else Color.White,
                fontSize = 12,
                modifier = Modifier.weight(1f)
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (solverType == 0) {
                QuadraticPanel(viewModel)
            } else {
                SystemPanel(viewModel)
            }
        }
    }
}

@Composable
fun SolverTabLandscape(viewModel: CalculatorViewModel) {
    val solverType by viewModel.solverType.collectAsState()
    if (solverType == 0) {
        QuadraticPanel(viewModel)
    } else {
        SystemPanel(viewModel)
    }
}

@Composable
fun QuadraticPanel(viewModel: CalculatorViewModel) {
    val qA by viewModel.quadA.collectAsState()
    val qB by viewModel.quadB.collectAsState()
    val qC by viewModel.quadC.collectAsState()
    val qResult by viewModel.quadResult.collectAsState()

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
                Text("Bentuk Persamaan: ax² + bx + c = 0 📉", fontWeight = FontWeight.Black, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    NeoTextField(
                        value = qA,
                        onValueChange = { viewModel.updateQuadInputs(it, qB, qC) },
                        label = "Koef a",
                        modifier = Modifier.weight(1f)
                    )
                    NeoTextField(
                        value = qB,
                        onValueChange = { viewModel.updateQuadInputs(qA, it, qC) },
                        label = "Koef b",
                        modifier = Modifier.weight(1f)
                    )
                    NeoTextField(
                        value = qC,
                        onValueChange = { viewModel.updateQuadInputs(qA, qB, it) },
                        label = "Koef c",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                NeoBrutalButton(
                    text = "PECAHIN PERSAMAAN",
                    onClick = { viewModel.solveQuadratic() },
                    backgroundColor = NeoPink,
                    textColor = Color.Black
                )
            }
        }

        if (qResult.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            NeoBrutalCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeoBg
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Langkah Penyelesaian Cuy:", fontWeight = FontWeight.Black, fontSize = 12.sp, color = NeoPink)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = qResult,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun SystemPanel(viewModel: CalculatorViewModel) {
    val a1 by viewModel.sysA1.collectAsState()
    val b1 by viewModel.sysB1.collectAsState()
    val c1 by viewModel.sysC1.collectAsState()
    val a2 by viewModel.sysA2.collectAsState()
    val b2 by viewModel.sysB2.collectAsState()
    val c2 by viewModel.sysC2.collectAsState()
    val sysResult by viewModel.sysResult.collectAsState()

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
                Text("Sistem Persamaan Linear (2x2) 📊:", fontWeight = FontWeight.Black, fontSize = 13.sp)
                Text("Pers 1: a₁x + b₁y = c₁\nPers 2: a₂x + b₂y = c₂", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                Text("Koefisien Persamaan 1", fontSize = 10.sp, fontWeight = FontWeight.Black)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    NeoTextField(value = a1, onValueChange = { viewModel.updateSystemInputs(it, b1, c1, a2, b2, c2) }, label = "a₁", modifier = Modifier.weight(1f))
                    NeoTextField(value = b1, onValueChange = { viewModel.updateSystemInputs(a1, it, c1, a2, b2, c2) }, label = "b₁", modifier = Modifier.weight(1f))
                    NeoTextField(value = c1, onValueChange = { viewModel.updateSystemInputs(a1, b1, it, a2, b2, c2) }, label = "c₁", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text("Koefisien Persamaan 2", fontSize = 10.sp, fontWeight = FontWeight.Black)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    NeoTextField(value = a2, onValueChange = { viewModel.updateSystemInputs(a1, b1, c1, it, b2, c2) }, label = "a₂", modifier = Modifier.weight(1f))
                    NeoTextField(value = b2, onValueChange = { viewModel.updateSystemInputs(a1, b1, c1, a2, it, c2) }, label = "b₂", modifier = Modifier.weight(1f))
                    NeoTextField(value = c2, onValueChange = { viewModel.updateSystemInputs(a1, b1, c1, a2, b2, it) }, label = "c₂", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(10.dp))

                NeoBrutalButton(
                    text = "PECAHIN SISTEM PERSAMAAN",
                    onClick = { viewModel.solveSystem() },
                    backgroundColor = NeoGreen
                )
            }
        }

        if (sysResult.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            NeoBrutalCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeoBg
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Langkah Aturan Cramer Cuy:", fontWeight = FontWeight.Black, fontSize = 12.sp, color = NeoGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = sysResult,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
