package com.example.habittracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class YourGoalsFragment : Fragment() {

    private lateinit var rvGoals: RecyclerView
    private lateinit var goalDetailAdapter: GoalDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_your_goals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(view)
        setupRecyclerView(view)
    }

    private fun setupToolbar(view: View) {
        view.findViewById<View>(R.id.iv_back)?.setOnClickListener {
            // Navigate back using popBackStack
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView(view: View) {
        rvGoals = view.findViewById(R.id.rv_goals)

        goalDetailAdapter = GoalDetailAdapter(
            HomeFragment.goals,
            onMenuClick = { goal ->
                showGoalOptionsDialog(goal)
            }
        )

        rvGoals.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = goalDetailAdapter
        }
    }

    private fun showGoalOptionsDialog(goal: Goal) {
        val dialog = GoalOptionsDialogFragment(goal) { action, selectedGoal ->
            when (action) {
                "edit" -> showEditGoalDialog(selectedGoal)
                "delete" -> showDeleteGoalConfirmationDialog(selectedGoal)
            }
        }
        dialog.show(parentFragmentManager, "GoalOptionsDialog")
    }

    private fun showEditGoalDialog(goal: Goal) {
        val dialog = EditGoalDialogFragment(goal) { updatedGoalName, period, habitType ->
            val index = HomeFragment.goals.indexOf(goal)
            if (index != -1) {
                // Update goal
                val updatedGoal = goal.copy(
                    name = updatedGoalName,
                    target = getTargetFromPeriod(period),
                    frequency = habitType
                )
                HomeFragment.goals[index] = updatedGoal
                goalDetailAdapter.notifyItemChanged(index)

                // Update corresponding habit
                val habitIndex = HomeFragment.todayHabits.indexOfFirst { it.habitId == goal.habitId }
                if (habitIndex != -1) {
                    // You might want to update habit name based on goal name
                    // This is optional based on your requirements
                }
            }
        }
        dialog.show(parentFragmentManager, "EditGoalDialog")
    }

    private fun showDeleteGoalConfirmationDialog(goal: Goal) {
        val dialog = DeleteConfirmationDialogFragment { confirmed ->
            if (confirmed) {
                deleteGoal(goal)
            }
        }
        dialog.show(parentFragmentManager, "DeleteGoalConfirmationDialog")
    }

    private fun deleteGoal(goal: Goal) {
        val index = HomeFragment.goals.indexOf(goal)
        if (index != -1) {
            // Remove from goals list
            HomeFragment.goals.removeAt(index)
            goalDetailAdapter.notifyItemRemoved(index)

            // Remove corresponding habit
            val habitIndex = HomeFragment.todayHabits.indexOfFirst { it.habitId == goal.habitId }
            if (habitIndex != -1) {
                HomeFragment.todayHabits.removeAt(habitIndex)
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
