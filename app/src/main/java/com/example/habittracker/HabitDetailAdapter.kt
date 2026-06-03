package com.example.habittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class HabitDetailAdapter(
    private val habits: MutableList<TodayHabit>,
    private val onHabitChecked: (TodayHabit, Boolean) -> Unit,
    private val onMenuClick: (TodayHabit) -> Unit
) : RecyclerView.Adapter<HabitDetailAdapter.HabitDetailViewHolder>() {

    class HabitDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val habitName: TextView = view.findViewById(R.id.tv_habit_name)
        val habitCheckbox: CheckBox = view.findViewById(R.id.cb_habit_completed)
        val habitContainer: View = view.findViewById(R.id.habit_container)
        val menuButton: View = view.findViewById(R.id.btn_menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit_detail, parent, false)
        return HabitDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitDetailViewHolder, position: Int) {
        val habit = habits[position]

        holder.habitName.text = habit.name
        holder.habitCheckbox.isChecked = habit.isCompleted

        // Update visual appearance based on completion
        updateHabitAppearance(holder, habit.isCompleted)

        // Clear previous listener
        holder.habitCheckbox.setOnCheckedChangeListener(null)

        // Set new listener
        holder.habitCheckbox.setOnCheckedChangeListener { _, isChecked ->
            habit.isCompleted = isChecked
            updateHabitAppearance(holder, isChecked)
            onHabitChecked(habit, isChecked)
        }

        // Menu button click
        holder.menuButton.setOnClickListener {
            onMenuClick(habit)
        }
    }

    private fun updateHabitAppearance(holder: HabitDetailViewHolder, isCompleted: Boolean) {
        if (isCompleted) {
            holder.habitContainer.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.light_green)
            )
            holder.habitName.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.green)
            )
        } else {
            holder.habitContainer.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.light_gray)
            )
            holder.habitName.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.black)
            )
        }
    }

    override fun getItemCount() = habits.size
}
