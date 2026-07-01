package com.example

import kotlin.math.*

class CalculatorParser(private val isDegreeMode: Boolean = true) {

    private var pos = -1
    private var ch = 0
    private var str = ""

    private fun nextChar() {
        ch = if (++pos < str.length) str[pos].code else -1
    }

    private fun eat(charToEat: Int): Boolean {
        while (ch == ' '.code) nextChar()
        if (ch == charToEat) {
            nextChar()
            return true
        }
        return false
    }

    fun parse(expression: String): Double {
        // Pre-process expression:
        str = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "pi")
        
        pos = -1
        nextChar()
        val x = parseExpression()
        if (pos < str.length) throw IllegalArgumentException("Unexpected character: " + ch.toChar() + " at index " + pos)
        if (x.isNaN() || x.isInfinite()) {
            throw ArithmeticException("Result is undefined or infinite")
        }
        return x
    }

    private fun parseExpression(): Double {
        var x = parseTerm()
        while (true) {
            if (eat('+'.code)) x += parseTerm() // addition
            else if (eat('-'.code)) x -= parseTerm() // subtraction
            else break
        }
        return x
    }

    private fun parseTerm(): Double {
        var x = parseFactor()
        while (true) {
            if (eat('*'.code)) x *= parseFactor() // multiplication
            else if (eat('/'.code)) {
                val denominator = parseFactor()
                if (denominator == 0.0) throw ArithmeticException("Division by zero")
                x /= denominator // division
            } else if (eat('m'.code)) {
                // check for "mod"
                if (ch == 'o'.code) {
                    nextChar()
                    if (ch == 'd'.code) {
                        nextChar()
                        val denominator = parseFactor()
                        if (denominator == 0.0) throw ArithmeticException("Modulo by zero")
                        x %= denominator
                    } else {
                        throw IllegalArgumentException("Expected 'mod'")
                    }
                } else {
                    throw IllegalArgumentException("Unexpected character 'm'")
                }
            } else {
                break
            }
        }
        return x
    }

    private fun parseFactor(): Double {
        if (eat('+'.code)) return parseFactor() // unary plus
        if (eat('-'.code)) return -parseFactor() // unary minus

        var x: Double
        val startPos = this.pos
        if (eat('('.code)) { // parentheses
            x = parseExpression()
            if (!eat(')'.code)) throw IllegalArgumentException("Mismatched parentheses")
        } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) { // numbers
            while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
            val numStr = str.substring(startPos, this.pos)
            x = numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $numStr")
        } else if (ch >= 'a'.code && ch <= 'z'.code || ch == 'p'.code || ch == 'e'.code) { // functions or constants
            while (ch >= 'a'.code && ch <= 'z'.code || ch >= '0'.code && ch <= '9'.code) nextChar()
            val id = str.substring(startPos, this.pos)
            
            if (eat('('.code)) {
                // It's a function call
                val args = mutableListOf<Double>()
                if (ch != ')'.code) {
                    args.add(parseExpression())
                    while (eat(','.code)) {
                        args.add(parseExpression())
                    }
                }
                if (!eat(')'.code)) throw IllegalArgumentException("Mismatched parentheses after arguments of $id")
                x = evaluateFunction(id, args)
            } else {
                // It's a constant
                x = when (id) {
                    "pi" -> PI
                    "e" -> E
                    else -> throw IllegalArgumentException("Unknown constant: $id")
                }
            }
        } else {
            throw IllegalArgumentException("Unexpected character: " + ch.toChar() + " at index " + pos)
        }

        // Handle postfix operators like ^, !, %
        while (true) {
            if (eat('^'.code)) {
                val exponent = parseFactor()
                x = x.pow(exponent)
            } else if (eat('!'.code)) {
                x = factorial(x)
            } else if (eat('%'.code)) {
                x /= 100.0
            } else {
                break
            }
        }

        return x
    }

    private fun evaluateFunction(func: String, args: List<Double>): Double {
        if (args.isEmpty()) {
            throw IllegalArgumentException("Function $func requires at least one argument")
        }
        val first = args[0]
        return when (func) {
            "sin" -> if (isDegreeMode) sin(Math.toRadians(first)) else sin(first)
            "cos" -> if (isDegreeMode) cos(Math.toRadians(first)) else cos(first)
            "tan" -> if (isDegreeMode) tan(Math.toRadians(first)) else tan(first)
            "asin" -> {
                val res = asin(first)
                if (isDegreeMode) Math.toDegrees(res) else res
            }
            "acos" -> {
                val res = acos(first)
                if (isDegreeMode) Math.toDegrees(res) else res
            }
            "atan" -> {
                val res = atan(first)
                if (isDegreeMode) Math.toDegrees(res) else res
            }
            "sinh" -> sinh(first)
            "cosh" -> cosh(first)
            "tanh" -> tanh(first)
            "ln" -> {
                if (first <= 0.0) throw IllegalArgumentException("Logarithm of non-positive number")
                ln(first)
            }
            "log" -> {
                if (first <= 0.0) throw IllegalArgumentException("Logarithm of non-positive number")
                log10(first)
            }
            "log2" -> {
                if (first <= 0.0) throw IllegalArgumentException("Logarithm of non-positive number")
                log2(first)
            }
            "sqrt" -> {
                if (first < 0.0) throw IllegalArgumentException("Square root of negative number")
                sqrt(first)
            }
            "cbrt" -> Math.cbrt(first)
            "abs" -> abs(first)
            "nCr" -> {
                if (args.size != 2) throw IllegalArgumentException("nCr requires 2 arguments (n, r)")
                val n = args[0]
                val r = args[1]
                nCr(n, r)
            }
            "nPr" -> {
                if (args.size != 2) throw IllegalArgumentException("nPr requires 2 arguments (n, r)")
                val n = args[0]
                val r = args[1]
                nPr(n, r)
            }
            "gcd" -> {
                if (args.size != 2) throw IllegalArgumentException("gcd requires 2 arguments")
                gcd(args[0], args[1])
            }
            "lcm" -> {
                if (args.size != 2) throw IllegalArgumentException("lcm requires 2 arguments")
                lcm(args[0], args[1])
            }
            else -> throw IllegalArgumentException("Unknown function: $func")
        }
    }

    private fun factorial(n: Double): Double {
        if (n < 0.0) throw IllegalArgumentException("Factorial of negative number")
        val intN = n.roundToInt()
        if (abs(n - intN) > 1e-9) {
            throw IllegalArgumentException("Factorial is only supported for integers")
        }
        if (intN > 170) throw IllegalArgumentException("Factorial overflow (max 170)")
        var result = 1.0
        for (i in 2..intN) {
            result *= i
        }
        return result
    }

    private fun nCr(n: Double, r: Double): Double {
        val nInt = n.roundToInt()
        val rInt = r.roundToInt()
        if (nInt < 0 || rInt < 0 || rInt > nInt) return 0.0
        return factorial(nInt.toDouble()) / (factorial(rInt.toDouble()) * factorial((nInt - rInt).toDouble()))
    }

    private fun nPr(n: Double, r: Double): Double {
        val nInt = n.roundToInt()
        val rInt = r.roundToInt()
        if (nInt < 0 || rInt < 0 || rInt > nInt) return 0.0
        return factorial(nInt.toDouble()) / factorial((nInt - rInt).toDouble())
    }

    private fun gcd(a: Double, b: Double): Double {
        var x = abs(a.roundToLong())
        var y = abs(b.roundToLong())
        while (y != 0L) {
            val temp = y
            y = x % y
            x = temp
        }
        return x.toDouble()
    }

    private fun lcm(a: Double, b: Double): Double {
        val g = gcd(a, b)
        if (g == 0.0) return 0.0
        return abs(a.roundToLong() * b.roundToLong()).toDouble() / g
    }
}
