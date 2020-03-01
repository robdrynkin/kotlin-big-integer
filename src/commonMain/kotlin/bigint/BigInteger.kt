package bigint

import kotlin.math.abs
import kotlin.math.sign
import kotlin.Int
import kotlin.math.max


typealias TMagnitude = UIntArray
typealias TBase = ULong

class TBigInteger constructor(
    sign: Int = 0,
    magnitude: TMagnitude = TMagnitude(0)
) : Number(), Comparable<TBigInteger> {

    val magnitude = stripLeadingZeros(magnitude)
    val sign = if (this.magnitude.isNotEmpty()) sign else 0
    val sizeByte: Int = magnitude.size * BASE_SIZE / 4

    companion object {
        //        TODO: Rename
        val BASE = 0xffffffffUL
        const val BASE_SIZE: Int = 32
        private val hexMapping: HashMap<UInt, String> = hashMapOf(0U to "0", 1U to "1", 2U to "2", 3U to "3", 4U to "4", 5U to "5", 6U to "6", 7U to "7", 8U to "8", 9U to "9", 10U to "a", 11U to "b", 12U to "c", 13U to "d", 14U to "e", 15U to "f")

        val ZERO: TBigInteger = TBigInteger()
        val ONE: TBigInteger = TBigInteger(1)

        private fun stripLeadingZeros(mag: TMagnitude): TMagnitude {
            // TODO: optimize performance
            if (mag.isEmpty()) {
                return mag
            }
            var resSize: Int = mag.size - 1
            while (mag[resSize] == 0U) {
                if (resSize == 0)
                    break
                resSize -= 1
            }
            return mag.sliceArray(IntRange(0, resSize))
        }

        private fun compareMagnitudes(mag1: TMagnitude, mag2: TMagnitude): Int {
            when {
                mag1.size > mag2.size -> return 1
                mag1.size < mag2.size -> return -1
                else -> {
                    for (i in mag1.size - 1 downTo 0) {
                        if (mag1[i] > mag2[i]) {
                            return 1
                        } else if (mag1[i] < mag2[i]) {
                            return -1
                        }
                    }
                    return 0
                }
            }
        }

        private fun addMagnitudes(mag1: TMagnitude, mag2: TMagnitude): TMagnitude {
            val resultLength: Int = max(mag1.size, mag2.size) + 1
            val result = TMagnitude(resultLength)
            var carry: TBase = 0UL

            for (i in 0 until resultLength - 1) {

                val res = when {
                    i >= mag1.size -> mag2[i].toULong() + carry
                    i >= mag2.size -> mag1[i].toULong() + carry
                    else -> mag1[i].toULong() + mag2[i].toULong() + carry
                }
                result[i] = (res and BASE).toUInt()
                carry = (res shr BASE_SIZE)
            }
            result[resultLength - 1] = carry.toUInt()
            return result
        }

        private fun subtractMagnitudes(mag1: TMagnitude, mag2: TMagnitude): TMagnitude {
            val resultLength: Int = mag1.size
            val result = TMagnitude(resultLength)
            var carry = 0L

            for (i in 0 until resultLength) {
                var res: Long =
                    if (i < mag2.size) mag1[i].toLong() - mag2[i].toLong() - carry
                    else mag1[i].toLong() - carry

                carry = if (res < 0) 1 else 0
                res += carry * (BASE + 1UL).toLong()

                result[i] = res.toUInt()
            }

            return result
        }

        private fun multiplyMagnitudeByUInt(mag: TMagnitude, x: UInt): TMagnitude {
            val resultLength: Int = mag.size + 1
            val result = TMagnitude(resultLength)
            var carry: ULong = 0UL

            for (i in mag.indices) {
                val cur: ULong = carry + mag[i].toULong() * x.toULong()
                result[i] = (cur and BASE.toULong()).toUInt()
                carry = cur shr BASE_SIZE
            }
            result[resultLength - 1] = (carry and BASE).toUInt()

            return result
        }

        private fun multiplyMagnitudes(mag1: TMagnitude, mag2: TMagnitude): TMagnitude {
            val resultLength: Int = mag1.size + mag2.size
            val result = TMagnitude(resultLength)

            for (i in mag1.indices) {
                var carry: ULong = 0UL
                for (j in mag2.indices) {
                    val cur: ULong = result[i + j].toULong() + mag1[i].toULong() * mag2[j].toULong() + carry
                    result[i + j] = (cur and BASE.toULong()).toUInt()
                    carry = cur shr BASE_SIZE
                }
                result[i + mag2.size] = (carry and BASE).toUInt()
            }

            return result
        }

        private fun divideMagnitudeByUInt(mag: TMagnitude, x: UInt): TMagnitude {
            val resultLength: Int = mag.size
            val result = TMagnitude(resultLength)
            var carry: ULong = 0UL

            for (i in mag.size - 1 downTo 0) {
                val cur: ULong = mag[i].toULong() + (carry shl BASE_SIZE)
                result[i] = (cur / x).toUInt()
                carry = cur % x
            }
            return result
        }

        private fun divideMagnitudes(mag1_: TMagnitude, mag2: TMagnitude): TMagnitude {
            val mag1 = ULongArray(mag1_.size) { mag1_[it].toULong() }

            val resultLength: Int = mag1.size - mag2.size + 1
            val result = LongArray(resultLength)

            for (i in mag1.size - 1 downTo mag2.size - 1) {
                val div: ULong = mag1[i] / mag2[mag2.size - 1]
                result[i - mag2.size + 1] = div.toLong()
                for (j in mag2.indices) {
                    mag1[i - j] -= mag2[mag2.size - 1 - j] * div
                }
                if (i > 0) {
                    mag1[i - 1] += (mag1[i] shl BASE_SIZE)
                }
            }

            val normalizedResult = TMagnitude(resultLength)
            var carry = 0L

            for (i in result.indices) {
                result[i] += carry
                if (result[i] < 0L) {
                    normalizedResult[i] = (result[i] + (BASE + 1UL).toLong()).toUInt()
                    carry = -1
                } else {
                    normalizedResult[i] = result[i].toUInt()
                    carry = 0
                }
            }

            return normalizedResult
        }
    }

    constructor(x: Int) : this(x.sign, uintArrayOf(abs(x).toUInt()))
    constructor(x: Long) : this(x.sign, uintArrayOf((abs(x).toULong() and BASE).toUInt(), ((abs(x).toULong() shr BASE_SIZE) and BASE).toUInt()))

    override fun compareTo(other: TBigInteger): Int {
        return when {
            (this.sign == 0) and (other.sign == 0) -> 0
            this.sign < other.sign -> -1
            this.sign > other.sign -> 1
            else -> this.sign * compareMagnitudes(this.magnitude, other.magnitude)
        }
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is TBigInteger -> this.compareTo(other) == 0
            is Int -> this.compareTo(TBigInteger(other)) == 0
            is Long -> this.compareTo(TBigInteger(other)) == 0
            is UInt -> this.compareTo(TBigInteger(other)) == 0
            is ULong -> this.compareTo(TBigInteger(other)) == 0
            // TODO: implement for other types
            else -> false
        }
    }

    override fun hashCode(): Int {
        var hash = 0
        for (x in this.magnitude) {
            hash = hash * 31 + x.toInt()
        }
        return hash * 31 + this.sign
    }

    operator fun unaryMinus(): TBigInteger {
        return if (this.sign == 0) this else TBigInteger(-this.sign, this.magnitude)
    }

    operator fun plus(other: TBigInteger): TBigInteger {
        return when {
            other.sign == 0 -> this
            this.sign == 0 -> other
            this == -other -> ZERO
            this.sign == other.sign -> TBigInteger(this.sign, addMagnitudes(this.magnitude, other.magnitude))
            else -> {
                val comp: Int = compareMagnitudes(this.magnitude, other.magnitude)

                if (comp == 1) {
                    TBigInteger(this.sign, subtractMagnitudes(this.magnitude, other.magnitude))
                } else {
                    TBigInteger(-this.sign, subtractMagnitudes(other.magnitude, this.magnitude))
                }
            }
        }
    }

    operator fun minus(other: TBigInteger): TBigInteger {
        return this + (-other)
    }

    operator fun times(other: TBigInteger): TBigInteger {
        return when {
            this.sign == 0 -> ZERO
            other.sign == 0 -> ZERO
//            TODO: Karatsuba
            else -> TBigInteger(this.sign * other.sign, multiplyMagnitudes(this.magnitude, other.magnitude))
        }
    }

    operator fun times(other: UInt): TBigInteger {
        return when {
            this.sign == 0 -> ZERO
            other == 0U -> ZERO
            else -> TBigInteger(this.sign, multiplyMagnitudeByUInt(this.magnitude, other))
        }
    }

    operator fun times(other: Int): TBigInteger {
        return if (other > 0)
            this * abs(other).toUInt()
        else
            -this * abs(other).toUInt()
    }

    operator fun div(other: UInt): TBigInteger {
        return TBigInteger(this.sign, divideMagnitudeByUInt(this.magnitude, other))
    }

    operator fun div(other: Int): TBigInteger {
        return TBigInteger(this.sign * other.sign, divideMagnitudeByUInt(this.magnitude, abs(other).toUInt()))
    }

    operator fun div(other: TBigInteger): TBigInteger {
        return when {
            this < other -> ZERO
            this == other -> ONE
            else -> TBigInteger(this.sign * other.sign, divideMagnitudes(this.magnitude, other.magnitude))
        }
    }

    operator fun rem(other: Int): Int {
        val res = this - (this / other) * other
        return if (res == ZERO) 0 else res.sign * res.magnitude[0].toInt()
    }

    operator fun rem(other: TBigInteger): TBigInteger {
        val a = (this / other)
        val b = a * other
        return this - (this / other) * other
    }

    fun modPow(exponent: TBigInteger, m: TBigInteger): TBigInteger {
        return when {
            exponent == ZERO -> ONE
            exponent % 2 == 1 -> (this * modPow(exponent - ONE, m)) % m
            else -> {
                val sqRoot = modPow(exponent / 2, m)
                (sqRoot * sqRoot) % m
            }
        }
    }




    override fun toChar(): Char {
        return '0'
    }

    override fun toDouble(): Double {
        return 0.0
    }

    override fun toFloat(): Float {
        return 0.0f
    }

    override fun toInt(): Int {
        return 0
    }

    override fun toLong(): Long {
        return 0
    }

    override fun toShort(): Short {
        return 0
    }

    override fun toByte(): Byte {
        return 0
    }

    override fun toString(): String {
        if (this.sign == 0) {
            return "0x0"
        }
        var res: String = if (this.sign == -1) "-0x" else "0x"
        var numberStarted = false

        for (i in this.magnitude.size - 1 downTo 0) {
            for (j in BASE_SIZE / 4 - 1 downTo 0) {
                val curByte = (this.magnitude[i] shr 4 * j) and 0xfU
                if (numberStarted or (curByte != 0U)) {
                    numberStarted = true
                    res += hexMapping[curByte]
                }
            }
        }

        return res
    }
}

