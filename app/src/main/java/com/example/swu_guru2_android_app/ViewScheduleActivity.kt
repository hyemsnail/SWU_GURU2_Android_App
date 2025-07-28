package com.example.swu_guru2_android_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import android.widget.TextView




class ViewScheduleActivity : AppCompatActivity() {

    // 요일 정렬 순서 정의
    private val dayOrder = mapOf(
        "월" to 0, "화" to 1, "수" to 2,
        "목" to 3, "금" to 4, "토" to 5, "일" to 6
    )

    // 선택된 세트의 운동 목록을 저장할 변수
    private var selectedWorkoutSet: List<ScheduleItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_schedule)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_schedule)
        val dbManager = DBManager(this)
        val scheduleList = dbManager.getAllSchedules()

        val sortedAndGroupedList = scheduleList
            .sortedWith(compareBy { dayOrder[it.day] ?: 99 }) // 요일별 정렬은 유지
            .toMutableList()

        val selectedSetInfoTextView = findViewById<TextView>(R.id.tv_selected_set_info)

        val adapter = ScheduleAdapter(sortedAndGroupedList, dbManager) { scheduleItems ->
            selectedWorkoutSet = scheduleItems

            val toastMessage = scheduleItems.firstOrNull()?.let { item ->
                val day = item.day ?: "알 수 없음"
                val setNumber = item.setIndex + 1
                // ✅ 선택 정보 표시 업데이트
                selectedSetInfoTextView.text = "선택된 세트: ${day}요일 ${setNumber}번째 세트"
                "선택된 세트: ${day} ${setNumber}번째 세트가 선택되었습니다."
            } ?: run {
                selectedSetInfoTextView.text = "선택된 세트 없음"
                "선택된 세트 정보 없음"
            }

            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            Log.d("ViewScheduleActivity", "선택된 세트 저장됨: ${scheduleItems.joinToString { it.exerciseName ?: "" }}")
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        Log.d("ACTIVITY_FLOW", "ViewScheduleActivity onCreate 실행됨")

        // "선택한 운동 시작" 버튼 클릭 리스너
        val btnStartSelectedWorkout = findViewById<Button>(R.id.btnStartSelectedWorkout)
        btnStartSelectedWorkout.setOnClickListener {
            // selectedWorkoutSet에 저장된 운동이 있을 때만 다음화면 시작
            if (selectedWorkoutSet != null && selectedWorkoutSet!!.isNotEmpty()) {
                val exercisesToStart = ArrayList(selectedWorkoutSet!!.map {
                    Exercise(
                        name = it.exerciseName ?: "",
                        category = it.category ?: "",
                        description = it.description ?: "",
                        duration = it.duration ?: "",
                        caloriesBurned = it.calories ?: ""
                    )
                })

                val intent = Intent(this, StretchingActivity::class.java).apply {
                    putExtra("selectedExercises", exercisesToStart)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "먼저 운동할 세트를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        //"처음으로" 버튼 클릭 리스너
        val backBtn = findViewById<Button>(R.id.btnBackToMain)
        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}