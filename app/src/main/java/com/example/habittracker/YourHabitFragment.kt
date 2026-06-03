package com.example.habittracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class YourHabitFragment : Fragment() {

    private lateinit var rvHabits: RecyclerView
    private lateinit var habitAdapter: HabitDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_your_habit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(view)
        setupRecyclerView(view)
        setupDateTabs(view)
    }

    private fun setupToolbar(view: View) {
        view.findViewById<View>(R.id.iv_back)?.setOnClickListener {
            // Navigate back using popBackStack
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView(view: View) {
        rvHabits = view.findViewById(R.id.rv_habits)

        habitAdapter = HabitDetailAdapter(
            HomeFragment.todayHabits,
            onHabitChecked = { habit, isChecked ->
                habit.isCompleted = isChecked
                updateGoalProgress(habit)
            },
            onMenuClick = { habit ->
                showHabitOptionsDialog(habit)
            }
        )

        rvHabits.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = habitAdapter
        }
    }

    private fun setupDateTabs(view: View) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd\nMMM", Locale.getDefault())

        // Set current date with orange background
        view.findViewById<TextView>(R.id.tv_date_1)?.apply {
            text = dateFormat.format(calendar.time)
            setBackgroundResource(R.drawable.selected_date_background)
            setTextColor(resources.getColor(R.color.white, null))
        }

        // Set other dates
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        view.findViewById<TextView>(R.id.tv_date_2)?.apply {
            text = dateFormat.format(calendar.time)
            setBackgroundResource(R.drawable.date_tab_background)
            setTextColor(resources.getColor(R.color.black, null))
        }

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        view.findViewById<TextView>(R.id.tv_date_3)?.apply {
            text = dateFormat.format(calendar.time)
            setBackgroundResource(R.drawable.date_tab_background)
            setTextColor(resources.getColor(R.color.black, null))
        }

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        view.findViewById<TextView>(R.id.tv_date_4)?.apply {
            text = dateFormat.format(calendar.time)
            setBackgroundResource(R.drawable.date_tab_background)
            setTextColor(resources.getColor(R.color.black, null))
        }

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        view.findViewById<TextView>(R.id.tv_date_5)?.apply {
            text = dateFormat.format(calendar.time)
            setBackgroundResource(R.drawable.date_tab_background)
            setTextColor(resources.getColor(R.color.black, null))
        }
    }

    private fun updateGoalProgress(habit: TodayHabit) {
        val correspondingGoal = HomeFragment.goals.find { it.habitId == habit.habitId }
        if (correspondingGoal != null) {
            val goalIndex = HomeFragment.goals.indexOf(correspondingGoal)

            if (habit.isCompleted && correspondingGoal.current < correspondingGoal.target) {
                HomeFragment.goals[goalIndex] = correspondingGoal.copy(current = correspondingGoal.current + 1)
            } else if (!habit.isCompleted && correspondingGoal.current > 0) {
                HomeFragment.goals[goalIndex] = correspondingGoal.copy(current = correspondingGoal.current - 1)
            }
        }
    }

    private fun showHabitOptionsDialog(habit: TodayHabit) {
        val dialog = HabitOptionsDialogFragment(habit) { action, selectedHabit ->
            when (action) {
                "edit" -> showEditHabitDialog(selectedHabit)
                "delete" -> showDeleteConfirmationDialog(selectedHabit)
            }
        }
        dialog.show(parentFragmentManager, "HabitOptionsDialog")
    }

    private fun showEditHabitDialog(habit: TodayHabit) {
        val dialog = EditHabitDialogFragment(habit) { updatedGoal, updatedHabitName, period, habitType ->
            val index = HomeFragment.todayHabits.indexOf(habit)
            if (index != -1) {
                // Update habit
                val updatedHabit = habit.copy(name = updatedHabitName)
                HomeFragment.todayHabits[index] = updatedHabit
                habitAdapter.notifyItemChanged(index)

                // Update corresponding goal
                val goalIndex = HomeFragment.goals.indexOfFirst { it.habitId == habit.habitId }
                if (goalIndex != -1) {
                    val goal = HomeFragment.goals[goalIndex]
                    val updatedGoalObj = goal.copy(
                        name = updatedGoal,
                        target = getTargetFromPeriod(period),
                        frequency = habitType
                    )
                    HomeFragment.goals[goalIndex] = updatedGoalObj
                }
            }
        }
        dialog.show(parentFragmentManager, "EditHabitDialog")
    }

    private fun showDeleteConfirmationDialog(habit: TodayHabit) {
        val dialog = DeleteConfirmationDialogFragment { confirmed ->
            if (confirmed) {
                deleteHabit(habit)
            }
        }
        dialog.show(parentFragmentManager, "DeleteConfirmationDialog")
    }

    private fun deleteHabit(habit: TodayHabit) {
        val index = HomeFragment.todayHabits.indexOf(habit)
        if (index != -1) {
            // Remove from habits list
            HomeFragment.todayHabits.removeAt(index)
            habitAdapter.notifyItemRemoved(index)

            // Remove corresponding goal
            val goalIndex = HomeFragment.goals.indexOfFirst { it.habitId == habit.habitId }
            if (goalIndex != -1) {
                HomeFragment.goals.removeAt(goalIndex)
            }
        }
    }

    private fun getTargetFromPeriod(period: String): Int {
        return when (period) {
            "1 Week (7 Days)" -> 7
            "1 Month (30 Days)" -> 30
            "3 Months (90 Days)" -> 90
            else -> 30
        }
    }
}
