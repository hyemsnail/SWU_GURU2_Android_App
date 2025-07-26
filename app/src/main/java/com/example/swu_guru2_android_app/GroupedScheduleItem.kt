package com.example.swu_guru2_android_app

data class GroupedScheduleItem(
    val day: String,
    val sets: MutableList<MutableList<ScheduleItem>>
)