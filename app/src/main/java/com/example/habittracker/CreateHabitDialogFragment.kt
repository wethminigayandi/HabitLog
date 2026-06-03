package com.example.habittracker

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment

class CreateHabitDialogFragment(
    private val onHabitCreated: (String, String, String, String) -> Unit
) : DialogFragment() {

    private lateinit var etGoal: EditText
    private lateinit var etHabitName: EditText
    private lateinit var spinnerPeriod: Spinner
    private lateinit var spinnerHabitType: Spinner
    private lateinit var btnCreateNew: Button
    private lateinit var ivClose: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_create_habit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupSpinners()
        setupClickListeners()
        setupAutoCompleteGoal()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun initViews(view: View) {
        etGoal = view.findViewById(R.id.et_goal)
        etHabitName = view.findViewById(R.id.et_habit_name)
        spinnerPeriod = view.findViewById(R.id.spinner_period)
        spinnerHabitType = view.findViewById(R.id.spinner_habit_type)
        btnCreateNew = view.findViewById(R.id.btn_create_new)
        ivClose = view.findViewById(R.id.iv_close)
    }

    private fun setupAutoCompleteGoal() {
        // Auto-complete goal name based on habit name
        etHabitName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val habitName = etHabitName.text.toString().trim()
                if (habitName.isNotEmpty() && etGoal.text.toString().trim().isEmpty()) {
                    // Generate goal text based on habit name
                    val goalText = generateGoalText(habitName)
                    etGoal.setText(goalText)
                }
            }
        }
    }

    private fun generateGoalText(habitName: String): String {
        return when {
            habitName.contains("read", ignoreCase = true) -> "Complete reading goal for $habitName"
            habitName.contains("exercise", ignoreCase = true) -> "Maintain regular $habitName routine"
            habitName.contains("meditat", ignoreCase = true) -> "Build consistent $habitName practice"
            habitName.contains("water", ignoreCase = true) -> "Stay hydrated with $habitName"
            habitName.contains("sleep", ignoreCase = true) -> "Improve $habitName schedule"
            else -> "Complete $habitName consistently"
        }
    }

    private fun setupSpinners() {
        // Period Spinner
        val periodOptions = arrayOf(
            "1 Week (7 Days)",
            "1 Month (30 Days)",
            "3 Months (90 Days)"
        )
        val periodAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            periodOptions
        )
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPeriod.adapter = periodAdapter
        spinnerPeriod.setSelection(1) // Default to "1 Month (30 Days)"

        // Habit Type Spinner
        val habitTypeOptions = arrayOf(
            "Everyday",
            "Weekly",
            "Custom"
        )
        val habitTypeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            habitTypeOptions
        )
        habitTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHabitType.adapter = habitTypeAdapter
        spinnerHabitType.setSelection(0) // Default to "Everyday"
    }

    private fun setupClickListeners() {
        ivClose.setOnClickListener {
            dismiss()
        }

        btnCreateNew.setOnClickListener {
            if (validateInputs()) {
                val goal = etGoal.text.toString().trim()
                val habitName = etHabitName.text.toString().trim()
                val period = spinnerPeriod.selectedItem.toString()
                val habitType = spinnerHabitType.selectedItem.toString()

                onHabitCreated(goal, habitName, period, habitType)
                dismiss()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val goal = etGoal.text.toString().trim()
        val habitName = etHabitName.text.toString().trim()

        if (goal.isEmpty()) {
            etGoal.error = "Please enter your goal"
            etGoal.requestFocus()
            return false
        }

        if (habitName.isEmpty()) {
            etHabitName.error = "Please enter habit name"
            etHabitName.requestFocus()
            return false
        }

        return true
    }
}
