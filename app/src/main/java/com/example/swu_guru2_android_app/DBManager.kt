package com.example.swu_guru2_android_app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.swu_guru2_android_app.ScheduleItem
import com.example.swu_guru2_android_app.Exercise
import com.example.swu_guru2_android_app.GroupedScheduleItem

class DBManager(context: Context) : SQLiteOpenHelper(context, "exerciseDB", null, 1) {


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE ExerciseSchedule (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "day TEXT, " +
                    "exercise_name TEXT, " +
                    "exercise_description TEXT, " +
                    "duration TEXT, " +
                    "calories TEXT," +
                    "setIndex INTEGER)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ExerciseSchedule")
        onCreate(db)
    }

    // 저장된 운동 스케줄 전체를 불러오는 함수
    fun getAllSchedules(): List<GroupedScheduleItem> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT day, exercise_name, exercise_description, duration, calories, setIndex FROM ExerciseSchedule",
            null
        )

        val scheduleMap = mutableMapOf<String, MutableMap<Int, MutableList<ScheduleItem>>>()

        if (cursor.moveToFirst()) {
            do {
                val day = cursor.getString(0)
                val name = cursor.getString(1)
                val description = cursor.getString(2) // 사용하지 않아도 index 맞추기용
                val duration = cursor.getString(3)
                val calories = cursor.getString(4)
                val setIndex = cursor.getInt(5)

                val item = ScheduleItem(day, name, description, duration, calories, setIndex)

                Log.d("DB_READ", "불러온 항목: $day / $name / setIndex=$setIndex")

                if (!scheduleMap.containsKey(day)) {
                    scheduleMap[day] = mutableMapOf()
                }
                if (!scheduleMap[day]!!.containsKey(setIndex)) {
                    scheduleMap[day]!![setIndex] = mutableListOf()
                }

                scheduleMap[day]!![setIndex]!!.add(item)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        // 🔽 GroupedScheduleItem으로 변환해서 반환
        return scheduleMap.map { (day, setMap) ->
            val sets = setMap.toSortedMap().values.toMutableList()
            GroupedScheduleItem(day, sets)
        }
    }

    fun insertSchedule(day: String, exercise: Exercise, setIndex: Int) {
        val db = writableDatabase
        val sql = """
        INSERT INTO ExerciseSchedule 
        (day, exercise_name, exercise_description, duration, calories, setIndex) 
        VALUES (?, ?, ?, ?, ?, ?)
    """.trimIndent()

        val args = arrayOf(
            day,
            exercise.name,
            exercise.description,
            exercise.duration,
            exercise.caloriesBurned,
            setIndex
        )

        Log.d(
            "DB_INSERT",
            "Insert: day=$day, name=${exercise.name}, setIndex=$setIndex"
        )  // ★ 이 줄 추가
        db.execSQL(sql, args)
        db.close()
    }


    fun deleteSchedule(day: String, name: String, setIndex: Int) {
        val db = writableDatabase
        db.execSQL(
            "DELETE FROM ExerciseSchedule WHERE day = ? AND exercise_name = ? AND setIndex = ?",
            arrayOf(day, name, setIndex)
        )
        db.close()
    }


    fun deleteScheduleByDay(day: String) {
        val db = this.writableDatabase
        db.delete("ExerciseSchedule", "day = ?", arrayOf(day))
        db.close()
    }
}