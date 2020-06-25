package com.dkarakaya.core.util

fun String.formatPrice(hideOnUpperLimit: Float = DecimalFormatter.UPPER_LIMIT): String {
    val price = this.substringBefore("€").toBigDecimal()
    val decimalPlaces = DecimalFormatter.getDecimalPlaces(
        value = price,
        hideOnUpperLimit = hideOnUpperLimit
    )
    val numberDecimals = when (decimalPlaces) {
        0 -> DecimalFormatter.NumberDecimals.NONE
        2 -> DecimalFormatter.NumberDecimals.TWO
        else -> DecimalFormatter.NumberDecimals.THREE
    }
    return DecimalFormatter.formatDecimals(
        value = price,
        numberOfDecimals = numberDecimals,
        suffix = DecimalFormatter.Suffix.Euro
    )
}
