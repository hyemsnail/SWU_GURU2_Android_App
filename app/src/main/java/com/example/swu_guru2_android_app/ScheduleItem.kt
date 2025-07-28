package com.example.swu_guru2_android_app

data class ScheduleItem(
    val day: String,
    val exerciseName: String,
    val description: String?,
    val category: String?,
    val duration: String?,
    val calories: String?,
    val setIndex: Int
)