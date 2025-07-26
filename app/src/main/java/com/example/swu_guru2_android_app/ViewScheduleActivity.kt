package com.example.hometraing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swu_guru2_android_app.DBManager
import com.example.swu_guru2_android_app.R
import com.example.swu_guru2_android_app.ScheduleAdapter
import com.example.swu_guru2_android_app.SelectStartActivity

class ViewScheduleActivity : AppCompatActivity() {

    // 요일 정렬 순서 정의
    private val dayOrder = mapOf(
        "월" to 0, "화" to 1, "수" to 2,
        "목" to 3, "금" to 4, "토" to 5, "일" to 6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_schedule)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_schedule)
        val dbManager = DBManager(this)
        val scheduleList = dbManager.getAllSchedules()

        //
        val groupedList = scheduleList
            .sortedWith(compareBy { dayOrder[it.day] ?: 99 })
            .toMutableList()

        val adapter = ScheduleAdapter(groupedList, dbManager)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        Log.d("ACTIVITY_FLOW", "ViewScheduleActivity onCreate 실행됨")

        //"처음으로" 버튼 클릭 리스너
        val backBtn = findViewById<Button>(R.id.btnBackToMain)
        backBtn.setOnClickListener {
            val intent = Intent(this, SelectStartActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

}