fun TBigInteger(x: UInt): TBigInteger
        = TBigInteger(1, uintArrayOf(x))
fun TBigInteger(x: ULong): TBigInteger
        = TBigInteger(1, uintArrayOf((x and TBigInteger.BASE).toUInt(), ((x shr TBigInteger.BASE_SIZE) and TBigInteger.BASE).toUInt()))

fun abs(x: TBigInteger): TBigInteger {
    return if (x.sign == 0) x else TBigInteger(1, x.magnitude)
}


//class TBigIntegerIterator(
//    var start: TBigInteger,
//    val endInclusive: TBigInteger
//) : Iterator<TBigInteger> {
//
//    override fun hasNext(): Boolean {
//        return start <= endInclusive
//    }
//
//    override fun next(): TBigInteger {
//        start += TBigInteger.ONE
//        return start
//    }
//}
//
//class TBigIntegerRange(
//    override val start: TBigInteger,
//    override val endInclusive: TBigInteger
//) : ClosedRange<TBigInteger>, Iterable<TBigInteger>{
//    override fun iterator(): Iterator<TBigInteger> {
//        return TBigIntegerIterator(start, endInclusive)
//    }
//}
//
//operator fun TBigInteger.rangeTo(
//    that: TBigInteger
//): ClosedRange<TBigInteger> {
//    return TBigIntegerRange(this, that)
//}
