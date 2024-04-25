package org.simplex

class Simplex(val objFunction: String, constraints: List<String>) {
    var obj: MutableList<Int> = mutableListOf() // List of parameters for objective function
    var vars: MutableList<String> = mutableListOf()
    var constraint: MutableList<String> = mutableListOf()
    init {
        convertObjective()
        convertConstraints()
    }

    private fun convertConstraints() {
        val initList = constraints.split(" ")
    }

    /**
     * Convert the inputted objective function to the form necessary for Simplex
     *
     * Input is of form "<function> <var1> <+/-> <var2> ... <+/-> <varx>". The function can be either "Maximize" or "Minimize".
     *
     * Since Simplex requires a maximization problem, if the goal is to minimize, each variable is multiplied by -1 to convert
     * to the equivalent maximization problem.
     *
     * Each variable is separated from it's coefficient and, in the case of a subtraction, converted to the negative form. These
     * are stored in the 'obj' and 'vars' lists of the object.
     */
    private fun convertObjective() {
        val initList = objFunction.split(" ")
        var multiply: Int = if (initList[0] == "Maximize") 1 else -1 // If minimizing, then reverse the objective

        // Loop through all variables in objective
        for (value in initList.subList(1, initList.size)) {
            // If + or -, add corresponding value to multiply next number by
            if (value == "-") {
                multiply *= -1
                continue
            }

            // Else separate the number from the variable name and add to corresponding list
            if (value != "+") {
                var idxIntEnd: Int = 0
                for (char in value) {
                    if (!char.isDigit()) {
                        break
                    }
                    idxIntEnd += 1
                }
                obj.add(value.substring(0, idxIntEnd).toInt() * multiply)
                vars.add(value.substring(idxIntEnd))
            }
            multiply = if (initList[0] == "Maximize") 1 else -1 // If minimizing, reverse the objective
        }
    }
}
fun main() {
    val s = Simplex("Minimize 3x - 2y")
    print(s.obj)
    print(s.vars)
}