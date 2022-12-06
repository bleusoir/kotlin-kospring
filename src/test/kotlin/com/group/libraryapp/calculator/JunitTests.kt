package com.group.libraryapp.calculator

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class JunitTests {

    @BeforeEach
    fun before() {
        println("before each!")
    }

    @AfterEach
    fun after() {
        println("after each!")
    }

    @Test
    fun addTest() {
        // given
        val calc = Calculator(5)

        // when
        calc.add(5)

        // then
        assertThat(calc.number).isEqualTo(10)
    }

    @Test
    fun divideExceptionTest() {

        // given
        val calculator = Calculator(5)

        // when & then
        assertThrows<IllegalArgumentException> {
            calculator.divide(0)
        }.apply {
            assertThat(message).isEqualTo("Not divide zero.")
        }
    }
}