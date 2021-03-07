package com.app.coderByte.utils

import java.text.SimpleDateFormat

class DataParser  {
    fun parseDate(date: String?): String {
        var newDateData = date
        try {
            var spf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSSSSS") // server
            val newDate = spf.parse(date)
            spf = SimpleDateFormat("MMM dd, yyyy hh:mm a") // convert formate
            newDateData = spf.format(newDate)
            return newDateData
        } catch (e: Exception) {
        }
        return date?:""
    }
}