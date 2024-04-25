package org.simplex

import kotlin.math.exp

class Simplex(val objFunction: String, val constraints: List<String>) {
    var c: MutableList<Int> = mutableListOf() // List of parameters for objective function
    var x: MutableList<String> = mutableListOf() // List of variables total
    var b: MutableList<Int> = mutableListOf() // List of outputs of each constraint
    var A: MutableList<MutableList<Int>> = mutableListOf() // Matrix of parameters of each constraint

    // Initialize Simplex by converting objective function and constraints to standard form
    init {
        convertObjective()
        convertConstraints()
    }

    /**
     * Convert the inputted constraints to the standard form for Simplex
     *
     * It is assumed that all variables are meant to be positive and so this constraint is not included in the list of
     * constraints added. Each constraint is converted to a list of coefficients and variables as well as their value.
     * Slack variables are added for each constraint.
     */
    private fun convertConstraints() {
        var totalConstraints = 0 // Used to create slack variables

        // Convert each constraint into lists of variables form
        for (constraint in constraints) {
            totalConstraints += 1
            var thisParam: MutableList<Int> = mutableListOf()

            // If >= for all but last, reverse the constraint because all need to be upper bounds
            val reverse: Int = if (">=" in constraint) -1 else 1

            // Convert left side of constraint
            val thisConstraint = separateEquation(constraint.substring(0, constraint.indexOf("=") - 2))
            thisConstraint.first.replaceAll { it * reverse }

            // For each variable check if it is in the constraint or not and add corresponding parameter to thisParam
            for (i in 0..<x.size) {
                if (x[i] in thisConstraint.second) {
                    thisParam.add(thisConstraint.first[thisConstraint.second.indexOf(x[i])])
                } else {
                    thisParam.add(0)
                }
            }

            thisParam.add(1) // Add parameter 1 for slack variable
            x.add("e$totalConstraints") // Add slack variable to variables
            c.add(0) // Add initial 0 to objective function parameters for each slack variable / constraint being added
            // Add 0 to each previous constraint for the new slack variable
            for (prevConstIdx in 0..<A.size) {
                A[prevConstIdx].add(0)
            }
            A.add(thisParam) // Add thisParam list to A matrix
            // Add result to b vector (reversed if necessary)
            b.add(constraint.substring(constraint.indexOf("=") + 2).toInt() * reverse)
        }
    }

    /**
     * Convert the objective function to the standard form necessary for Simplex
     *
     * Input is of form "<function> <var1> <+/-> <var2> ... <+/-> <varx>". The function can be either "Maximize" or
     * "Minimize". An example is "Minimize 3x1 + 2x2 - 4x3".
     *
     * Since Simplex requires a maximization problem, if the goal is to minimize, each variable is multiplied by -1 to
     * convert to the equivalent maximization problem.
     */
    private fun convertObjective() {
        var reverse: Int = if (objFunction.substring(0, 3) == "Max") 1 else -1 // If minimizing, reverse the objective
        val objStandard = separateEquation(objFunction.substring(objFunction.indexOf(" ") + 1))
        objStandard.first.replaceAll { it * reverse }
        c = objStandard.first
        x = objStandard.second
    }

    /**
     * Converts inputted expression into a list of parameters and variables
     *
     * Each variable is separated from its coefficient and, in the case of a subtraction, converted to the negative form
     *
     * @param equation: A string of the format "p1v1 +/- p2v2 ..." to be separated. A sample input is "- 3x1 + 2x2"
     *
     * @return A Pair of two Mutable Lists, the first containing Ints representing the parameters of each of the terms
     * and the second containing Strings representing the variables of each of the terms
     */
    private fun separateEquation(equation: String): Pair<MutableList<Int>, MutableList<String>> {
        val initList = equation.split(" ")
        var multiply: Int = 1 // If come across a '-', then change this to -1
        val params: MutableList<Int> = mutableListOf()
        val vars: MutableList<String> = mutableListOf()

        // Loop through all variables in equation
        for (value in initList) {
            // If + or -, add corresponding value to multiply next number by
            if (value == "-") {
                multiply *= -1
                continue
            }

            // Else separate the number from the variable name and add to corresponding list
            if (value != "+") {
                // If no beginning, assume to be 1
                if (!value[0].isDigit()) {
                    params.add(1 * multiply)
                    vars.add(value)
                    continue
                }
                var idxIntEnd: Int = 0
                // Find the index where the parameter of the variable ends (cannot just convert all digits since
                // variable name could be x1 etc
                for (char in value) {
                    if (!char.isDigit()) {
                        break
                    }
                    idxIntEnd += 1
                }
                params.add(value.substring(0, idxIntEnd).toInt() * multiply)
                vars.add(value.substring(idxIntEnd))
            }
            multiply = 1 // Reset multiply
        }
        return Pair(params, vars)
    }
}

fun main() {
    val s = Simplex("Minimize 4x1 + 6x2", listOf("- x1 + x2 >= 11", "x1 + x2 <= 27", "2x1 + 5x2 <= 90"))
    println(s.c)
    println(s.x)
    println(s.A)
    println(s.b)
}