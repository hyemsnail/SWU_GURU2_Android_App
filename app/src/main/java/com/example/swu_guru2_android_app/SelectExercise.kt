package com.example.hometraing

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog

//선택화면
class SelectExercise : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter

    private lateinit var selectedExercisesRecyclerView: RecyclerView // 선택된 운동 목록
    private lateinit var selectedExerciseAdapter: SelectedExerciseAdapter // 선택된 운동 어댑터

    private val allExercises = mutableListOf<Exercise>() // 모든 운동 데이터를 저장할 리스트
    private val selectedExercises = mutableListOf<Exercise>() //사용자 선택한 운동 저장 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_exercise)

        // UI 요소 초기화
        val btnUpperBody: Button = findViewById(R.id.btn_upper_body)
        val btnAbs: Button = findViewById(R.id.btn_abs)
        val btnLowerBody: Button = findViewById(R.id.btn_lower_body)
        val btnFullBody: Button = findViewById(R.id.btn_full_body)
        recyclerView = findViewById(R.id.recycler_view_exercises)

        // RecyclerView 설정
        recyclerView.layoutManager = LinearLayoutManager(this)

        //선택된 운동 목록 RecyclerView 초기화
        selectedExercisesRecyclerView = findViewById(R.id.recycler_view_selected_exercises)
        selectedExercisesRecyclerView.layoutManager = LinearLayoutManager(this)
        selectedExerciseAdapter = SelectedExerciseAdapter(selectedExercises) {
                exercise ->
            // 선택된 운동 항목 클릭 시 삭제
            selectedExercises.remove(exercise)
            Toast.makeText(this, "${exercise.name}이(가) 선택 목록에서 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            selectedExerciseAdapter.updateList()
        }// 선택된 운동 리스트 전달
        selectedExercisesRecyclerView.adapter = selectedExerciseAdapter

        // 모든 운동 데이터 초기화 (실제 앱에서는 데이터베이스 등에서 가져옴)
        initAllExercises()

        // 버튼 클릭 리스너 설정
        btnUpperBody.setOnClickListener {
            displayExercises("상체")
        }
        btnAbs.setOnClickListener {
            displayExercises("복근")
        }
        btnLowerBody.setOnClickListener {
            displayExercises("하체")
        }
        btnFullBody.setOnClickListener {
            displayExercises("전신")
        }

        // 초기 화면에 상체 운동 표시 (선택 사항)
        displayExercises("상체")
        selectedExerciseAdapter.updateList()
    }

    // 모든 운동 데이터를 초기화하는 함수
    private fun initAllExercises() {
        allExercises.add(Exercise("푸쉬업 (Push-up)", "상체", "가슴, 어깨, 삼두근을 단련하는 대표적인 상체 운동입니다.", "30초", "약 5~7 kcal"))
        allExercises.add(Exercise("숄더 프레스 (덤벨/밴드)", "상체", "어깨 근육을 강화하는 운동입니다. 덤벨이나 밴드를 사용할 수 있습니다.", "60초", "약 7~10 kcal"))
        allExercises.add(Exercise("숄더 탭 (Shoulder taps)", "상체", "코어 안정성과 어깨 안정성을 동시에 기를 수 있는 운동입니다.", "30초", "약 6~8 kcal"))

        allExercises.add(Exercise("크런치 (Crunch)", "복근", "복직근을 단련하는 기본적인 복근 운동입니다.", "30초", "약 5~6 kcal"))
        allExercises.add(Exercise("플랭크 (Plank)", "복근", "코어 근육을 강화하는 등척성 운동입니다.", "60초", "약 7~9 kcal"))
        allExercises.add(Exercise("레그 레이즈 (Leg raise)", "복근", "하복부를 단련하는 데 효과적인 운동입니다.", "30초", "약 6~8 kcal"))

        allExercises.add(Exercise("스쿼트 (Squat)", "하체", "하체 전체와 코어 근육을 강화하는 기본적인 하체 운동입니다.", "30초", "약 6~8 kcal"))
        allExercises.add(Exercise("런지 (Lunge)", "하체", "허벅지와 엉덩이를 강화하는 운동입니다. 균형감각 향상에도 좋습니다.", "60초", "약 10~12 kcal"))
        allExercises.add(Exercise("힙 브릿지 (Glute bridge)", "하체", "둔근을 강화하는 데 효과적인 운동입니다. 허리 부담이 적습니다.", "30초", "약 6~8 kcal"))

        allExercises.add(Exercise("버피 (Burpee)", "전신", "유산소와 무산소 운동을 결합한 전신 고강도 운동입니다.", "30초", "약 10~12 kcal"))
        allExercises.add(Exercise("점핑잭 (Jumping jacks)", "전신", "유산소 운동으로 심박수를 높이고 전신을 활성화합니다.", "30초", "약 7~9 kcal"))
        allExercises.add(Exercise("마운틴 클라이머", "전신", "코어와 하체를 동시에 사용하는 유산소성 전신 운동입니다.", "60초", "약 12~15 kcal"))
    }

    // 선택된 카테고리에 해당하는 운동 목록을 RecyclerView에 표시하는 함수
    private fun displayExercises(category: String) {
        val filteredExercises = allExercises.filter { it.category == category }
        exerciseAdapter = ExerciseAdapter(filteredExercises) { exercise ->
            // 운동 항목 클릭 시 동작 (팝업)
            showExerciseDetailPopup(exercise)
        }
        recyclerView.adapter = exerciseAdapter
    }


    private fun showExerciseDetailPopup(exercise: Exercise) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(exercise.name) // 팝업 제목 (운동 이름)

        // 팝업에 표시할 상세 내용 구성
        val details = """
            ${exercise.description}
            
            ⏱️ **운동 시간:** ${exercise.duration}
            🔥 **예상 소모 칼로리:** ${exercise.caloriesBurned}
        """.trimIndent() // trimIndent()로 들여쓰기 제거

        builder.setMessage(details) // 구성된 상세 내용을 팝업 내용으로 설정

        //확인 버튼(팝업 닫기)
        builder.setPositiveButton("닫기") { dialog, _ ->
            dialog.dismiss()
        }

        //선택 버튼
        builder.setNegativeButton("선택") { dialog, _ ->
            if (selectedExercises.contains(exercise)) {
                Toast.makeText(this, "${exercise.name}은(는) 이미 선택되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                selectedExercises.add(exercise)
                Toast.makeText(this, "${exercise.name}이(가) 선택 목록에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                // 선택된 운동 목록이 변경되었음을 어댑터에 알림
                selectedExerciseAdapter.updateList() //
            }
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}