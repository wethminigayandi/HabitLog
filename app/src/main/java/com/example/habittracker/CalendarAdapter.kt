package com.example.habittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private val calendarDates: List<CalendarDate>,
    private val moodEntries: List<MoodEntry>
) : BaseAdapter() {

    override fun getCount(): Int = calendarDates.size

    override fun getItem(position: Int): CalendarDate = calendarDates[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)

        val calendarDate = calendarDates[position]
        val tvDay = view.findViewById<TextView>(R.id.tv_day)
        val tvMoodEmoji = view.findViewById<TextView>(R.id.tv_mood_emoji)

        val calendar = Calendar.getInstance()
        calendar.time = calendarDate.date
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Find mood for this date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dayDateString = dateFormat.format(calendarDate.date)
        val moodForDay = moodEntries.find { it.date == dayDateString }

        if (moodForDay != null) {
            // Show emoji instead of date number
            tvDay.visibility = View.GONE
            tvMoodEmoji.visibility = View.VISIBLE
            tvMoodEmoji.text = moodForDay.mood.emoji
        } else {
            // Show date number
            tvDay.visibility = View.VISIBLE
            tvMoodEmoji.visibility = View.GONE
            tvDay.text = dayOfMonth.toString()
        }

        // Style based on current month
        if (calendarDate.isCurrentMonth) {
            tvDay.setTextColor(ContextCompat.getColor(parent.context, R.color.black))
        } else {
            tvDay.setTextColor(ContextCompat.getColor(parent.context, R.color.gray))
        }

        // Highlight today
        val today = Calendar.getInstance()
        if (isSameDay(calendarDate.date, today.time)) {
            view.setBackgroundResource(R.drawable.calendar_today_background)
        } else {
            view.setBackgroundResource(R.drawable.calendar_day_background)
        }

        return view
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
