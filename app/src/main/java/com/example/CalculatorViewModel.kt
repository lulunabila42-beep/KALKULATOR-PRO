package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import kotlin.math.*

// Represents historical equation
data class HistoryItem(val expression: String, val result: String, val timestamp: Long = System.currentTimeMillis())

class CalculatorViewModel : ViewModel() {

    // Main Calculator State
    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    private val _isDegreeMode = MutableStateFlow(true)
    val isDegreeMode: StateFlow<Boolean> = _isDegreeMode.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    private val _memory = MutableStateFlow(0.0)
    val memory: StateFlow<Double> = _memory.asStateFlow()

    // Active Tab State: 0 = CALC, 1 = CONV, 2 = SOLVER, 3 = FORMULAS
    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    // 1. BASE CONVERTER STATE
    enum class BaseType { DEC, HEX, OCT, BIN }
    private val _selectedBase = MutableStateFlow(BaseType.DEC)
    val selectedBase: StateFlow<BaseType> = _selectedBase.asStateFlow()

    private val _baseInput = MutableStateFlow("0")
    val baseInput: StateFlow<String> = _baseInput.asStateFlow()

    private val _hexValue = MutableStateFlow("0")
    val hexValue: StateFlow<String> = _hexValue.asStateFlow()

    private val _decValue = MutableStateFlow("0")
    val decValue: StateFlow<String> = _decValue.asStateFlow()

    private val _octValue = MutableStateFlow("0")
    val octValue: StateFlow<String> = _octValue.asStateFlow()

    private val _binValue = MutableStateFlow("0")
    val binValue: StateFlow<String> = _binValue.asStateFlow()

    // 2. EQUATION SOLVER STATE
    // Quadratic Solver: ax^2 + bx + c = 0
    private val _quadA = MutableStateFlow("")
    val quadA: StateFlow<String> = _quadA.asStateFlow()
    private val _quadB = MutableStateFlow("")
    val quadB: StateFlow<String> = _quadB.asStateFlow()
    private val _quadC = MutableStateFlow("")
    val quadC: StateFlow<String> = _quadC.asStateFlow()

    private val _quadResult = MutableStateFlow("")
    val quadResult: StateFlow<String> = _quadResult.asStateFlow()

    // System Solver (2x2): a1 x + b1 y = c1, a2 x + b2 y = c2
    private val _sysA1 = MutableStateFlow("")
    val sysA1: StateFlow<String> = _sysA1.asStateFlow()
    private val _sysB1 = MutableStateFlow("")
    val sysB1: StateFlow<String> = _sysB1.asStateFlow()
    private val _sysC1 = MutableStateFlow("")
    val sysC1: StateFlow<String> = _sysC1.asStateFlow()
    private val _sysA2 = MutableStateFlow("")
    val sysA2: StateFlow<String> = _sysA2.asStateFlow()
    private val _sysB2 = MutableStateFlow("")
    val sysB2: StateFlow<String> = _sysB2.asStateFlow()
    private val _sysC2 = MutableStateFlow("")
    val sysC2: StateFlow<String> = _sysC2.asStateFlow()

    private val _sysResult = MutableStateFlow("")
    val sysResult: StateFlow<String> = _sysResult.asStateFlow()

    // Solver type: 0 = Quadratic, 1 = 2x2 System
    private val _solverType = MutableStateFlow(0)
    val solverType: StateFlow<Int> = _solverType.asStateFlow()

    // 3. FORMULAS SHEET STATE
    // Formula type: 0 = Ohm's Law, 1 = Circle Geometry, 2 = Pythagorean, 3 = BMI
    private val _formulaType = MutableStateFlow(0)
    val formulaType: StateFlow<Int> = _formulaType.asStateFlow()

