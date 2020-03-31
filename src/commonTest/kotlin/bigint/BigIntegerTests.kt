package bigint

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals


class BigIntegerConstructorTest {
    @Test
    fun testConstructorZero() {
        assertEquals(TBigInteger(0), TBigInteger(0, uintArrayOf()))
    }

    @Test
    fun testConstructor8() {
        assertEquals(TBigInteger(8), TBigInteger(1, uintArrayOf(8U)))
    }

    @Test
    fun testConstructor_0xffffffffaL() {
        val x = TBigInteger(-0xffffffffaL)
        val y = TBigInteger(-1, uintArrayOf(0xfffffffaU, 0xfU))
        assertEquals(x, y)
    }
}

class BigIntegerCompareTest {
    @Test
    fun testCompare1_2() {
        val x = TBigInteger(1)
        val y = TBigInteger(2)
        assertTrue { x < y }
    }

    @Test
    fun testCompare0_0() {
        val x = TBigInteger(0)
        val y = TBigInteger(0)
        assertEquals(x, y)
    }

    @Test
    fun testCompare1__2() {
        val x = TBigInteger(1)
        val y = TBigInteger(-2)
        assertTrue { x > y }
    }

    @Test
    fun testCompare_1__2() {
        val x = TBigInteger(-1)
        val y = TBigInteger(-2)
        assertTrue { x > y }
    }

    @Test
    fun testCompare_2__1() {
        val x = TBigInteger(-2)
        val y = TBigInteger(-1)
        assertTrue { x < y }
    }

    @Test
    fun testCompare12345_12345() {
        val x = TBigInteger(12345)
        val y = TBigInteger(12345)
        assertEquals(x, y)
    }

    @Test
    fun testEqualsWithInt() {
        val x = TBigInteger(-12345)
        // TODO: replace with ==
        assertTrue { x.equals(-12345) }
    }

    @Test
    fun testEqualsWithUInt() {
        val x = TBigInteger(12345)
        // TODO: replace with ==
        assertTrue { x.equals(12345U) }
    }

    @Test
    fun testEqualsWithLong() {
        val x = TBigInteger(12345)
        // TODO: replace with ==
        assertTrue { x.equals(12345L) }
    }

    @Test
    fun testEqualsWithULong() {
        val x = TBigInteger(12345)
        // TODO: replace with ==
        assertTrue { x.equals(12345UL) }
    }

    @Test
    fun testCompareBigNumbersGreater() {
        val x = TBigInteger(0xfffffffffL)
        val y = TBigInteger(0xffffffffaL)
        assertTrue { x > y }
    }

    @Test
    fun testCompareBigNumbersEqual() {
        val x = TBigInteger(0xffffffffaL)
        val y = TBigInteger(0xffffffffaL)
        assertEquals(x, y)
    }

    @Test
    fun testCompareBigNumbersLess() {
        val x = TBigInteger(-0xffffffffaL)
        val y = TBigInteger(0xffffffffaL)
        assertTrue { x < y }
    }
}

class BigIntegerOperationsTest {
    @Test
    fun testPlus_1_1() {
        val x = TBigInteger(1)
        val y = TBigInteger(1)

        val res = x + y
        val sum = TBigInteger(2)

        assertEquals(sum, res)
    }

    @Test
    fun testPlusBigNumbers() {
        val x = TBigInteger(0x7fffffff)
        val y = TBigInteger(0x7fffffff)
        val z = TBigInteger(0x7fffffff)

        val res = x + y + z
        val sum = TBigInteger(1, uintArrayOf(0x7ffffffdU, 0x1U))

        assertEquals(sum, res)
    }

    @Test
    fun testUnaryMinus() {
        val x = TBigInteger(1234)
        val y = TBigInteger(-1234)
        assertEquals(-x, y)
    }

    @Test
    fun testMinus_2_1() {
        val x = TBigInteger(2)
        val y = TBigInteger(1)

        val res = x - y
        val sum = TBigInteger(1)

        assertEquals(sum, res)
    }

    @Test
    fun testMinus__2_1() {
        val x = TBigInteger(-2)
        val y = TBigInteger(1)

        val res = x - y
        val sum = TBigInteger(-3)

        assertEquals(sum, res)
    }

