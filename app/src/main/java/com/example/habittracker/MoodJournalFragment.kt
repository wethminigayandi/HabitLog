package com.example.habittracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MoodJournalFragment : Fragment() {

    private lateinit var moodButtons: List<ImageView>
    private lateinit var etNotes: EditText
    private lateinit var btnSave: Button
    private lateinit var rvMoodEntries: RecyclerView
    private lateinit var calendarView: GridView
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var tvMonthYear: TextView
    private lateinit var btnPrevMonth: ImageView
    private lateinit var btnNextMonth: ImageView

    private lateinit var moodEntriesAdapter: MoodEntriesAdapter
    private lateinit var calendarAdapter: CalendarAdapter

    private var selectedMood: MoodType? = null
    private val moodEntries = mutableListOf<MoodEntry>()
    private val calendar = Calendar.getInstance()
    private val currentMonth = Calendar.getInstance()

    // Mood types with emojis
    enum class MoodType(val emoji: String, val description: String, val colorRes: Int) {
        HAPPY("😊", "Happy", R.color.green),
        SAD("😢", "Sad", R.color.blue),
        NEUTRAL("😐", "Neutral", R.color.gray),
        ANGRY("😠", "Angry", R.color.red)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupMoodSelection()
        setupCalendar()
        setupRecyclerView()
        setupSaveButton()
        loadSampleData()
    }

    private fun initViews(view: View) {
        moodButtons = listOf(
            view.findViewById(R.id.iv_mood_happy),
            view.findViewById(R.id.iv_mood_sad),
            view.findViewById(R.id.iv_mood_neutral),
            view.findViewById(R.id.iv_mood_angry)
        )

        etNotes = view.findViewById(R.id.et_notes)
        btnSave = view.findViewById(R.id.btn_save)
        rvMoodEntries = view.findViewById(R.id.rv_mood_entries)
        calendarView = view.findViewById(R.id.calendar_grid)
        tvStartDate = view.findViewById(R.id.tv_start_date)
        tvEndDate = view.findViewById(R.id.tv_end_date)
        tvMonthYear = view.findViewById(R.id.tv_month_year)
        btnPrevMonth = view.findViewById(R.id.btn_prev_month)
        btnNextMonth = view.findViewById(R.id.btn_next_month)
    }

    private fun setupMoodSelection() {
        val moods = MoodType.values()

        moodButtons.forEachIndexed { index, imageView ->
            if (index < moods.size) {
                val mood = moods[index]
                imageView.setOnClickListener {
                    selectMood(mood, imageView)
                }
            }
        }
    }

    private fun selectMood(mood: MoodType, selectedButton: ImageView) {
        selectedMood = mood

        // Reset all mood buttons
        moodButtons.forEach { button ->
            button.setBackgroundResource(R.drawable.mood_button_unselected)
        }

        // Highlight selected mood
        selectedButton.setBackgroundResource(R.drawable.mood_button_selected)
    }

    private fun setupCalendar() {
        currentMonth.time = Date()

        btnPrevMonth.setOnClickListener {
            currentMonth.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        btnNextMonth.setOnClickListener {
            currentMonth.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        updateCalendar()
    }

    private fun updateCalendar() {
        // Update month/year display
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        tvMonthYear.text = monthYearFormat.format(currentMonth.time)

        // Generate calendar dates
        val calendarDates = generateCalendarDates()

        // Update start and end dates
        if (calendarDates.isNotEmpty()) {
            val dateFormat = SimpleDateFormat("MMM d yyyy", Locale.getDefault())
            tvStartDate.text = dateFormat.format(calendarDates.first().date)
            tvEndDate.text = dateFormat.format(calendarDates.last().date)
        }

        // Setup calendar adapter
        calendarAdapter = CalendarAdapter(calendarDates, moodEntries)
        calendarView.adapter = calendarAdapter
    }

    private fun generateCalendarDates(): List<CalendarDate> {
        val dates = mutableListOf<CalendarDate>()
        val cal = Calendar.getInstance()
        cal.time = currentMonth.time

        // Set to first day of month
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

        // Add previous month days to fill the grid
        val prevMonthCal = Calendar.getInstance()
        prevMonthCal.time = cal.time
        prevMonthCal.add(Calendar.MONTH, -1)
        prevMonthCal.set(Calendar.DAY_OF_MONTH, prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH))

        for (i in firstDayOfWeek - 2 downTo 0) {
            val date = Calendar.getInstance()
            date.time = prevMonthCal.time
            date.add(Calendar.DAY_OF_MONTH, -i)
            dates.add(CalendarDate(date.time, false))
        }

        // Add current month days
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..daysInMonth) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            dates.add(CalendarDate(cal.time, true))
        }

        // Add next month days to fill remaining slots
        val nextMonthCal = Calendar.getInstance()
        nextMonthCal.time = currentMonth.time
        nextMonthCal.add(Calendar.MONTH, 1)
        nextMonthCal.set(Calendar.DAY_OF_MONTH, 1)

        val remainingSlots = 42 - dates.size // 6 rows × 7 days
        for (day in 0 until remainingSlots) {
            val date = Calendar.getInstance()
            date.time = nextMonthCal.time
            date.add(Calendar.DAY_OF_MONTH, day)
            dates.add(CalendarDate(date.time, false))
        }

        return dates
    }

    private fun setupRecyclerView() {
        moodEntriesAdapter = MoodEntriesAdapter(moodEntries)
        rvMoodEntries.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moodEntriesAdapter
        }
    }

    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            saveMoodEntry()
        }
    }

    private fun saveMoodEntry() {
        val mood = selectedMood
        val note = etNotes.text.toString().trim()

        if (mood == null) {
            Toast.makeText(context, "Please select a mood", Toast.LENGTH_SHORT).show()
            return
        }

        val currentTime = Date()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val moodEntry = MoodEntry(
            mood = mood,
            note = if (note.isEmpty()) "No notes" else note,
            time = timeFormat.format(currentTime),
            date = dateFormat.format(currentTime),
            fullDate = currentTime
        )

        // Add to the beginning of the list (most recent first)
        moodEntries.add(0, moodEntry)
        moodEntriesAdapter.notifyItemInserted(0)

        // Update calendar to show new mood
        updateCalendar()

        // Reset form
        resetForm()

        Toast.makeText(context, "Mood saved successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun resetForm() {
        selectedMood = null
        etNotes.setText("")

        // Reset mood button selection
        moodButtons.forEach { button ->
            button.setBackgroundResource(R.drawable.mood_button_unselected)
        }
    }

    private fun loadSampleData() {
        // Add some sample mood entries for demonstration
        val sampleEntries = listOf(
            MoodEntry(MoodType.HAPPY, "Had a great breakfast", "11:41", "17/09/2025", getDateDaysAgo(7)),
            MoodEntry(MoodType.NEUTRAL, "Normal Morning", "08:32", "16/09/2025", getDateDaysAgo(8)),
            MoodEntry(MoodType.ANGRY, "Felt tired after work", "17:33", "15/09/2025", getDateDaysAgo(9))
        )

        moodEntries.addAll(sampleEntries)
        moodEntriesAdapter.notifyDataSetChanged()
        updateCalendar()
    }

    private fun getDateDaysAgo(daysAgo: Int): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return cal.time
    }
}

// Data classes
data class MoodEntry(
    val mood: MoodJournalFragment.MoodType,
    val note: String,
    val time: String,
    val date: String,
    val fullDate: Date
)

data class CalendarDate(
    val date: Date,
    val isCurrentMonth: Boolean
)
