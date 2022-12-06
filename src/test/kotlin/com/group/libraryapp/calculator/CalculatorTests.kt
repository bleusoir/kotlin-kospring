package com.group.libraryapp.calculator


fun main() {
    val calculatorTests = CalculatorTests()

    calculatorTests.addTest()
    calculatorTests.minusTest()
}

class CalculatorTests {

    fun addTest() {

        // given
        val calculator = Calculator(5)

        // when
        calculator.add(3)

        // then
        val expectedCalculator = Calculator(8)
        if (calculator != expectedCalculator) {
            throw IllegalStateException()
        }
    }

    fun minusTest() {

        // given
        val calculator = Calculator(5)

        // when
        calculator.minus(3)

        // then
        val expectedCalculator = Calculator(2)
        if (calculator != expectedCalculator) {
            throw IllegalStateException()
        }
    }
}