    // Ohm's law: V = I * R (Input 2 to solve the 3rd)
    private val _ohmVoltage = MutableStateFlow("")
    val ohmVoltage: StateFlow<String> = _ohmVoltage.asStateFlow()
    private val _ohmCurrent = MutableStateFlow("")
    val ohmCurrent: StateFlow<String> = _ohmCurrent.asStateFlow()
    private val _ohmResistance = MutableStateFlow("")
    val ohmResistance: StateFlow<String> = _ohmResistance.asStateFlow()
    private val _ohmResult = MutableStateFlow("")
    val ohmResult: StateFlow<String> = _ohmResult.asStateFlow()

    // Circle Geometry
    private val _circleRadius = MutableStateFlow("")
    val circleRadius: StateFlow<String> = _circleRadius.asStateFlow()
    private val _circleArea = MutableStateFlow("")
    val circleArea: StateFlow<String> = _circleArea.asStateFlow()
    private val _circlePerimeter = MutableStateFlow("")
    val circlePerimeter: StateFlow<String> = _circlePerimeter.asStateFlow()

    // Pythagorean Theorem: a^2 + b^2 = c^2
    private val _pythagA = MutableStateFlow("")
    val pythagA: StateFlow<String> = _pythagA.asStateFlow()
    private val _pythagB = MutableStateFlow("")
    val pythagB: StateFlow<String> = _pythagB.asStateFlow()
    private val _pythagC = MutableStateFlow("")
    val pythagC: StateFlow<String> = _pythagC.asStateFlow()
    private val _pythagResult = MutableStateFlow("")
    val pythagResult: StateFlow<String> = _pythagResult.asStateFlow()

    // BMI Calculator
    private val _bmiWeight = MutableStateFlow("")
    val bmiWeight: StateFlow<String> = _bmiWeight.asStateFlow()
    private val _bmiHeight = MutableStateFlow("")
    val bmiHeight: StateFlow<String> = _bmiHeight.asStateFlow()
    private val _bmiResultVal = MutableStateFlow("")
    val bmiResultVal: StateFlow<String> = _bmiResultVal.asStateFlow()
    private val _bmiResultCategory = MutableStateFlow("")
    val bmiResultCategory: StateFlow<String> = _bmiResultCategory.asStateFlow()


    // --- MAIN CALCULATOR LOGIC ---

    fun setActiveTab(tab: Int) {
        _activeTab.value = tab
    }

    fun appendExpression(value: String) {
        val current = _expression.value
        // If result was shown and we enter a number/constant, clear first. 
        // If we enter an operator, append to the existing result!
        if (_result.value.isNotEmpty() && _result.value != "Error" && current.isEmpty()) {
            if (isOperator(value) || value.startsWith("^") || value == "%" || value == "!") {
                _expression.value = _result.value + value
                _result.value = ""
                return
            } else {
                _result.value = ""
            }
        } else if (_result.value == "Error") {
            _result.value = ""
        }

        _expression.value = current + value
    }

    private fun isOperator(c: String): Boolean {
        return c == "+" || c == "−" || c == "×" || c == "÷" || c == "mod"
    }

    fun clearExpression() {
        _expression.value = ""
        _result.value = ""
    }

    fun deleteLastChar() {
        val current = _expression.value
        if (current.isNotEmpty()) {
            // Check if we are deleting a function word (like "sin(", "cos(", etc.)
            val functions = listOf(
                "sin(", "cos(", "tan(", "asin(", "acos(", "atan(",
                "sinh(", "cosh(", "tanh(", "log2(", "log(", "ln(",
                "sqrt(", "cbrt(", "abs(", "nCr(", "nPr(", "gcd(", "lcm("
            )
            var deletedFunc = false
            for (f in functions) {
                if (current.endsWith(f)) {
                    _expression.value = current.substring(0, current.length - f.length)
                    deletedFunc = true
                    break
                }
            }
            if (!deletedFunc) {
                _expression.value = current.substring(0, current.length - 1)
            }
        }
    }

    fun toggleDegreeMode() {
        _isDegreeMode.value = !_isDegreeMode.value
    }

