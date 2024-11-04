package com.grepp.somun.chat.util

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object TimeAgoUtil {
    private val todayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("a h:mm") // 오전/오후 h:mm
    private val monthDayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M월 d일")
    private val yearMonthDayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")

    fun getElapsedTime(createdAt: LocalDateTime): String {
        val now = LocalDateTime.now()
        val duration = Duration.between(createdAt, now)

        val days = duration.toDays()
        val years = ChronoUnit.YEARS.between(createdAt, now)

        return when {
            days == 0L -> {
                // 오늘 날짜인 경우 오전/오후 h:mm 형식
                createdAt.format(todayFormatter)
            }
            days == 1L -> {
                // 1일 전인 경우 '어제'로 표시
                "어제"
            }
            years > 0 -> {
                // 1년 전 이상인 경우 yyyy년 M월 d일 형식
                createdAt.format(yearMonthDayFormatter)
            }
            else -> {
                // 2일 전부터 1년 미만인 경우 M월 d일 형식
                createdAt.format(monthDayFormatter)
            }
        }
    }

    // 오전/오후 시간 표시
    fun formatToAmPm(dateTime: LocalDateTime): String {
        return dateTime.format(todayFormatter)
    }
}
