package com.example.habittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class MoodEntriesAdapter(
    private val moodEntries: List<MoodEntry>
) : RecyclerView.Adapter<MoodEntriesAdapter.MoodEntryViewHolder>() {

    class MoodEntryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val moodEmoji: TextView = view.findViewById(R.id.tv_mood_emoji)
        val moodDescription: TextView = view.findViewById(R.id.tv_mood_description)
        val moodDate: TextView = view.findViewById(R.id.tv_mood_date)
        val moodTime: TextView = view.findViewById(R.id.tv_mood_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood_entry, parent, false)
        return MoodEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodEntryViewHolder, position: Int) {
        val moodEntry = moodEntries[position]

        holder.moodEmoji.text = moodEntry.mood.emoji
        holder.moodDescription.text = moodEntry.note
        holder.moodDate.text = moodEntry.date
        holder.moodTime.text = moodEntry.time

        // Set mood color
        holder.moodEmoji.setTextColor(
            ContextCompat.getColor(holder.itemView.context, moodEntry.mood.colorRes)
        )
    }

    override fun getItemCount() = moodEntries.size
}