    @Test
    fun testMinus___2_1() {
        val x = TBigInteger(-2)
        val y = TBigInteger(1)

        val res = -x - y
        val sum = TBigInteger(1)

        assertEquals(sum, res)
    }

    @Test
    fun testMinusBigNumbers() {
        val x = TBigInteger(12345)
        val y = TBigInteger(0xffffffffaL)

        val res = x - y
        val sum = TBigInteger(-0xfffffcfc1L)

        assertEquals(sum, res)
    }

    @Test
    fun testMultiply_2_3() {
        val x = TBigInteger(2)
        val y = TBigInteger(3)

        val res = x * y
        val prod = TBigInteger(6)

        assertEquals(prod, res)
    }

    @Test
    fun testMultiply__2_3() {
        val x = TBigInteger(-2)
        val y = TBigInteger(3)

        val res = x * y
        val prod = TBigInteger(-6)

        assertEquals(prod, res)
    }

    @Test
    fun testMultiply_0xfff123_0xfff456() {
        val x = TBigInteger(0xfff123)
        val y = TBigInteger(0xfff456)

        val res = x * y
        val prod = TBigInteger(0xffe579ad5dc2L)

        assertEquals(prod, res)
    }

    @Test
    fun testMultiplyUInt_0xfff123_0xfff456() {
        val x = TBigInteger(0xfff123)
        val y = 0xfff456U

        val res = x * y
        val prod = TBigInteger(0xffe579ad5dc2L)

        assertEquals(prod, res)
    }

    @Test
    fun testMultiplyInt_0xfff123__0xfff456() {
        val x = TBigInteger(0xfff123)
        val y = -0xfff456

        val res = x * y
        val prod = TBigInteger(-0xffe579ad5dc2L)

        assertEquals(prod, res)
    }

    @Test
    fun testMultiply_0xffffffff_0xffffffff() {
        val x = TBigInteger(0xffffffffL)
        val y = TBigInteger(0xffffffffL)

        val res = x * y
        val prod = TBigInteger(0xfffffffe00000001UL)

        assertEquals(prod, res)
    }

    @Test
    fun test_shr_20() {
        val x = TBigInteger(20)
        assertEquals(TBigInteger(10), x shr 1)
    }

    @Test
    fun test_shl_20() {
        val x = TBigInteger(20)
        assertEquals(TBigInteger(40), x shl 1)
    }

    @Test
    fun test_shl_1_0() {
        assertEquals(TBigInteger.ONE, TBigInteger.ONE shl 0)
    }

    @Test
    fun test_shl_1_32() {
        assertEquals(TBigInteger(0x100000000UL), TBigInteger.ONE shl 32)
    }

    @Test
    fun test_shl_1_33() {
        assertEquals(TBigInteger(0x200000000UL), TBigInteger.ONE shl 33)
    }

    @Test
    fun test_shr_1_33_33() {
        assertEquals(TBigInteger.ONE, (TBigInteger.ONE shl 33) shr 33)
    }

    @Test
    fun test_shr_1_32() {
        assertEquals(TBigInteger.ZERO, TBigInteger.ONE shr 32)
    }

    @Test
    fun test_and_123_456() {
        val x = TBigInteger(123)
        val y = TBigInteger(456)
        assertEquals(TBigInteger(72), x and y)
    }

    @Test
    fun test_or_123_456() {
        val x = TBigInteger(123)
        val y = TBigInteger(456)
        assertEquals(TBigInteger(507), x or y)
    }

    @Test
    fun test_asd() {
        assertEquals(TBigInteger.ONE, TBigInteger.ZERO or ((TBigInteger(20) shr 4) and TBigInteger.ONE))
    }

    @Test
    fun testDivision_6_3() {
        val x = TBigInteger(6)
        val y = 3U

        val res = x / y
        val div = TBigInteger(2)

        assertEquals(div, res)
    }

    @Test
    fun testBigDivision_6_3() {
        val x = TBigInteger(6)
        val y = TBigInteger(3)

        val res = x / y
        val div = TBigInteger(2)

        assertEquals(div, res)
    }

