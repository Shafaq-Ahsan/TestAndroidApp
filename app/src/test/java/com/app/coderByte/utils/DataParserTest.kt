package com.app.coderByte.utils

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class DataParserTest {

    lateinit var dateParser: DataParser
    @Before
    fun setup() {
        dateParser = DataParser()
    }

    @Test
    fun parseDate() {
        val result = dateParser.parseDate("1998-09-30 12:01:00 .00567")
        assertEquals("November 30, 1998 12:01 PM",result)
    }
}