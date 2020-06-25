package com.dkarakaya.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class StringUtilTest {

    @Test
    fun `GIVEN lowercase string THEN uppercase the first letter of the string`() {
        val input = "test"

        val expectedOutput = "Test"


        assertEquals(expectedOutput, input.uppercaseFirstLetterAndLowercaseRest())
    }

    @Test
    fun `GIVEN string starts with uppercase THEN keep uppercase the first letter of the string`() {
        val input = "Test"

        val expectedOutput = "Test"


        assertEquals(expectedOutput, input.uppercaseFirstLetterAndLowercaseRest())
    }

    @Test
    fun `GIVEN string contains uppercase THEN uppercase the first letter and lowercase the rest of the string`() {
        val input = "teST"

        val expectedOutput = "Test"


        assertEquals(expectedOutput, input.uppercaseFirstLetterAndLowercaseRest())
    }
}
