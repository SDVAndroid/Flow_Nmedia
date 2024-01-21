package ru.netology.nmedia.utils

import java.text.DecimalFormat
import kotlin.math.pow

object FormatNumber {
    fun formatNumber(number: Long): String {
        if (number < 1000) {
            return number.toString()
        }
        val suffixes = arrayOf("", "K", "M")
        val formatter = DecimalFormat("#,##0.#")
        val exp = (Math.log10(number.toDouble()) / 3).toInt()
        return formatter.format(number / 10.0.pow(exp * 3)) + suffixes[exp]
    }
}