    @Test
    fun testDivision_20_3() {
        val x = TBigInteger(10)
        val y = TBigInteger(3)

        val res = x / y
        val div = TBigInteger(3)

        assertEquals(div, res)
    }

    @Test
    fun testDivision_20__3() {
        val x = TBigInteger(20)
        val y = -3

        val res = x / y
        val div = TBigInteger(-6)

        assertEquals(div, res)
    }

    @Test
    fun testBigDivision_20__3() {
        val x = TBigInteger(20)
        val y = TBigInteger(-3)

        val res = x / y
        val div = TBigInteger(-6)

        assertEquals(div, res)
    }

    @Test
    fun testDivision_0xfffffffe00000001_0xffffffff() {
        val x = TBigInteger(0xfffffffe00000001UL)
        val y = 0xffffffffU

        val res = x / y
        val div = TBigInteger(0xffffffffL)

        assertEquals(div, res)
    }

    @Test
    fun testBigDivision_0xfffffffe00000001_0xffffffff() {
        val x = TBigInteger(0xfffffffe00000001UL)
        val y = TBigInteger(0xffffffffU)

        val res = x / y
        val div = TBigInteger(0xffffffffL)

        assertEquals(div, res)
    }

    @Test
    fun testBigDivision_0xfffffffeabcdef01_0xfffffffeabcUL() {
        val x = TBigInteger(0xfffffffeabcdef01UL)
        val y = TBigInteger(0xfffffffeabcUL)

        val res = x / y
        val div = TBigInteger(0x100000L)

        assertEquals(div, res)
    }

    @Test
    fun testMod_20_3() {
        val x = TBigInteger(20)
        val y = 3

        val res = x % y
        val mod = 2

        assertEquals(mod, res)
    }

    @Test
    fun testBigMod_20_3() {
        val x = TBigInteger(20)
        val y = TBigInteger(3)

        val res = x % y
        val mod = TBigInteger(2)

        assertEquals(mod, res)
    }

    @Test
    fun testMod_0xfffffffe00000001_12345() {
        val x = TBigInteger(0xfffffffe00000001UL)
        val y = 12345

        val res = x % y
        val mod = 1980

        assertEquals(mod, res)
    }

    @Test
    fun testBigMod_0xfffffffe00000001_12345() {
        val x = TBigInteger(0xfffffffe00000001UL)
        val y = TBigInteger(12345)

        val res = x % y
        val mod = TBigInteger(1980)

        assertEquals(mod, res)
    }

    @Test
    fun testModPow_3_10_17() {
        val x = TBigInteger(3)
        val exp = TBigInteger(10)
        val mod = TBigInteger(17)

        val res = TBigInteger(8)

        return assertEquals(res, x.modPow(exp, mod))
    }

    @Test
    fun testModPowBigNumbers() {
        val x = TBigInteger(0xfffffffeabcdef01UL)
        val exp = TBigInteger(2)
        val mod = TBigInteger(0xfffffffeabcUL)

        val res = TBigInteger(0xc2253cde01)

        return assertEquals(res, x.modPow(exp, mod))
    }

    @Test
    fun testModBigNumbers() {
        val x = TBigInteger(0xfffffffeabcdef01UL)
        val mod = TBigInteger(0xfffffffeabcUL)

        val res = TBigInteger(0xdef01)

        return assertEquals(res, x % mod)
    }
}


class BigIntegerConversionsTest {
    @Test
    fun testToString0x10() {
        val x = TBigInteger(0x10)
        assertEquals("0x10", x.toString())
    }

    @Test
    fun testToString0x17ffffffd() {
        val x = TBigInteger(0x17ffffffdL)
        assertEquals("0x17ffffffd", x.toString())
    }

    @Test
    fun testToString_0x17ead2ffffd() {
        val x = TBigInteger(-0x17ead2ffffdL)
        assertEquals("-0x17ead2ffffd", x.toString())
    }
}


//class BigIntegerIteratorTest {
//    @Test
//    fun testRange_10_20() {
//        val actual = TBigInteger(10)..TBigInteger(20)
//        val expected = (10..20).map { TBigInteger(it) }
//
//        for ((x, y) in actual zip expected) {
//
//        }
//
//    }
//}

