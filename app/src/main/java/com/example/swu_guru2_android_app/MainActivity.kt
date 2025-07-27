package com.example.swu_guru2_android_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val goSelectExerciseBtn = findViewById<Button>(R.id.btn_go_select_exercise)
        goSelectExerciseBtn.setOnClickListener {
            val intent = Intent(this, SelectExercise::class.java)
            startActivity(intent)
        }

        val goScheduleBtn = findViewById<Button>(R.id.btn_go_select_exercise2)
        goScheduleBtn.setOnClickListener {
            val intent = Intent(this, ViewScheduleActivity::class.java)  // ✅ 운동 스케줄 화면으로 이동
            startActivity(intent)
        }
    }
}
