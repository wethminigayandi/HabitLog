package com.example.habittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GoalDetailAdapter(
    private val goals: MutableList<Goal>,
    private val onMenuClick: (Goal) -> Unit
) : RecyclerView.Adapter<GoalDetailAdapter.GoalDetailViewHolder>() {

    class GoalDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val goalName: TextView = view.findViewById(R.id.tv_goal_name)
        val goalProgress: ProgressBar = view.findViewById(R.id.pb_goal_progress)
        val goalTarget: TextView = view.findViewById(R.id.tv_goal_target)
        val goalFrequency: TextView = view.findViewById(R.id.tv_goal_frequency)
        val menuButton: View = view.findViewById(R.id.btn_menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal_detail, parent, false)
        return GoalDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalDetailViewHolder, position: Int) {
        val goal = goals[position]

        holder.goalName.text = goal.name
        holder.goalTarget.text = "${goal.current} from ${goal.target} days target"
        holder.goalFrequency.text = goal.frequency

        // Set progress
        val progress = if (goal.target > 0) (goal.current * 100) / goal.target else 0
        holder.goalProgress.progress = progress

        // Menu button click
        holder.menuButton.setOnClickListener {
            onMenuClick(goal)
        }
    }

    override fun getItemCount() = goals.size
}
