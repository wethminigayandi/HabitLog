package com.example.habittracker

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HydrationFragment : Fragment() {

    private lateinit var progressDaily: LinearProgressIndicator
    private lateinit var tvGoalValue: TextView
    private lateinit var tvEmptyState: TextView
    private lateinit var rvIntake: RecyclerView
    private lateinit var btnAdd250: MaterialButton
    private lateinit var btnAdd500: MaterialButton
    private lateinit var btnReminder30: MaterialButton
    private lateinit var btnReminder60: MaterialButton
    private lateinit var btnReminder120: MaterialButton
    private lateinit var btnReminderOff: MaterialButton

    private lateinit var intakeAdapter: IntakeAdapter
    private val intakeEntries = mutableListOf<IntakeEntry>()

    private val dailyGoalMl = 3000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hydration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupRecyclerView()
        setupButtons()
        renderProgress()
        updateEmptyState()
        updateReminderButtonState(currentReminderInterval())
    }

    private fun initViews(view: View) {
        progressDaily = view.findViewById(R.id.progress_daily)
        tvGoalValue = view.findViewById(R.id.tv_goal_value)
        tvEmptyState = view.findViewById(R.id.tv_empty_state)
        rvIntake = view.findViewById(R.id.rv_intake)
        btnAdd250 = view.findViewById(R.id.btn_add_250)
        btnAdd500 = view.findViewById(R.id.btn_add_500)
        btnReminder30 = view.findViewById(R.id.btn_reminder_30)
        btnReminder60 = view.findViewById(R.id.btn_reminder_60)
        btnReminder120 = view.findViewById(R.id.btn_reminder_120)
        btnReminderOff = view.findViewById(R.id.btn_reminder_off)

        btnReminder30.isCheckable = true
        btnReminder60.isCheckable = true
        btnReminder120.isCheckable = true
    }

    private fun setupRecyclerView() {
        intakeAdapter = IntakeAdapter(intakeEntries)
        rvIntake.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = intakeAdapter
        }
    }

    private fun setupButtons() {
        btnAdd250.setOnClickListener { addIntake(250) }
        btnAdd500.setOnClickListener { addIntake(500) }
        btnReminder30.setOnClickListener { scheduleReminder(intervalMinutes = 2) }
        btnReminder60.setOnClickListener { scheduleReminder(intervalMinutes = 60) }
        btnReminder120.setOnClickListener { scheduleReminder(intervalMinutes = 120) }
        btnReminderOff.setOnClickListener { cancelReminder() }
    }

    private fun addIntake(amountMl: Int) {
        intakeEntries.add(0, IntakeEntry(amountMl, Date()))
        intakeAdapter.notifyItemInserted(0)
        rvIntake.scrollToPosition(0)
        renderProgress()
        updateEmptyState()
    }

    private fun scheduleReminder(intervalMinutes: Int) {
        if (!isNotificationPermissionGranted()) {
            Toast.makeText(requireContext(), R.string.reminder_permission_denied_toast, Toast.LENGTH_SHORT).show()
            return
        }

        if (!ensureExactAlarmPermission()) {
            return
        }

        cancelReminder(showToast = false)

        HydrationReminderReceiver.scheduleExact(requireContext(), intervalMinutes)
        saveReminderInterval(intervalMinutes)
        updateReminderButtonState(intervalMinutes)

        Toast.makeText(
            requireContext(),
            getString(R.string.reminder_set_toast, intervalMinutes),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun cancelReminder(showToast: Boolean = true) {
        val hadReminder = currentReminderInterval() != null

        HydrationReminderReceiver.cancelScheduled(requireContext())
        clearReminderInterval()
        updateReminderButtonState(null)

        if (showToast && hadReminder) {
            Toast.makeText(requireContext(), R.string.reminder_cancelled_toast, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        val permissionState = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun renderProgress() {
        val totalIntake = intakeEntries.sumOf { it.amountMl }
        progressDaily.progress = totalIntake.coerceAtMost(dailyGoalMl)
        tvGoalValue.text = getGoalLabel(totalIntake)
    }

    private fun updateEmptyState() {
        tvEmptyState.visibility = if (intakeEntries.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun getGoalLabel(totalMl: Int): String {
        val consumedLiters = totalMl / 1000f
        val goalLiters = dailyGoalMl / 1000f
        return getString(R.string.goal_label_format, consumedLiters, goalLiters)
    }

    private fun saveReminderInterval(intervalMinutes: Int) {
        reminderPrefs().edit()
            .putInt(HydrationReminderReceiver.KEY_INTERVAL_MINUTES, intervalMinutes)
            .apply()
    }

    private fun clearReminderInterval() {
        reminderPrefs().edit()
            .remove(HydrationReminderReceiver.KEY_INTERVAL_MINUTES)
            .apply()
    }

    private fun currentReminderInterval(): Int? {
        val stored = reminderPrefs().getInt(HydrationReminderReceiver.KEY_INTERVAL_MINUTES, -1)
        return stored.takeIf { it > 0 }
    }

    private fun reminderPrefs() =
        requireContext().getSharedPreferences(HydrationReminderReceiver.PREFS_NAME, Context.MODE_PRIVATE)

    private fun updateReminderButtonState(activeIntervalMinutes: Int?) {
        val buttons = listOf(
            btnReminder30 to 2,
            btnReminder60 to 60,
            btnReminder120 to 120
        )

        buttons.forEach { (button, interval) ->
            button.isChecked = activeIntervalMinutes == interval
        }
    }

    private fun ensureExactAlarmPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (alarmManager.canScheduleExactAlarms()) return true

        Toast.makeText(requireContext(), R.string.reminder_exact_alarm_needed_toast, Toast.LENGTH_SHORT).show()

        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:${requireContext().packageName}")
        }
        runCatching { startActivity(intent) }

        return false
    }
}

data class IntakeEntry(
    val amountMl: Int,
    val timestamp: Date
)

class IntakeAdapter(
    private val items: List<IntakeEntry>
) : RecyclerView.Adapter<IntakeAdapter.IntakeViewHolder>() {

    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

    inner class IntakeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amount: TextView = view.findViewById(R.id.tv_intake_amount)
        val time: TextView = view.findViewById(R.id.tv_intake_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntakeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_intake_entry, parent, false)
        return IntakeViewHolder(view)
    }

    override fun onBindViewHolder(holder: IntakeViewHolder, position: Int) {
        val entry = items[position]
        holder.amount.text = holder.itemView.context.getString(R.string.intake_amount_format, entry.amountMl)
        holder.time.text = timeFormatter.format(entry.timestamp)
    }

    override fun getItemCount(): Int = items.size
}
