package com.example.swu_guru2_android_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
// import com.example.swu_guru2_android_app.R
// import com.example.swu_guru2_android_app.GroupedScheduleItem
// import com.example.swu_guru2_android_app.ScheduleItem
// import com.example.swu_guru2_android_app.DBManager

class ScheduleAdapter(
    private var scheduleList: MutableList<GroupedScheduleItem>,
    private val dbManager: DBManager,
    private val onSetSelected: (List<ScheduleItem>) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayText: TextView = itemView.findViewById(R.id.dayText)
        val exerciseContainer: LinearLayout = itemView.findViewById(R.id.exerciseContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_grouped_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val groupedItem = scheduleList[position]
        holder.dayText.text = groupedItem.day
        holder.exerciseContainer.removeAllViews()

        // 각 세트별로 UI 요소 생성 및 리스너 설정
        groupedItem.sets.forEachIndexed { setIndex, exerciseSet ->
            // 세트 제목 (예: "세트 1")
            val setTitle = TextView(holder.itemView.context).apply {
                text = "세트 ${setIndex + 1}"
                textSize = 16f
                setPadding(0, 16, 0, 8)
            }
            holder.exerciseContainer.addView(setTitle)

            val buttonLayout = LinearLayout(holder.itemView.context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // 세트 시작 버튼 (각 세트별로 생성)
            val startSetButton = Button(holder.itemView.context).apply {
                text = "운동 선택"
                layoutParams = LinearLayout.LayoutParams(
                    0, // width를 0으로 설정하고 weight 부여
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1f // 왼쪽 공간을 차지하도록
                    setMargins(0, 8, 8, 16) // 마진 조정 (오른쪽 마진 추가)
                }
            }
            // 세트 시작 버튼 클릭 리스너
            startSetButton.setOnClickListener {
                onSetSelected(exerciseSet) // 해당 세트의 운동 목록을 콜백으로 전달
            }
            buttonLayout.addView(startSetButton)

            // "세트 삭제" 버튼 (각 세트별로)
            val deleteSetButton = Button(holder.itemView.context).apply {
                text = "세트 삭제" // "삭제"에서 "세트 삭제"로 텍스트 변경
                layoutParams = LinearLayout.LayoutParams(
                    0, // width를 0으로 설정하고 weight 부여
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1f // 오른쪽 공간을 차지하도록
                    setMargins(8, 8, 0, 16) // 마진 조정 (왼쪽 마진 추가)
                }
            }
            deleteSetButton.setOnClickListener {
                // 세트 삭제 로직
                // 1. DB에서 해당 세트의 모든 운동 삭제
                //    DBManager에 `deleteSetByDayAndSetIndex` 같은 메서드가 필요할 수 있음
                //    지금은 각 운동을 개별 삭제하는 방식으로 임시 처리하거나, DBManager에 일괄 삭제 메서드 추가 고려
                exerciseSet.forEach { item ->
                    dbManager.deleteSchedule(item.day, item.exerciseName, item.setIndex)
                }

                // 2. 해당 세트를 리스트에서 제거
                groupedItem.sets.removeAt(setIndex)

                // 3. 해당 요일의 모든 세트가 비었으면 요일 항목 자체를 제거
                if (groupedItem.sets.isEmpty()) {
                    scheduleList.removeAt(position)
                    notifyItemRemoved(position)
                } else {
                    notifyItemChanged(position) // 세트가 변경되었음을 알림
                }
            }
            buttonLayout.addView(deleteSetButton) // 버튼 레이아웃에 추가

            holder.exerciseContainer.addView(buttonLayout) // exerciseContainer에 버튼 레이아웃 추가

            // 해당 세트의 각 운동 항목 추가
            exerciseSet.forEach { item ->
                val itemView = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.item_exercise_with_delete, holder.exerciseContainer, false)

                val nameText = itemView.findViewById<TextView>(R.id.tv_exercise_name)
                val durationText = itemView.findViewById<TextView>(R.id.tv_duration)
                val caloriesText = itemView.findViewById<TextView>(R.id.tv_calories)
                val deleteButton = itemView.findViewById<Button>(R.id.btn_delete_exercise) // 운동 개별 삭제 버튼

                nameText.text = item.exerciseName
                durationText.text = item.duration
                caloriesText.text = item.calories

                deleteButton.setOnClickListener {
                    // 개별 운동 삭제 로직
                    dbManager.deleteSchedule(item.day, item.exerciseName, item.setIndex)
                    groupedItem.sets[setIndex].remove(item)

                    if (groupedItem.sets[setIndex].isEmpty()) {
                        // 세트 내 마지막 운동 삭제로 세트가 비었을 때
                        groupedItem.sets.removeAt(setIndex)
                        if (groupedItem.sets.isEmpty()) {
                            // 요일의 모든 세트가 비었을 때
                            scheduleList.removeAt(position)
                            notifyItemRemoved(position)
                        } else {
                            notifyItemChanged(position)
                        }
                    } else {
                        // 세트 내 운동만 변경되었을 때
                        notifyItemChanged(position)
                    }
                }
                holder.exerciseContainer.addView(itemView)
            }
        }
    }

    override fun getItemCount(): Int = scheduleList.size

    fun updateData(newList: List<GroupedScheduleItem>) {
        scheduleList = newList.toMutableList()
        notifyDataSetChanged() // 데이터가 완전히 변경되었을 때 전체 갱신
    }
}