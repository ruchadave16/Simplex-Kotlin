package org.simplex

/**
 * Class implementing Simplex Algorithm in Kotlin.
 *
 * @param objFunction A string representing the objective function to solve (maximize or minimize). The format of the
 *                    input is as follows: "<function> <var1> <+/-> <var2> ... <+/-> <varx>". The function can be
 *                    either "Maximize" or "Minimize". An example is "Minimize 3x1 + 2x2 - 4x3".
 * @param constraints A list of strings representing the constraints this LP contains. This runs under the assumption
 *                    that all variables are greater than 0 and therefore this is not a part of the list of
 *                    constraints. The format is as follows: "p1v1 +/- p2v2 ..." to be separated. A sample input is
 *                    "- 3x1 + 2x2".
 *
 * Shape of the overall array that this is representing (size may change):
 *
 *                       c1  c2  c3  c4  c5  c6
 *                 _________________________________
 *                       x1  x2  x3  x4  x5  x6  RHS
 *                 C1 | a00 a01 a02 a03 a04 a05   b1
 *                 C2 | a10 a11 a12 a13 a14 a15   b2
 *                 C3 | a20 a21 a22 a23 a24 a25   b3
 *                 __________________________________
 *             negObj | no1 no2 no3 no4 no5 no6  no7
 *
 * -- The top row (c1 - c6) represents the values in the c matrix, representing the parameters of the objective
 *    function. The first __ values represent the coefficients for the variables from the objective function and the
 *    remaining represent the coefficients for the slack variables from each constraint.
 *
 * -- The header row of the matrix (x1 - x6) represents the variables for the LP. The first __ values represent the
 *    variables from the objective function and the remaining represent the variables for the slack variables each
 *    constraint adds
 *
 * -- The right hand column (b1 - b3) represents the values in the b matrix, representing the right hand side of each
 *    of the constraints once they are converted to standard form. b1 corresponds to the first constraint, b2 to the
 *    second etc.
 *
 * -- The matrix in the middle of the table (a00 - a25) represents values from the A matrix, representing the
 *    coefficients from each of the constraints. The first __ represents coefficients for each variable in the objective
 *    function (if it isn't present in the constraint, then the coefficient is set to 0) while the remaining represent
 *    coefficients for the slack variables. Initially, each row only has 1 slack variable with a non-zero value.
 *
 * -- The last row (no1 - no7) represents values from the negObj matrix.
 */
class Simplex(val objFunction: String, val constraints: List<String>) {
    var c: MutableList<Int> = mutableListOf() // List of parameters for objective function
    var x: MutableList<String> = mutableListOf() // List of variables total
    var b: MutableList<Int> = mutableListOf() // List of outputs of each constraint
    var A: MutableList<MutableList<Int>> = mutableListOf() // Matrix of parameters of each constraint
    var negObj: MutableList<Int> = mutableListOf() // List representing negative values of the objective

    var soln: MutableList<Int> = mutableListOf() // The current solution state, in the order of x's variables

    // Initialize Simplex by converting objective function and constraints to standard form
    init {
        convertObjective()
        convertConstraints()
        negObj.add(0)
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
            negObj.add(0)
            // Add 0 to each previous constraint for the new slack variable
            for (prevConstIdx in 0..<A.size) {
                A[prevConstIdx].add(0)
            }
            A.add(thisParam) // Add thisParam list to A matrix
            // Add result to b vector (reversed if necessary)
            b.add(constraint.substring(constraint.indexOf("=") + 2).toInt() * reverse)
            soln.add(b.last()) // Add b values associated with correct slack variable to initial solution
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

        // Initialize solution so that all x variables are 0 and negObj is negative values of current c
        for (idx in 0..<x.size) {
            soln.add(0)
            negObj.add(c[idx] * -1)
        }
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
    val s = Simplex("Maximize 4x1 + 6x2", listOf("- x1 + x2 >= 11", "x1 + x2 <= 27", "2x1 + 5x2 <= 90"))
    println(s.c)
    println(s.x)
    println(s.A)
    println(s.b)
    println(s.soln)
    println(s.negObj)
}