    fun evaluate() {
        val expr = _expression.value
        if (expr.isEmpty()) return
        try {
            val parser = CalculatorParser(isDegreeMode = _isDegreeMode.value)
            val evalResult = parser.parse(expr)
            
            // Format result: remove trailing .0 if integer
            val formattedResult = formatResultValue(evalResult)
            _result.value = formattedResult
            
            // Add to history
            val newHistory = _history.value.toMutableList()
            newHistory.add(0, HistoryItem(expr, formattedResult))
            _history.value = newHistory
            
            _expression.value = "" // clear expression for next inputs, retaining result as a reference
        } catch (e: Exception) {
            _result.value = "Error"
        }
    }

    private fun formatResultValue(value: Double): String {
        if (value.isInfinite() || value.isNaN()) return "Error"
        val intVal = value.toLong()
        return if (value == intVal.toDouble()) {
            intVal.toString()
        } else {
            // Format decimal places
            val formatted = String.format(Locale.US, "%.8f", value)
            formatted.replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
        }
    }

    // Memory operations
    fun memoryClear() {
        _memory.value = 0.0
    }

    fun memoryRecall() {
        val memStr = formatResultValue(_memory.value)
        appendExpression(memStr)
    }

    fun memoryAdd() {
        val currentRes = _result.value.toDoubleOrNull() ?: _expression.value.toDoubleOrNull()
        if (currentRes != null) {
            _memory.value += currentRes
        }
    }

    fun memorySubtract() {
        val currentRes = _result.value.toDoubleOrNull() ?: _expression.value.toDoubleOrNull()
        if (currentRes != null) {
            _memory.value -= currentRes
        }
    }

    fun clearHistory() {
        _history.value = emptyList()
    }

    fun useHistoryItem(item: HistoryItem) {
        _expression.value = item.expression
        _result.value = item.result
    }


    // --- 1. BASE CONVERTER LOGIC ---

    fun setBaseType(base: BaseType) {
        _selectedBase.value = base
        updateBaseValues(_baseInput.value, base)
    }

    fun inputBaseChar(char: String) {
        var current = _baseInput.value
        if (current == "0") {
            current = if (char == ".") "0." else char
        } else {
            current += char
        }
        _baseInput.value = current
        updateBaseValues(current, _selectedBase.value)
    }

    fun clearBaseConverter() {
        _baseInput.value = "0"
        updateBaseValues("0", _selectedBase.value)
    }

    fun deleteBaseChar() {
        val current = _baseInput.value
        if (current.isNotEmpty() && current != "0") {
            _baseInput.value = if (current.length == 1) "0" else current.substring(0, current.length - 1)
        } else {
            _baseInput.value = "0"
        }
        updateBaseValues(_baseInput.value, _selectedBase.value)
    }

    private fun updateBaseValues(input: String, base: BaseType) {
        try {
            val decimalValue: Long = when (base) {
                BaseType.DEC -> input.toLongOrNull(10) ?: 0L
                BaseType.HEX -> input.toLongOrNull(16) ?: 0L
                BaseType.OCT -> input.toLongOrNull(8) ?: 0L
                BaseType.BIN -> input.toLongOrNull(2) ?: 0L
            }

            _decValue.value = decimalValue.toString(10)
            _hexValue.value = decimalValue.toString(16).uppercase()
            _octValue.value = decimalValue.toString(8)
            _binValue.value = decimalValue.toString(2)
        } catch (e: Exception) {
            _decValue.value = "Error"
            _hexValue.value = "Error"
            _octValue.value = "Error"
            _binValue.value = "Error"
        }
    }


    // --- 2. EQUATION SOLVER LOGIC ---

    fun setSolverType(type: Int) {
        _solverType.value = type
    }

    fun updateQuadInputs(a: String, b: String, c: String) {
        _quadA.value = a
        _quadB.value = b
        _quadC.value = c
    }

