package com.dkarakaya.core.util

import org.junit.Assert.*
import org.junit.Test

class PriceFormatterTest {

    @Test
    fun `GIVEN price below lower limit THEN format price with three decimals`() {
        val input = "1.95€"

        val expectedOutput = "1.950 €"

        assertEquals(expectedOutput, input.formatPrice())
    }

    @Test
    fun `GIVEN price below lower limit without euro symbol THEN format price with three decimals`() {
        val input = "1.95"

        val expectedOutput = "1.950 €"

        assertEquals(expectedOutput, input.formatPrice())
    }

    @Test
    fun `GIVEN price between lower limit and upper limit THEN format price with two decimals`() {
        val input = "195€"

        val expectedOutput = "195.00 €"

        assertEquals(expectedOutput, input.formatPrice())
    }

    @Test
    fun `GIVEN price between lower and upper limit without euro symbol THEN format price with two decimals`() {
        val input = "195"

        val expectedOutput = "195.00 €"

        assertEquals(expectedOutput, input.formatPrice())
    }

    @Test
    fun `GIVEN price above upper limit THEN format price without decimals`() {
        val input = "${DecimalFormatter.UPPER_LIMIT}€"

        val expectedOutput = "1,000,000 €"

        assertEquals(expectedOutput, input.formatPrice())
    }

    @Test
    fun `GIVEN price above upper limit without euro symbol THEN format price without decimals`() {
        val input = DecimalFormatter.UPPER_LIMIT.toString()

        val expectedOutput = "1,000,000 €"

        assertEquals(expectedOutput, input.formatPrice())
    }
}
