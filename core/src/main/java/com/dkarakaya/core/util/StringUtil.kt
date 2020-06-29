package com.dkarakaya.core.util

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import androidx.annotation.StringRes
import java.util.*

fun String.uppercaseFirstLetterAndLowercaseRest(): String {
    return this.substring(0, 1).toUpperCase(Locale.getDefault()) + this.substring(1)
        .toLowerCase(Locale.getDefault())
}

fun Context.getSpannable(@StringRes resId: Int, vararg formatArgs: CharSequence): CharSequence {
    val baseString = getString(resId)
    var template = baseString
    if (formatArgs.size == 1) {
        template = template.replace("%s", "^1")
    } else {
        for (i in 0..formatArgs.size) {
            template = template.replace("%$i\$s", "^$i")
        }
    }
    return TextUtils.expandTemplate(template, *formatArgs)
}

fun String.makeBold(): SpannableStringBuilder {
    val spannableHeadline = SpannableStringBuilder(this)
    val start = this.indexOf(this).takeIf { it >= 0 } ?: return spannableHeadline
    val end = this.length.takeIf { it >= 0 && it >= start } ?: return spannableHeadline
    spannableHeadline.setSpan(
        StyleSpan(Typeface.BOLD),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableHeadline
}

fun <T> T.setTextSize(textSize: Int): SpannableStringBuilder {
    val string = this.toString()
    val spannableHeadline = SpannableStringBuilder(string)
    val start = string.indexOf(string).takeIf { it >= 0 } ?: return spannableHeadline
    val end = string.length.takeIf { it >= 0 && it >= start } ?: return spannableHeadline
    spannableHeadline.setSpan(
        AbsoluteSizeSpan(textSize),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableHeadline
}
