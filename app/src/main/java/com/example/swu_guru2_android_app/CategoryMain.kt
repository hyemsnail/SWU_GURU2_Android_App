package com.example.swu_guru2_android_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.swu_guru2_android_app.SelectExercise


class CategoryMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.exercise_select_main) // 카테고리 메인 화면 레이아웃

        val btnGoSelectExercise: Button = findViewById(R.id.btn_go_select_exercise)

        btnGoSelectExercise.setOnClickListener {
            val intent = Intent(this, SelectExercise::class.java) // SelectExercise 클래스 참조
            startActivity(intent)
        }
    }
}