    fun solveQuadratic() {
        val a = _quadA.value.toDoubleOrNull()
        val b = _quadB.value.toDoubleOrNull()
        val c = _quadC.value.toDoubleOrNull()

        if (a == null || b == null || c == null) {
            _quadResult.value = "Please enter valid numbers for a, b, and c."
            return
        }
        if (a == 0.0) {
            _quadResult.value = "Coefficient 'a' cannot be 0 in a quadratic equation."
            return
        }

        val discriminant = b * b - 4 * a * c
        val steps = StringBuilder()
        steps.append("Equation: ${formatDouble(a)}x² + ${formatDouble(b)}x + ${formatDouble(c)} = 0\n\n")
        steps.append("Step 1: Compute discriminant D = b² - 4ac\n")
        steps.append("D = (${formatDouble(b)})² - 4 * (${formatDouble(a)}) * (${formatDouble(c)})\n")
        steps.append("D = ${formatDouble(b*b)} - ${formatDouble(4*a*c)} = ${formatDouble(discriminant)}\n\n")

        steps.append("Step 2: Solve for x = (-b ± √D) / 2a\n")
        if (discriminant > 0) {
            val rootD = sqrt(discriminant)
            val x1 = (-b + rootD) / (2 * a)
            val x2 = (-b - rootD) / (2 * a)
            steps.append("D > 0, so there are two distinct real roots:\n")
            steps.append("x₁ = (-($b) + ${formatDouble(rootD)}) / (2 * $a) = ${formatDouble(x1)}\n")
            steps.append("x₂ = (-($b) - ${formatDouble(rootD)}) / (2 * $a) = ${formatDouble(x2)}\n\n")
            steps.append("Roots:\nx₁ = ${formatDouble(x1)}\nx₂ = ${formatDouble(x2)}")
        } else if (discriminant == 0.0) {
            val x = -b / (2 * a)
            steps.append("D = 0, so there is one repeated real root:\n")
            steps.append("x = -$b / (2 * $a) = ${formatDouble(x)}\n\n")
            steps.append("Root:\nx = ${formatDouble(x)}")
        } else {
            // Complex roots
            val realPart = -b / (2 * a)
            val imagPart = sqrt(-discriminant) / (2 * a)
            steps.append("D < 0, so there are complex conjugate roots:\n")
            steps.append("x₁ = ${formatDouble(realPart)} + ${formatDouble(imagPart)}i\n")
            steps.append("x₂ = ${formatDouble(realPart)} - ${formatDouble(imagPart)}i\n\n")
            steps.append("Roots:\nx₁ = ${formatDouble(realPart)} + ${formatDouble(imagPart)}i\nx₂ = ${formatDouble(realPart)} - ${formatDouble(imagPart)}i")
        }

        _quadResult.value = steps.toString()
    }

    fun updateSystemInputs(a1: String, b1: String, c1: String, a2: String, b2: String, c2: String) {
        _sysA1.value = a1
        _sysB1.value = b1
        _sysC1.value = c1
        _sysA2.value = a2
        _sysB2.value = b2
        _sysC2.value = c2
    }

