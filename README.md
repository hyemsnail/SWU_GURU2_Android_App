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

```plaintext
app/
├─ manifests/AndroidManifest.xml
├─ java/com/example/swu_guru2_android_app/
│   ├─ SplashActivity.kt
│   ├─ MainActivity.kt
│   ├─ SelectExerciseActivity.kt
│   ├─ ViewScheduleActivity.kt
│   ├─ ScheduleAdapter.kt
│   └─ (그 외 DBManager, ExerciseAdapter 등)
└─ res/
   ├─ layout/
   │   ├─ activity_splash.xml
   │   ├─ activity_main.xml
   │   ├─ activity_select_exercise.xml
   │   ├─ activity_view_schedule.xml
   │   ├─ activity_item_grouped_schedule.xml
   │   └─ item_exercise_with_delete.xml
   ├─ drawable/
   ├─ values/
   │   ├─ colors.xml
   │   ├─ strings.xml
   │   └─ themes.xml
   └─ xml/
       ├─ backup_rules.xml
       └─ data_extraction_rules.xml
```
---

## 5. 개발 환경

- **개발 언어**: Kotlin  
- **IDE / 빌드 툴**: Android Studio Arctic Fox 이상 / Gradle  
- **버전 관리**: Git & GitHub  
- **라이브러리**:  
  - AndroidX Core, AppCompat, Material Design  
  - RecyclerView, ConstraintLayout  
  - Kotlin Coroutines  
- **데이터 저장 방식**:  
  - 로컬 SQLite 데이터베이스 (Room 사용 예정)  
  - SharedPreferences (간단 설정 저장용)

---

## 6. 발전 방향성

1. **정확한 운동 방법 안내 기능 강화**  
   - 운동별 동작 설명 텍스트 및 애니메이션 가이드 추가  
   - 전문가 촬영 가이드 영상 삽입 → 잘못된 자세로 인한 부상 위험 감소

2. **데이터 기반 맞춤형 루틴 추천**  
   - 사용자의 운동 이력(선택 운동, 실행 빈도) 분석  
   - 머신러닝 기반 추천 알고리즘 도입 → 개인 성향에 최적화된 루틴 제안

3. **운동 성과 시각화**  
   - 주간/월간 운동 시간, 칼로리 소모 통계 차트 제공  
   - 달성률 배지 & 레벨 시스템 도입으로 동기 부여

4. **커뮤니티 & 소셜 기능**  
   - 친구 초대 및 운동 공유 기능  
   - 챌린지 모드(랭킹, 그룹 운동)로 사용자 참여 유도

5. **푸시 알림 & 위젯 지원**  
   - 운동 알림 설정 (시간, 요일별 반복)  
   - 홈 화면 위젯으로 당일 운동 루틴 바로보기
