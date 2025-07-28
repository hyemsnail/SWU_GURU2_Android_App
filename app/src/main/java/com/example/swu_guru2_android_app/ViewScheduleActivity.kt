package com.example.swu_guru2_android_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.example.swu_guru2_android_app.DBManager
import com.example.swu_guru2_android_app.R
import com.example.swu_guru2_android_app.ScheduleAdapter
import kotlinx.coroutines.MainScope

class ViewScheduleActivity : AppCompatActivity() {

    // 요일 정렬 순서 정의
    private val dayOrder = mapOf(
        "월" to 0, "화" to 1, "수" to 2,
        "목" to 3, "금" to 4, "토" to 5, "일" to 6
    )

    private var selectedWorkoutSet :List<ScheduleItem>? = null

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

        val adapter = ScheduleAdapter(groupedList, dbManager) { scheduleItems ->
            // 사용자가 세트를 선택했을 때 호출될 람다 함수
            selectedWorkoutSet = scheduleItems // 선택된 세트의 운동 목록 저장
            //선택한 세트 인덱스+1
            val toastMessage = scheduleItems.firstOrNull()?.let { item ->
                val day = item.day
                val setNumber = item.setIndex + 1
                "선택된 세트: ${day} ${setNumber}번째 세트"
            } ?: "선택된 세트 정보 없음" // scheduleItems가 비어있거나 firstOrNull이 null일 경우

            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            Log.d("ViewScheduleActivity", "선택된 세트: ${scheduleItems.joinToString { it.exerciseName ?: "" }}")
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        Log.d("ACTIVITY_FLOW", "ViewScheduleActivity onCreate 실행됨")

        //운동 시작 버튼
        val btnStartSelectedWorkout = findViewById<Button>(R.id.btnStartSelectedWorkout)
        btnStartSelectedWorkout.setOnClickListener {
            if (selectedWorkoutSet != null && selectedWorkoutSet!!.isNotEmpty()) {
                // ScheduleItem 리스트를 Exercise 리스트로 변환 (TrainingActivity에서 Exercise 객체를 받으므로)
                val exercisesToStart = ArrayList(selectedWorkoutSet!!.map {
                    // Exercise 데이터 클래스가 category 필드를 가지고 있다고 가정
                    Exercise(it.exerciseName ?: "", it.description ?: "", it.description ?: "", it.duration ?: "", it.calories ?: "") // description을 category로 사용하지 않는다면 다른 값 전달 필요
                })

                val intent = Intent(this, TrainingActivity::class.java).apply {
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