    fun solveSystem() {
        val a1 = _sysA1.value.toDoubleOrNull()
        val b1 = _sysB1.value.toDoubleOrNull()
        val c1 = _sysC1.value.toDoubleOrNull()
        val a2 = _sysA2.value.toDoubleOrNull()
        val b2 = _sysB2.value.toDoubleOrNull()
        val c2 = _sysC2.value.toDoubleOrNull()

        if (a1 == null || b1 == null || c1 == null || a2 == null || b2 == null || c2 == null) {
            _sysResult.value = "Please fill in all coefficients."
            return
        }

        val d = a1 * b2 - b1 * a2
        val dx = c1 * b2 - b1 * c2
        val dy = a1 * c2 - c1 * a2

        val steps = StringBuilder()
        steps.append("System of equations:\n")
        steps.append("1) ${formatDouble(a1)}x + ${formatDouble(b1)}y = ${formatDouble(c1)}\n")
        steps.append("2) ${formatDouble(a2)}x + ${formatDouble(b2)}y = ${formatDouble(c2)}\n\n")

        steps.append("Step 1: Compute determinants (Cramer's Rule)\n")
        steps.append("D = a₁b₂ - b₁a₂ = ($a1)($b2) - ($b1)($a2) = ${formatDouble(d)}\n")
        steps.append("Dx = c₁b₂ - b₁c₂ = ($c1)($b2) - ($b1)($c2) = ${formatDouble(dx)}\n")
        steps.append("Dy = a₁c₂ - c₁a₂ = ($a1)($c2) - ($c1)($a2) = ${formatDouble(dy)}\n\n")

        if (d != 0.0) {
            val x = dx / d
            val y = dy / d
            steps.append("Step 2: Solve x = Dx/D, y = Dy/D\n")
            steps.append("x = ${formatDouble(dx)} / ${formatDouble(d)} = ${formatDouble(x)}\n")
            steps.append("y = ${formatDouble(dy)} / ${formatDouble(d)} = ${formatDouble(y)}\n\n")
            steps.append("Solution:\nx = ${formatDouble(x)}\ny = ${formatDouble(y)}")
        } else {
            if (dx == 0.0 && dy == 0.0) {
                steps.append("Since D = Dx = Dy = 0, the system has infinitely many solutions (dependent system).")
            } else {
                steps.append("Since D = 0 but Dx or Dy != 0, the system has no solution (inconsistent system).")
            }
        }

        _sysResult.value = steps.toString()
    }

    private fun formatDouble(d: Double): String {
        val intVal = d.toLong()
        return if (d == intVal.toDouble()) intVal.toString() else String.format(Locale.US, "%.4f", d).replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
    }


    // --- 3. FORMULAS SHEET LOGIC ---

    fun setFormulaType(type: Int) {
        _formulaType.value = type
    }

    fun updateOhmInputs(v: String, i: String, r: String) {
        _ohmVoltage.value = v
        _ohmCurrent.value = i
        _ohmResistance.value = r
    }

    fun solveOhm() {
        val v = _ohmVoltage.value.toDoubleOrNull()
        val i = _ohmCurrent.value.toDoubleOrNull()
        val r = _ohmResistance.value.toDoubleOrNull()

        if (v != null && i != null) {
            val res = v / i
            _ohmResistance.value = formatDouble(res)
            _ohmResult.value = "Solved Resistance R = V / I = $v / $i = ${formatDouble(res)} Ω"
        } else if (v != null && r != null) {
            if (r == 0.0) {
                _ohmResult.value = "Resistance cannot be 0."
                return
            }
            val cur = v / r
            _ohmCurrent.value = formatDouble(cur)
            _ohmResult.value = "Solved Current I = V / R = $v / $r = ${formatDouble(cur)} A"
        } else if (i != null && r != null) {
            val vol = i * r
            _ohmVoltage.value = formatDouble(vol)
            _ohmResult.value = "Solved Voltage V = I * R = $i * $r = ${formatDouble(vol)} V"
        } else {
            _ohmResult.value = "Please enter exactly TWO fields to compute the third."
        }
    }

    fun clearOhm() {
        _ohmVoltage.value = ""
        _ohmCurrent.value = ""
        _ohmResistance.value = ""
        _ohmResult.value = ""
    }

    fun updateCircleRadius(radius: String) {
        _circleRadius.value = radius
        val r = radius.toDoubleOrNull()
        if (r != null && r >= 0) {
            val area = PI * r * r
            val perimeter = 2 * PI * r
            _circleArea.value = formatDouble(area)
            _circlePerimeter.value = formatDouble(perimeter)
        } else {
            _circleArea.value = ""
            _circlePerimeter.value = ""
        }
    }

    fun updatePythagInputs(a: String, b: String, c: String) {
        _pythagA.value = a
        _pythagB.value = b
        _pythagC.value = c
    }

