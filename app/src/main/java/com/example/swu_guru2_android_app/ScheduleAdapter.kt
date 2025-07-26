package com.example.swu_guru2_android_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swu_guru2_android_app.R
import com.example.swu_guru2_android_app.GroupedScheduleItem
import com.example.swu_guru2_android_app.ScheduleItem
import com.example.swu_guru2_android_app.DBManager

class ScheduleAdapter(
    private var scheduleList:  MutableList<GroupedScheduleItem>,
    private val dbManager: DBManager) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

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

        // 여러 세트 반복
        groupedItem.sets.forEachIndexed { setIndex, exerciseSet ->
            val setTitle = TextView(holder.itemView.context)
            setTitle.text = "세트 ${setIndex + 1}"
            setTitle.textSize = 16f
            setTitle.setPadding(0, 16, 0, 8)
            holder.exerciseContainer.addView(setTitle)

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
                    // 삭제 로직 구현 필요 (setIndex, item 기준)
                    // 1. DB에서 삭제
                    dbManager.deleteSchedule(item.day, item.exerciseName, item.setIndex)

                    // 2. 해당 세트에서 운동 삭제
                    groupedItem.sets[setIndex].remove(item)

                    // 3. 세트가 비었으면 해당 세트 제거
                    if (groupedItem.sets[setIndex].isEmpty()) {
                        groupedItem.sets.removeAt(setIndex)
                    }

                    // 4. 해당 요일의 모든 세트가 비었으면 전체 항목 제거
                    if (groupedItem.sets.isEmpty()) {
                        scheduleList.removeAt(position)
                        notifyItemRemoved(position)
                    } else {
                        notifyItemChanged(position)
                    }
                }

                holder.exerciseContainer.addView(itemView)
            }
        }

        // 요일 전체 삭제 버튼
        val deleteDayButton = holder.itemView.findViewById<Button>(R.id.deleteButton)
        deleteDayButton.setOnClickListener {
            dbManager.deleteScheduleByDay(groupedItem.day)
            scheduleList.removeAt(position)
            notifyItemRemoved(position)
        }
    }





    override fun getItemCount(): Int = scheduleList.size

    fun updateData(newList: List<GroupedScheduleItem>) {
        scheduleList = newList.toMutableList()
        notifyDataSetChanged()
    }
}
