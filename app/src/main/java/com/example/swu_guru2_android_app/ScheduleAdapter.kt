package com.example.swu_guru2_android_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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

        groupedItem.sets.forEachIndexed { setIndex, exerciseSet ->
            // 세트 제목 (예: "세트 1")
            val setTitle = TextView(holder.itemView.context).apply {
                text = "세트 ${setIndex + 1}"
                textSize = 16f
                setPadding(0, 16, 0, 8)
            }
            holder.exerciseContainer.addView(setTitle)

            // 버튼들을 담을 수평 LinearLayout 추가
            val buttonLayout = LinearLayout(holder.itemView.context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // "이 세트 운동 시작" 버튼
            val startSetButton = Button(holder.itemView.context).apply {
                text = "이 세트 운동 선택" // ★ 텍스트를 "이 세트 운동 선택"으로 변경
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1f
                    setMargins(0, 8, 8, 16)
                }
            }
            // 클릭 시 onSetSelected 람다를 호출하여 ViewScheduleActivity에 선택 정보 전달
            startSetButton.setOnClickListener {
                onSetSelected(exerciseSet)
            }
            buttonLayout.addView(startSetButton)

            // "세트 삭제" 버튼
            val deleteSetButton = Button(holder.itemView.context).apply {
                text = "세트 삭제"
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1f
                    setMargins(8, 8, 0, 16)
                }
            }
            deleteSetButton.setOnClickListener {
                // 1. DB에서 해당 세트의 모든 운동 삭제 (새 메서드 사용)
                dbManager.deleteScheduleByDayAndSetIndex(groupedItem.day, setIndex)

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
            buttonLayout.addView(deleteSetButton)

            holder.exerciseContainer.addView(buttonLayout)

            // 해당 세트의 각 운동 항목 추가
            exerciseSet.forEach { item ->
                val itemView = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.item_exercise_with_delete, holder.exerciseContainer, false)

                val nameText = itemView.findViewById<TextView>(R.id.tv_exercise_name)
                val durationText = itemView.findViewById<TextView>(R.id.tv_duration)
                val caloriesText = itemView.findViewById<TextView>(R.id.tv_calories)
                val deleteButton = itemView.findViewById<Button>(R.id.btn_delete_exercise)

                nameText.text = item.exerciseName
                durationText.text = item.duration
                caloriesText.text = item.calories

                deleteButton.setOnClickListener {
                    dbManager.deleteSchedule(item.day, item.exerciseName, item.setIndex)
                    groupedItem.sets[setIndex].remove(item)

                    if (groupedItem.sets[setIndex].isEmpty()) {
                        groupedItem.sets.removeAt(setIndex)
                        if (groupedItem.sets.isEmpty()) {
                            scheduleList.removeAt(position)
                            notifyItemRemoved(position)
                        } else {
                            notifyItemChanged(position)
                        }
                    } else {
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
        notifyDataSetChanged()
    }
}