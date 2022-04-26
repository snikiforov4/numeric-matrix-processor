package processor

import kotlin.math.pow

class Matrix(private val matrix: Array<Array<Double>>) {

    companion object {
        fun empty(n: Int, m: Int = n): Matrix {
            require(n > 0)
            require(m > 0)
            return Matrix(Array(n) { Array(m) { 0.0 } })
        }
    }

    val sizesOfDimensions: IntArray
        get() {
            val n = matrix.size
            val m = matrix.firstOrNull()?.size ?: 0
            return intArrayOf(n, m)
        }

    operator fun get(n: Int, m: Int): Double = matrix[n][m]

    operator fun set(n: Int, m: Int, value: Double) {
        matrix[n][m] = value
    }

    fun swap(x1: Int, y1: Int, x2: Int, y2: Int) {
        val temp = this[x1, y1]
        this[x1, y1] = this[x2, y2]
        this[x2, y2] = temp
    }

    fun isSquare(): Boolean {
        val (x, y) = this.sizesOfDimensions
        return x == y
    }

    fun determinant(): Double {
        require(this.isSquare())
        val (n) = this.sizesOfDimensions
        return when {
            n == 1 -> this[0, 0]
            n == 2 -> (this[0, 0] * this[1, 1]) - (this[0, 1] * this[1, 0])
            n >= 3 -> {
                var res = 0.0
                repeat(n) {
                    res += this[0, it] * cofactor(0, it)
                }
                res
            }
            else -> 0.0
        }
    }

    private fun cofactor(x: Int, y: Int): Double {
        return (-1.0).pow(x + y) * subMatrixExcluding(x, y).determinant()
    }

    private fun subMatrixExcluding(xExclude: Int, yExclude: Int): Matrix {
        val (n, m) = this.sizesOfDimensions
        require(xExclude in 0 until n) { "Index out of range: $xExclude" }
        require(yExclude in 0 until m) { "Index out of range: $yExclude" }
        val result = empty(n - 1, m - 1)
        var i = 0
        for (x in 0 until n) {
            if (x == xExclude) {
                continue
            }
            var j = 0
            for (y in 0 until m) {
                if (y == yExclude) {
                    continue
                }
                result[i, j] = this[x, y]
                j++
            }
            i++
        }
        return result
    }

    fun toCofactorMatrix(): Matrix {
        require(isSquare())
        val (n) = sizesOfDimensions
        val result = empty(n)
        for (x in 0 until n) {
            for (y in 0 until n) {
                result[x, y] = cofactor(x, y)
            }
        }
        return result
    }
}

interface TransposeStrategy {
    companion object {
        fun byNumber(number: Int): TransposeStrategy = when (number) {
            1 -> MainDiagonalTransposeStrategy
            2 -> SideDiagonalTransposeStrategy
            3 -> VerticalLineTransposeStrategy
            4 -> HorizontalLineTransposeStrategy
            else -> NullTransposeStrategy
        }
    }

    fun transpose(matrix: Matrix)
}

object MainDiagonalTransposeStrategy : TransposeStrategy {
    override fun transpose(matrix: Matrix) {
        val (n, m) = matrix.sizesOfDimensions
        check(n == m)
        for (x in 0 until n) {
            for (y in 0 until x) {
                matrix.swap(x, y, y, x)
            }
        }
    }
}

object SideDiagonalTransposeStrategy : TransposeStrategy {
    override fun transpose(matrix: Matrix) {
        val (n, m) = matrix.sizesOfDimensions
        check(n == m)
        for (x in 0 until n) {
            for (y in (0 until m - x - 1)) {
                matrix.swap(x, y, n - 1 - y, m - 1 - x)
            }
        }
    }
}

object VerticalLineTransposeStrategy : TransposeStrategy {
    override fun transpose(matrix: Matrix) {
        val (n, m) = matrix.sizesOfDimensions
        check(n == m)
        for (x in 0 until n) {
            for (y in 0 until m / 2) {
                matrix.swap(x, y, x, m - 1 - y)
            }
        }
    }
}

object HorizontalLineTransposeStrategy : TransposeStrategy {
    override fun transpose(matrix: Matrix) {
        val (n, m) = matrix.sizesOfDimensions
        check(n == m)
        for (x in 0 until n / 2) {
            for (y in 0 until m) {
                matrix.swap(x, y, n - 1 - x, y)
            }
        }
    }
}

object NullTransposeStrategy : TransposeStrategy {
    override fun transpose(matrix: Matrix) {
    }
}