    fun solvePythag() {
        val a = _pythagA.value.toDoubleOrNull()
        val b = _pythagB.value.toDoubleOrNull()
        val c = _pythagC.value.toDoubleOrNull()

        if (a != null && b != null) {
            val solvedC = sqrt(a*a + b*b)
            _pythagC.value = formatDouble(solvedC)
            _pythagResult.value = "Solved Hypotenuse c = √(a² + b²) = √($a² + $b²) = ${formatDouble(solvedC)}"
        } else if (a != null && c != null) {
            if (c <= a) {
                _pythagResult.value = "Hypotenuse c must be larger than side a."
                return
            }
            val solvedB = sqrt(c*c - a*a)
            _pythagB.value = formatDouble(solvedB)
            _pythagResult.value = "Solved Side b = √(c² - a²) = √($c² - $a²) = ${formatDouble(solvedB)}"
        } else if (b != null && c != null) {
            if (c <= b) {
                _pythagResult.value = "Hypotenuse c must be larger than side b."
                return
            }
            val solvedA = sqrt(c*c - b*b)
            _pythagA.value = formatDouble(solvedA)
            _pythagResult.value = "Solved Side a = √(c² - b²) = √($c² - $b²) = ${formatDouble(solvedA)}"
        } else {
            _pythagResult.value = "Please enter exactly TWO sides to compute the third."
        }
    }

    fun clearPythag() {
        _pythagA.value = ""
        _pythagB.value = ""
        _pythagC.value = ""
        _pythagResult.value = ""
    }

    fun updateBmiInputs(weight: String, height: String) {
        _bmiWeight.value = weight
        _bmiHeight.value = height
        calculateBmi()
    }

    private fun calculateBmi() {
        val w = _bmiWeight.value.toDoubleOrNull()
        val hCm = _bmiHeight.value.toDoubleOrNull()

        if (w != null && hCm != null && w > 0 && hCm > 0) {
            val hM = hCm / 100.0
            val bmi = w / (hM * hM)
            _bmiResultVal.value = String.format(Locale.US, "%.1f", bmi)
            _bmiResultCategory.value = when {
                bmi < 18.5 -> "Underweight"
                bmi < 25.0 -> "Normal weight"
                bmi < 30.0 -> "Overweight"
                else -> "Obese"
            }
        } else {
            _bmiResultVal.value = ""
            _bmiResultCategory.value = ""
        }
    }

    // 4. AI SOLVER STATE
    private val _aiQuery = MutableStateFlow("")
    val aiQuery: StateFlow<String> = _aiQuery.asStateFlow()

    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _apiKeyValidationResult = MutableStateFlow<Boolean?>(null)
    val apiKeyValidationResult: StateFlow<Boolean?> = _apiKeyValidationResult.asStateFlow()

    private val _isCheckingApiKey = MutableStateFlow(false)
    val isCheckingApiKey: StateFlow<Boolean> = _isCheckingApiKey.asStateFlow()

    fun updateAiQuery(query: String) {
        _aiQuery.value = query
    }

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key
        GeminiService.customApiKey = key
    }

    fun validateAndSetApiKey(key: String) {
        if (key.isBlank()) {
            setCustomApiKey("")
            _apiKeyValidationResult.value = true
            return
        }
        _isCheckingApiKey.value = true
        _apiKeyValidationResult.value = null
        viewModelScope.launch {
            val isValid = GeminiService.validateApiKey(key)
            _apiKeyValidationResult.value = isValid
            if (isValid) {
                setCustomApiKey(key)
            }
            _isCheckingApiKey.value = false
        }
    }

    fun clearApiKeyValidation() {
        _apiKeyValidationResult.value = null
    }

    fun clearAiSolver() {
        _aiQuery.value = ""
        _aiResponse.value = ""
        _isAiLoading.value = false
    }

    fun solveWithAi() {
        val query = _aiQuery.value.trim()
        if (query.isEmpty()) {
            _aiResponse.value = "Silakan masukkan soal cerita/pertanyaan matematika terlebih dahulu."
            return
        }

        _isAiLoading.value = true
        _aiResponse.value = ""

        viewModelScope.launch {
            val solution = GeminiService.solveWordProblem(query)
            _aiResponse.value = solution
            _isAiLoading.value = false
        }
    }
}
