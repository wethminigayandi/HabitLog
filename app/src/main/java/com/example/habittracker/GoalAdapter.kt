package com.example.habittracker

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GoalAdapter(
    private val goals: MutableList<Goal>,
    private val onGoalClick: (Goal) -> Unit
) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val goalName: TextView = view.findViewById(R.id.tv_goal_name)
        val goalProgress: ProgressBar = view.findViewById(R.id.pb_goal_progress)
        val goalTarget: TextView = view.findViewById(R.id.tv_goal_target)
        val goalFrequency: TextView = view.findViewById(R.id.tv_goal_frequency)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]

        holder.goalName.text = goal.name
        holder.goalTarget.text = "${goal.current} from ${goal.target} days target"
        holder.goalFrequency.text = goal.frequency

        // Enhanced progress calculation and animation
        val progress = if (goal.target > 0) (goal.current * 100) / goal.target else 0

        // Animate progress bar changes
        animateProgress(holder.goalProgress, progress)

        holder.itemView.setOnClickListener {
            onGoalClick(goal)
        }
    }

    private fun animateProgress(progressBar: ProgressBar, newProgress: Int) {
        val currentProgress = progressBar.progress
        val animator = ObjectAnimator.ofInt(progressBar, "progress", currentProgress, newProgress)
        animator.duration = 500 // Animation duration in milliseconds
        animator.start()
    }

    override fun getItemCount() = goals.size

    // Method to add new goal with animation
    fun addGoal(goal: Goal) {
        goals.add(goal)
        notifyItemInserted(goals.size - 1)
    }

    // Method to update specific goal
    fun updateGoal(position: Int, updatedGoal: Goal) {
        if (position >= 0 && position < goals.size) {
            goals[position] = updatedGoal
            notifyItemChanged(position)
        }
    }
}
