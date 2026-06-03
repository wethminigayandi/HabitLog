package com.example.habittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TodayHabitAdapter(
    private val habits: MutableList<TodayHabit>,
    private val onHabitChecked: (TodayHabit, Boolean) -> Unit
) : RecyclerView.Adapter<TodayHabitAdapter.TodayHabitViewHolder>() {

    class TodayHabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val habitName: TextView = view.findViewById(R.id.tv_habit_name)
        val habitCheckbox: CheckBox = view.findViewById(R.id.cb_habit_completed)
        val habitContainer: View = view.findViewById(R.id.habit_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayHabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_today_habit, parent, false)
        return TodayHabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodayHabitViewHolder, position: Int) {
        val habit = habits[position]

        holder.habitName.text = habit.name
        holder.habitCheckbox.isChecked = habit.isCompleted

        // Enhanced visual feedback based on completion status
        updateHabitAppearance(holder, habit.isCompleted)

        // Clear previous listener to avoid issues
        holder.habitCheckbox.setOnCheckedChangeListener(null)

        // Set new listener
        holder.habitCheckbox.setOnCheckedChangeListener { _, isChecked ->
            habit.isCompleted = isChecked
            updateHabitAppearance(holder, isChecked)
            onHabitChecked(habit, isChecked)
        }
    }

    private fun updateHabitAppearance(holder: TodayHabitViewHolder, isCompleted: Boolean) {
        if (isCompleted) {
            // Completed habit appearance
            holder.habitContainer.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.light_green)
            )
            holder.habitName.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.green)
            )
        } else {
            // Incomplete habit appearance
            holder.habitContainer.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.light_gray)
            )
            holder.habitName.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.black)
            )
        }
    }

    override fun getItemCount() = habits.size

    // Method to add new habit with animation
    fun addHabit(habit: TodayHabit) {
        habits.add(habit)
        notifyItemInserted(habits.size - 1)
    }
}
