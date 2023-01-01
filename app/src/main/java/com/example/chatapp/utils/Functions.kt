package com.example.chatapp.utils

import com.example.chatapp.models.Account
import com.example.chatapp.models.Message
import java.util.*

class Functions {

    companion object {

         fun today(date: Date): String {

            val today = when (date.month) {
                0 -> "yanvar"
                1 -> "fevral"
                2 -> "mart"
                3 -> "aprel"
                4 -> "may"
                5 -> "iyun"
                6 -> "iyul"
                7 -> "avgust"
                8 -> "sentabr"
                9 -> "oktabr"
                10 -> "noyabr"
                11 -> "dekabr"

                else -> ""
            }

            return "${date.date}-$today"
        }

         fun time(date: Date): String {
            var hour = ""
            var minutes = ""

            hour = if (date.hours < 10) "0${date.hours}"
            else date.hours.toString()

            minutes = if (date.minutes < 10) "0${date.minutes}"
            else date.minutes.toString()

            return "${hour}:${minutes}"
        }

        fun week(date: Date): String {
            var day = ""
           day = when(date.day) {
                1 -> "Dush"
                2 -> "Sesh"
                3 -> "Chor"
                4 -> "Pay"
                5 -> "Jum"
                6 -> "Shan"
                7 -> "Yak"

               else -> ""
            }
            return day
        }


         fun calculateDateMessage(date1: Date, value: Message): String {

            var theTime = ""

            val date = date1.date - (value.date?.date ?: 0)
            theTime =  when (date) {
                0 -> value.date?.let { time(it) }.toString()
                1 -> value.date?.let { week(it) }.toString()
                2 -> value.date?.let { week(it) }.toString()
                3 -> value.date?.let { week(it) }.toString()

                else -> (value.date?.let { today(it) })?.substring(0,6).toString()
            }
            return theTime
        }

        fun calculateDateAccount(date1: Date, value: Account): String {

            var theTime = ""

            val date = date1.date - (value.date?.date ?: 0)
            theTime =  when (date) {
                0 -> value.date?.let { time(it) }.toString()
                1 -> value.date?.let { week(it) }.toString()
                2 -> value.date?.let { week(it) }.toString()
                3 -> value.date?.let { week(it) }.toString()

                else -> (value.date?.let { today(it) })?.substring(0,6).toString()
            }
            return theTime
        }
    }

}