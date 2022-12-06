package com.group.libraryapp.calculator

data class Calculator(
    private var _number: Int
) {

    val number: Int
        get() = this._number

    fun add(operand: Int) {
        this._number += operand
    }

    fun minus(operand: Int) {
        this._number -= operand
    }

    fun multiply(operand: Int) {
        this._number *= operand
    }

    fun divide(operand: Int) {
        if (operand == 0) {
            throw IllegalArgumentException("Not divide zero.")
        }

        this._number /= operand
    }
}