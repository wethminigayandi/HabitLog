package com.example.habittracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var tvDate: TextView
    private lateinit var tvGreeting: TextView
    private lateinit var tvProgressPercentage: TextView
    private lateinit var tvHabitsCompleted: TextView
    private lateinit var rvTodayHabits: RecyclerView
    private lateinit var rvGoals: RecyclerView
    private lateinit var fabAddHabit: FloatingActionButton

    private lateinit var todayHabitAdapter: TodayHabitAdapter
    private lateinit var goalAdapter: GoalAdapter

    // Using companion object to share data between fragments
    companion object {
        val todayHabits = mutableListOf<TodayHabit>()
        val goals = mutableListOf<Goal>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerViews()
        setupClickListeners()
        loadSampleData()
        updateUI()
    }

    private fun initViews(view: View) {
        tvDate = view.findViewById(R.id.tv_date)
        tvGreeting = view.findViewById(R.id.tv_greeting)
        tvProgressPercentage = view.findViewById(R.id.tv_progress_percentage)
        tvHabitsCompleted = view.findViewById(R.id.tv_habits_completed)
        rvTodayHabits = view.findViewById(R.id.rv_today_habits)
        rvGoals = view.findViewById(R.id.rv_goals)
        fabAddHabit = view.findViewById(R.id.fab_add_habit)
    }

    private fun setupRecyclerViews() {
        // Today Habits RecyclerView with completion tracking
        todayHabitAdapter = TodayHabitAdapter(todayHabits) { habit, isChecked ->
            habit.isCompleted = isChecked
            updateCorrespondingGoalProgress(habit.name, isChecked)
            updateProgress()
        }
        rvTodayHabits.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todayHabitAdapter
        }

        // Goals RecyclerView
        goalAdapter = GoalAdapter(goals) { goal ->
            // Handle goal item click
        }
        rvGoals.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = goalAdapter
        }
    }

    private fun setupClickListeners() {
        fabAddHabit.setOnClickListener {
            showCreateHabitDialog()
        }

        view?.findViewById<TextView>(R.id.tv_see_all_today)?.setOnClickListener {
            // Navigate to Your Habit page using Fragment Transaction
            navigateToFragment(YourHabitFragment())
        }

        view?.findViewById<TextView>(R.id.tv_see_all_goals)?.setOnClickListener {
            // Navigate to Your Goals page using Fragment Transaction
            navigateToFragment(YourGoalsFragment())
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment) // ✅ Fixed: Use fragmentContainer instead of nav_host_fragment
            .addToBackStack(null)
            .commit()
    }

    private fun loadSampleData() {
        if (todayHabits.isEmpty()) {
            todayHabits.addAll(
                listOf(
                    TodayHabit("Meditating", true, R.color.green, "habit_1", Date()),
                    TodayHabit("Read Philosophy", true, R.color.green, "habit_2", Date()),
                    TodayHabit("Journaling", false, R.color.gray, "habit_3", Date())
                )
            )
        }

        if (goals.isEmpty()) {
            goals.addAll(
                listOf(
                    Goal("Finish 5 Philosophy Books", 5, 7, "Everyday", "habit_2", Date()),
                    Goal("Sleep before 11 pm", 5, 7, "Everyday", "habit_sleep", Date()),
                    Goal("Finish read The Hobbits", 0, 1, "Once", "habit_hobbits", Date())
                )
            )
        }

        todayHabitAdapter.notifyDataSetChanged()
        goalAdapter.notifyDataSetChanged()
    }

    private fun updateUI() {
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        tvDate.text = dateFormat.format(Date())
        tvGreeting.text = "Hello, Wethmini!"
        updateProgress()
    }

    private fun updateProgress() {
        val completedHabits = todayHabits.count { it.isCompleted }
        val totalHabits = todayHabits.size
        val percentage = if (totalHabits > 0) (completedHabits * 100) / totalHabits else 0

        tvProgressPercentage.text = "$percentage%"
        tvHabitsCompleted.text = "$completedHabits of $totalHabits habits\ncompleted today!"
    }

    private fun showCreateHabitDialog() {
        val dialog = CreateHabitDialogFragment { goal, habitName, period, habitType ->
            val habitId = "habit_${System.currentTimeMillis()}"
            val currentDate = Date()

            val newTodayHabit = TodayHabit(habitName, false, R.color.gray, habitId, currentDate)
            todayHabits.add(newTodayHabit)
            todayHabitAdapter.notifyItemInserted(todayHabits.size - 1)

            val newGoal = Goal(goal, 0, getTargetFromPeriod(period), habitType, habitId, currentDate)
            goals.add(newGoal)
            goalAdapter.notifyItemInserted(goals.size - 1)

            updateProgress()
            showHabitAddedDialog()
        }
        dialog.show(parentFragmentManager, "CreateHabitDialog")
    }

    private fun showHabitAddedDialog() {
        val dialog = HabitAddedDialogFragment()
        dialog.show(parentFragmentManager, "HabitAddedDialog")
    }

    private fun updateCorrespondingGoalProgress(habitName: String, isCompleted: Boolean) {
        val todayHabit = todayHabits.find { it.name == habitName }
        if (todayHabit != null) {
            val correspondingGoal = goals.find { it.habitId == todayHabit.habitId }
            if (correspondingGoal != null) {
                val goalIndex = goals.indexOf(correspondingGoal)

                if (isCompleted && correspondingGoal.current < correspondingGoal.target) {
                    goals[goalIndex] = correspondingGoal.copy(current = correspondingGoal.current + 1)
                } else if (!isCompleted && correspondingGoal.current > 0) {
                    goals[goalIndex] = correspondingGoal.copy(current = correspondingGoal.current - 1)
                }

                goalAdapter.notifyItemChanged(goalIndex)
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

// Enhanced Data classes with dates
data class TodayHabit(
    val name: String,
    var isCompleted: Boolean,
    val colorRes: Int,
    val habitId: String,
    val dateCreated: Date
)

data class Goal(
    val name: String,
    val current: Int,
    val target: Int,
    val frequency: String,
    val habitId: String,
    val dateCreated: Date
)
