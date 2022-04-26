package processor

import java.text.DecimalFormat


private val regexp = "\\s+".toRegex()
var decimalFormat = DecimalFormat("#0.##")

fun main() {
    while (true) {
        printMenu()
        print("Your choice: ")
        when (readln().toInt()) {
            0 -> break
            1 -> {
                val m1 = readMatrix("first")
                val m2 = readMatrix("second")
                if (isAdditionAllowed(m1, m2)) {
                    printMatrix(addMatrices(m1, m2))
                } else {
                    println("The operation cannot be performed.")
                }
            }
            2 -> {
                val m1 = readMatrix()
                print("Enter constant: ")
                val constant = readln().toDouble()
                printMatrix(multiplyMatrixByConstant(m1, constant))
            }
            3 -> {
                val m1 = readMatrix("first")
                val m2 = readMatrix("second")
                if (isMultiplicationAllowed(m1, m2)) {
                    printMatrix(multiplyMatrices(m1, m2))
                } else {
                    println("The operation cannot be performed.")
                }
            }
            4 -> {
                printTransposeMatrixMenu()
                print("Your choice: ")
                val strategy = TransposeStrategy.byNumber(readln().toInt())
                val m = readMatrix()
                strategy.transpose(m)
                printMatrix(m)
            }
            5 -> {
                val m = readMatrix()
                if (m.isSquare()) {
                    println("The result is:")
                    println(m.determinant())
                } else {
                    println("The operation cannot be performed.")
                }
            }
            6 -> {
                val m = readMatrix()
                if (m.isSquare()) {
                    val inverse = findMatrixInverse(m)
                    if (inverse == null) {
                        println("This matrix doesn't have an inverse.")
                    } else {
                        printMatrix(inverse)
                    }
                } else {
                    println("The operation cannot be performed.")
                }
            }
            else -> println("Wrong choice!")
        }
    }
}

private fun printMenu() {
    println("1. Add matrices")
    println("2. Multiply matrix by a constant")
    println("3. Multiply matrices")
    println("4. Transpose matrix")
    println("5. Calculate a determinant")
    println("6. Inverse matrix")
    println("0. Exit")
}

private fun readMatrix(matrixName: String = ""): Matrix {
    val matrixNamePlaceholder = if (matrixName.isNotBlank()) " " else "" + matrixName.trim()
    print("Enter size of$matrixNamePlaceholder matrix: ")
    val (n, m) = readln().split(regexp).map { it.toInt() }
    val result = Matrix.empty(n, m)
    println("Enter$matrixNamePlaceholder matrix:")
    repeat(n) { x ->
        val row = readln().split(regexp).map { it.toDouble() }
        check(row.size == m)
        row.forEachIndexed { y, e -> result[x, y] = e }
    }
    return result
}

private fun isAdditionAllowed(m1: Matrix, m2: Matrix) =
    m1.sizesOfDimensions.contentEquals(m2.sizesOfDimensions)

private fun addMatrices(m1: Matrix, m2: Matrix): Matrix {
    val (n, m) = m1.sizesOfDimensions
    val result = Matrix.empty(n, m)
    repeat(n) { x ->
        repeat(m) { y ->
            result[x, y] = m1[x, y] + m2[x, y]
        }
    }
    return result
}

private fun multiplyMatrixByConstant(matrix: Matrix, c: Double): Matrix {
    val (n, m) = matrix.sizesOfDimensions
    val result = Matrix.empty(n, m)
    repeat(n) { x ->
        repeat(m) { y ->
            result[x, y] = matrix[x, y] * c
        }
    }
    return result
}

private fun isMultiplicationAllowed(m1: Matrix, m2: Matrix): Boolean {
    val (_, m1d2) = m1.sizesOfDimensions
    val (m2d1, _) = m2.sizesOfDimensions
    return m1d2 == m2d1
}

private fun multiplyMatrices(m1: Matrix, m2: Matrix): Matrix {
    val (n, m) = m1.sizesOfDimensions
    val (_, k) = m2.sizesOfDimensions
    val result = Matrix.empty(n, k)
    repeat(n) { x ->
        repeat(k) { y ->
            val dotProduct = (0 until m).sumOf { idx -> m1[x, idx] * m2[idx, y] }
            result[x, y] = dotProduct
        }
    }
    return result
}

private fun printMatrix(matrix: Matrix) {
    val (n, m) = matrix.sizesOfDimensions
    println("The result is:")
    repeat(n) { x ->
        repeat(m) { y ->
            print("${decimalFormat.format(matrix[x, y])} ")
        }
        println()
    }
}

private fun printTransposeMatrixMenu() {
    println("1. Main diagonal")
    println("2. Side diagonal")
    println("3. Vertical line")
    println("4. Horizontal line")
}

fun findMatrixInverse(matrix: Matrix): Matrix? {
    val determinant = matrix.determinant()
    if (determinant == 0.0) {
        return null
    }
    val cofactorMatrix = matrix.toCofactorMatrix()
    MainDiagonalTransposeStrategy.transpose(cofactorMatrix)
    return multiplyMatrixByConstant(cofactorMatrix, 1.0 / determinant)
}
