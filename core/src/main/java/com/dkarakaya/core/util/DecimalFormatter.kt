package com.dkarakaya.core.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

object DecimalFormatter {

    /** Upper limit for removing the decimals. **/
    const val UPPER_LIMIT: Float = 999999.995f

    /** Lower limit for showing three decimal places. **/
    private const val LOWER_LIMIT: Int = 2

    /**
     * Return the number of decimal places for a value.
     * The decimals can be removed for an upper limit.
     *
     * @param value is used to calculate the limits to change the decimals
     * @param hideOnUpperLimit the upper limit to remove the decimals, default is Float.MAX_VALUE
     *
     * @return the number of decimals
     */
    fun getDecimalPlaces(
        value: BigDecimal,
        hideOnUpperLimit: Float = Float.MAX_VALUE
    ): Int {
        return when {
            value >= hideOnUpperLimit.toBigDecimal() -> 0
            value.abs() < LOWER_LIMIT.toBigDecimal() -> 3
            else -> 2
        }
    }

    private val noDecimalPointRoundUpFormatter = DecimalFormat().apply {
        minimumIntegerDigits = 1
        minimumFractionDigits = 0
        maximumFractionDigits = 0
        negativePrefix = "- "
        roundingMode = RoundingMode.HALF_UP
    }

    private val oneDecimalPointRoundUpFormatter = DecimalFormat().apply {
        minimumIntegerDigits = 1
        minimumFractionDigits = 1
        maximumFractionDigits = 1
        negativePrefix = "- "
        roundingMode = RoundingMode.HALF_UP
    }

    private val twoDecimalPointRoundUpFormatter = DecimalFormat().apply {
        minimumIntegerDigits = 1
        minimumFractionDigits = 2
        maximumFractionDigits = 2
        negativePrefix = "- "
        roundingMode = RoundingMode.HALF_UP
    }

    private val threeDecimalPointRoundUpFormatter = DecimalFormat().apply {
        minimumIntegerDigits = 1
        minimumFractionDigits = 3
        maximumFractionDigits = 3
        negativePrefix = "- "
        roundingMode = RoundingMode.HALF_UP
    }

    fun formatDecimals(
        value: BigDecimal,
        numberOfDecimals: NumberDecimals,
        suffix: Suffix = Suffix.None
    ): String {
        val formattedDecimal = formatDecimal(numberOfDecimals, value)
        return addSuffix(formattedDecimal, suffix)
    }

    private fun addSuffix(formattedDecimal: String, suffix: Suffix): String {
        return when (suffix) {
            Suffix.None -> formattedDecimal
            Suffix.Euro -> String.format("%s %s", formattedDecimal, "€")
            Suffix.Percentage -> String.format("%s %s", formattedDecimal, "%")
        }
    }

    private fun formatDecimal(numberOfDecimals: NumberDecimals, value: BigDecimal): String {
        return when (numberOfDecimals) {
            NumberDecimals.NONE -> noDecimalPointRoundUpFormatter.format(value)
            NumberDecimals.ONE -> oneDecimalPointRoundUpFormatter.format(value)
            NumberDecimals.TWO -> twoDecimalPointRoundUpFormatter.format(value)
            NumberDecimals.THREE -> threeDecimalPointRoundUpFormatter.format(value)
        }
    }

    sealed class Suffix {
        object None : Suffix()
        object Euro : Suffix()
        object Percentage : Suffix()
    }

    enum class NumberDecimals {
        NONE,
        ONE,
        TWO,
        THREE
    }
}
