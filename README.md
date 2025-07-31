# 🏃‍♀️ FITFIT

간단하고 직관적인 홈트레이닝 루틴 관리 앱 “FITFIT”의 Android 프로젝트입니다.  
사용자가 스스로 운동 루틴을 계획하고, 매일매일 꾸준히 운동 습관을 만들어 갈 수 있도록 돕습니다.

---

## 주요 기능

1. **시작 스플래시** (`SplashActivity`)  
   - FitFit 로고·앱명 중앙 배치  
   - 2초 후 자동으로 홈 화면으로 이동

2. **홈 화면** (`MainActivity`)  
   - 로고, 버튼(운동 계획하기 / 계획표 보기 / 운동 시작)  
   - 각 버튼 클릭 시 해당 페이지로 네비게이션

3. **운동 선택 화면** (`SelectExerciseActivity`)  
   - **요일 선택**: 월~일 체크박스, 다중 선택 가능  
   - **운동 검색**: 운동명(한글·영어)으로 리스트 필터링  
   - **카테고리 버튼**: 상체·하체·복근·전신 버튼  
   - **운동 선택**: 팝업 → “[운동명]이(가) 선택 목록에 추가되었습니다.” 토스트  
   - **운동 세트 저장**: “운동 세트가 저장되었습니다.” 토스트

4. **계획표 보기 화면** (`ViewScheduleActivity`)  
   - 선택한 **요일 순서대로** 세트(1,2,3…) 출력  
   - `activity_item_grouped_schedule.xml`을 통해 요일별 그룹화  
   - RecyclerView + ScrollView로 스크롤 가능

5. **운동 세트 조작** (`ScheduleAdapter.kt`)  
   - 각 세트 옆에 “세트 선택”, “세트 삭제” 버튼을 **코드로 동적 생성**  
   - **세트 선택** 클릭 → “선택된 세트: {요일} {N}번째 세트가 선택되었습니다.” 토스트  
   - **세트 삭제** 클릭 → 즉시 해당 세트 제거

---

## 디렉토리 구조

app/
├─ manifests/AndroidManifest.xml
├─ java/com/example/swu_guru2_android_app/
│ ├─ SplashActivity.kt
│ ├─ MainActivity.kt
│ ├─ SelectExerciseActivity.kt
│ ├─ ViewScheduleActivity.kt
│ ├─ ScheduleAdapter.kt
│ └─ (그 외 DBManager, ExerciseAdapter 등)
└─ res/
├─ layout/
│ ├─ activity_splash.xml
│ ├─ activity_main.xml
│ ├─ activity_select_exercise.xml
│ ├─ activity_view_schedule.xml
│ ├─ activity_item_grouped_schedule.xml
│ └─ item_exercise_with_delete.xml
├─ drawable/…
├─ values/colors.xml, strings.xml, themes.xml
└─ xml/backup_rules.xml, data_extraction_rules.xml

## 기술 스택
  - Kotlin
  - AndroidX
  - MVVM 패턴
