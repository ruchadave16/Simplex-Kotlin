package org.simplex

import kotlin.math.exp

class Simplex(val objFunction: String, val constraints: List<String>) {
    var c: MutableList<Int> = mutableListOf() // List of parameters for objective function
    var x: MutableList<String> = mutableListOf() // List of variables total
    var b: MutableList<Int> = mutableListOf() // List of outputs of each constraint
    var A: MutableList<MutableList<Int>> = mutableListOf() // Matrix of parameters of each constraint
    var constraint: MutableList<String> = mutableListOf()
    init {
        convertObjective()
        convertConstraints()
    }

    /**
     * Convert the inputted constraints to the standard form for Simplex
     */
    private fun convertConstraints() {
        var totalConstraints = 0 // Used to create slack variables

        // Convert each constraint into lists of variables form
        for (constraint in constraints) {
            println(constraint)
            totalConstraints += 1

            var thisParam: MutableList<Int> = mutableListOf()
            val reverse: Int = if (">=" in constraint) -1 else 1 // If >= for all but last, reverse the constraint because all need to be upper bounds

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
            c.add(0) // Add 1 initial 0 to the objective function parameters for each slack variable / constraint being added
            // Add 0 to each previous constraint for the new slack variable
            for (prevConstIdx in 0..<A.size) {
                A[prevConstIdx].add(0)
            }
            A.add(thisParam) // Add thisParam list to A matrix
            b.add(constraint.substring(constraint.indexOf("=") + 2).toInt() * reverse) // Add result to b vector (reversed if necessary)
        }
    }

    /**
     * Convert the inputted objective function to the form necessary for Simplex
     *
     * Input is of form "<function> <var1> <+/-> <var2> ... <+/-> <varx>". The function can be either "Maximize" or "Minimize".
     *
     * Since Simplex requires a maximization problem, if the goal is to minimize, each variable is multiplied by -1 to convert
     * to the equivalent maximization problem.
     */
    private fun convertObjective() {
        var reverse: Int = if (objFunction.substring(0, 3) == "Max") 1 else -1 // If minimizing, then reverse the objective
        val objStandard = separateEquation(objFunction.substring(objFunction.indexOf(" ") + 1))
        objStandard.first.replaceAll { it * reverse }
        c = objStandard.first
        x = objStandard.second
    }

    /**
     * Takes in a string representing the equation to split and 2 mutable lists to hold the parameters and variables
     *
     * Each variable is separated from it's coefficient and, in the case of a subtraction, converted to the negative form. These
     * are stored in the 'obj' and 'vars' lists of the object.
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
                // Find the index where the parameter of the variable ends (cannot just convert all digits since variable
                // name could be x1 etc
                for (char in value) {
                    if (!char.isDigit()) {
                        break
                    }
                    idxIntEnd += 1
                }
                params.add(value.substring(0, idxIntEnd).toInt() * multiply)
                vars.add(value.substring(idxIntEnd))
            }
            multiply = 1 // Reset mutliply
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