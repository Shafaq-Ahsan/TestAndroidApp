package com.app.coderByte.utils

import java.text.SimpleDateFormat
//date parser class
class DataParser  {
    fun parseDate(date: String?): String {
        var newDateData = ""
        try {
            var spf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSSSSS") // server
            val newDate = date?.let{spf.parse(it)}
            spf = SimpleDateFormat("MMM dd, yyyy hh:mm a") // convert formate
            newDate?.let { newDateData = spf.format(it) }
            return newDateData
        } catch (e: Exception) {
        }
        return date?:""
    }
}