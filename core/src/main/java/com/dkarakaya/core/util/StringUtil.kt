package com.dkarakaya.core.util

import java.util.*

fun String.uppercaseFirstLetterAndLowercaseRest(): String {
    return this.substring(0, 1).toUpperCase(Locale.getDefault()) + this.substring(1)
        .toLowerCase(Locale.getDefault())
}
