package com.example.swu_guru2_android_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.swu_guru2_android_app.SelectExercise

class SelectStartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_start_main)  // 레이아웃은 그대로 쓸 수 있음

        val btnGoSelectExercise: Button = findViewById(R.id.btn_go_select_exercise)

        btnGoSelectExercise.setOnClickListener {
            val intent = Intent(this, SelectExercise::class.java)
            startActivity(intent)
        }
    }
}