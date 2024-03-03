package com.example.calculator;

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CalculatorViewModel: ViewModel() {
    val TAG = CalculatorViewModel::class.java.simpleName

    var state by mutableStateOf(CalculatorState())
        private set

    fun onAction(action: Actions) {
        when(action) {
            is Actions.Digit -> enterDigit(action.digit)
            is Actions.Clear -> reset()
            is Actions.Delete -> deletion()
            is Actions.Operation -> enterOperation(action.operation)
            is Actions.Decimal -> enterDecimal()
            is Actions.Calculate -> calculate()
        }
    }

    private fun enterOperation(operation: Operations) {
        if(state.number1.isNotBlank()) {
            state = state.copy(operation = operation)
            Log.d(TAG, "The operation entered is: " + state.operation?.symbol);
        }
    }

    private fun reset() {
        Log.d(TAG, "Cleared content (reset calculator states)");

        state = CalculatorState()
    }

    private fun deletion() {
        when {
            state.number2.isNotBlank() -> state = state.copy(
                number2 = state.number2.dropLast(1)
            )
            state.operation != null -> state = state.copy(
                operation = null
            )
            state.number1.isNotBlank() -> state = state.copy(
                number1 = state.number1.dropLast(1)
            )
        }
    }

    private fun calculate() {
        val number1 = state.number1.toDoubleOrNull()
        val number2 = state.number2.toDoubleOrNull()
        if(number1 != null && number2 != null) {

            Log.d(TAG, "Performing operation" )
            Log.d(TAG, state.number1 + state.operation?.symbol + state.number2);

            val result = when(state.operation) {
                is Operations.Add -> number1 + number2
                is Operations.Subtract -> number1 - number2
                is Operations.Multiply -> number1 * number2
                is Operations.Divide -> number1 / number2
                null -> return
            }

            Log.d(TAG, "Result for above operation is $result")

            state = state.copy(
                number1 = result.toString().take(15),
                number2 = "",
                operation = null
            )
        }
    }

    private fun enterDecimal() {
        if(state.operation == null && !state.number1.contains(".") && state.number1.isNotBlank()) {
            state = state.copy(
                number1 = state.number1 + "."
            )
            Log.d(TAG, "Added decimal after" + state.number1)
            return
        } else if(!state.number2.contains(".") && state.number2.isNotBlank()) {
            state = state.copy(
                number2 = state.number2 + "."
            )
            Log.d(TAG, "Added decimal after" + state.number2)
        }
    }

    private fun enterDigit(number: Any) {
        if(state.operation == null) {
            if(state.number1.length >= MAX_NUM_LENGTH) {
                Log.e(TAG, "Number 1 exceeded max length")
                return
            }
            state = state.copy(
                number1 = state.number1 + number
            )
            return
        }
        if(state.number2.length >= MAX_NUM_LENGTH) {
            Log.e(TAG, "Number 2 exceeded max length")
            return
        }
        state = state.copy(
            number2 = state.number2 + number
        )
    }

    companion object {
        private const val MAX_NUM_LENGTH = 8
    }
}
