package com.example.swu_guru2_android_app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBManager(context: Context) : SQLiteOpenHelper(context, DBManager.DATABASE_NAME, null, DBManager.DATABASE_VERSION) {

    // 데이터베이스 상수를 위한 companion object 추가
    companion object {
        private const val DATABASE_NAME = "exerciseDB"
        private const val DATABASE_VERSION = 2

        // 테이블 및 컬럼 이름
        private const val TABLE_NAME = "ExerciseSchedule"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DAY = "day"
        private const val COLUMN_EXERCISE_NAME = "exercise_name"
        private const val COLUMN_CATEGORY = "category" // 새로운 컬럼 이름
        private const val COLUMN_DESCRIPTION = "exercise_description"
        private const val COLUMN_DURATION = "duration"
        private const val COLUMN_CALORIES = "calories"
        private const val COLUMN_SET_INDEX = "setIndex"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 'category'를 포함하도록 CREATE TABLE 쿼리 업데이트
        val createTableSql = """
            CREATE TABLE ${TABLE_NAME} (
                ${COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, 
                ${COLUMN_DAY} TEXT, 
                ${COLUMN_EXERCISE_NAME} TEXT, 
                ${COLUMN_CATEGORY} TEXT DEFAULT '', -- 새 컬럼을 기본값 빈 문자열로 추가
                ${COLUMN_DESCRIPTION} TEXT, 
                ${COLUMN_DURATION} TEXT, 
                ${COLUMN_CALORIES} TEXT,
                ${COLUMN_SET_INDEX} INTEGER
            )
        """.trimIndent()
        db.execSQL(createTableSql)
        Log.d("DBManager", "데이터베이스가 버전 $DATABASE_VERSION 로 생성되었습니다.")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // 버전 1에서 2로 업그레이드: 'category' 컬럼 추가
            // 기존 데이터를 유지하기 위해 ALTER TABLE 사용
            val alterTableSql = "ALTER TABLE ${TABLE_NAME} ADD COLUMN ${COLUMN_CATEGORY} TEXT DEFAULT ''"
            db.execSQL(alterTableSql)
            Log.d("DBManager", "데이터베이스가 업그레이드되었습니다: '$COLUMN_CATEGORY' 컬럼 추가.")
        }
        // 더 많은 버전이 있다면, 'if (oldVersion < X)' 블록을 추가합니다.
    }

    // 운동 스케줄 항목 저장 함수
    fun insertSchedule(day: String, exercise: Exercise, setIndex: Int) {
        val db = writableDatabase
        val sql = """
            INSERT INTO ${TABLE_NAME} 
            (${COLUMN_DAY}, ${COLUMN_EXERCISE_NAME}, ${COLUMN_CATEGORY}, ${COLUMN_DESCRIPTION}, ${COLUMN_DURATION}, ${COLUMN_CALORIES}, ${COLUMN_SET_INDEX}) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        val args = arrayOf(
            day,
            exercise.name,
            exercise.category, // 카테고리 값을 전달합니다.
            exercise.description,
            exercise.duration,
            exercise.caloriesBurned,
            setIndex
        )

        Log.d(
            "DB_INSERT",
            "삽입: day=$day, name=${exercise.name}, category=${exercise.category}, setIndex=$setIndex"
        )
        db.execSQL(sql, args)
        db.close()
    }

    // 저장된 전체 운동 스케줄 불러오기 함수
    fun getAllSchedules(): List<GroupedScheduleItem> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT ${COLUMN_DAY}, ${COLUMN_EXERCISE_NAME}, ${COLUMN_CATEGORY}, ${COLUMN_DESCRIPTION}, ${COLUMN_DURATION}, ${COLUMN_CALORIES}, ${COLUMN_SET_INDEX} FROM ${TABLE_NAME}",
            null
        )

        val scheduleMap = mutableMapOf<String, MutableMap<Int, MutableList<ScheduleItem>>>()

        if (cursor.moveToFirst()) {
            val dayColIndex = cursor.getColumnIndex(COLUMN_DAY)
            val nameColIndex = cursor.getColumnIndex(COLUMN_EXERCISE_NAME)
            val categoryColIndex = cursor.getColumnIndex(COLUMN_CATEGORY) // 카테고리 인덱스 가져오기
            val descriptionColIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION)
            val durationColIndex = cursor.getColumnIndex(COLUMN_DURATION)
            val caloriesColIndex = cursor.getColumnIndex(COLUMN_CALORIES)
            val setIndexColIndex = cursor.getColumnIndex(COLUMN_SET_INDEX)

            do {
                // getString/getInt 전에 컬럼 인덱스가 유효한지 확인합니다.
                val day = if (dayColIndex != -1) cursor.getString(dayColIndex) else ""
                val name = if (nameColIndex != -1) cursor.getString(nameColIndex) else ""
                val category = if (categoryColIndex != -1) cursor.getString(categoryColIndex) else null // 카테고리 검색
                val description = if (descriptionColIndex != -1) cursor.getString(descriptionColIndex) else ""
                val duration = if (durationColIndex != -1) cursor.getString(durationColIndex) else ""
                val calories = if (caloriesColIndex != -1) cursor.getString(caloriesColIndex) else ""
                val setIndex = if (setIndexColIndex != -1) cursor.getInt(setIndexColIndex) else 0

                // 카테고리를 포함하여 ScheduleItem 생성
                val item = ScheduleItem(day, name, category, description, duration, calories, setIndex)

                Log.d("DB_READ", "불러온 항목: $day / $name / category=$category / setIndex=$setIndex")

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

        // GroupedScheduleItem으로 변환
        return scheduleMap.map { (day, setMap) ->
            val sets = setMap.toSortedMap().values.toMutableList()
            GroupedScheduleItem(day, sets)
        }
    }

    // 특정 스케줄 항목 삭제
    fun deleteSchedule(day: String, name: String, setIndex: Int) {
        val db = writableDatabase
        db.delete(
            TABLE_NAME,
            "${COLUMN_DAY} = ? AND ${COLUMN_EXERCISE_NAME} = ? AND ${COLUMN_SET_INDEX} = ?",
            arrayOf(day, name, setIndex.toString())
        )
        db.close()
    }

    // 특정 요일 및 세트 인덱스에 대한 모든 스케줄 항목 삭제
    fun deleteScheduleByDayAndSetIndex(day: String, setIndex: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "${COLUMN_DAY} = ? AND ${COLUMN_SET_INDEX} = ?", arrayOf(day, setIndex.toString()))
        db.close()
    }
}