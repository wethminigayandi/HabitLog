package com.example.habittracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment

class EditHabitDialogFragment(
    private val habit: TodayHabit,
    private val onHabitUpdated: (String, String, String, String) -> Unit // ✅ Fixed: 4 parameters matching your usage
) : DialogFragment() {

    private lateinit var etGoal: EditText
    private lateinit var etHabitName: EditText
    private lateinit var spinnerPeriod: Spinner
    private lateinit var spinnerHabitType: Spinner
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: TextView
    private lateinit var ivClose: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_habit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupSpinners()
        populateFields()
        setupClickListeners()
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
        btnUpdate = view.findViewById(R.id.btn_update)
        btnDelete = view.findViewById(R.id.btn_delete)
        ivClose = view.findViewById(R.id.iv_close)
    }

    private fun setupSpinners() {
        // Period Spinner
        val periodOptions = arrayOf("1 Week (7 Days)", "1 Month (30 Days)", "3 Months (90 Days)")
        val periodAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, periodOptions)
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPeriod.adapter = periodAdapter

        // Habit Type Spinner
        val habitTypeOptions = arrayOf("Everyday", "Weekly", "Custom")
        val habitTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, habitTypeOptions)
        habitTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHabitType.adapter = habitTypeAdapter
    }

    private fun populateFields() {
        // Find corresponding goal to populate goal field
        val correspondingGoal = HomeFragment.goals.find { it.habitId == habit.habitId }

        etHabitName.setText(habit.name)
        correspondingGoal?.let {
            etGoal.setText(it.name)
        }

        // Set spinner selections based on current data
        val periodPosition = correspondingGoal?.let { goal ->
            when (goal.target) {
                7 -> 0    // 1 Week
                30 -> 1   // 1 Month
                90 -> 2   // 3 Months
                else -> 1 // Default to 1 Month
            }
        } ?: 1
        spinnerPeriod.setSelection(periodPosition)

        val habitTypePosition = correspondingGoal?.let { goal ->
            when (goal.frequency) {
                "Everyday" -> 0
                "Weekly" -> 1
                "Custom" -> 2
                else -> 0 // Default to Everyday
            }
        } ?: 0
        spinnerHabitType.setSelection(habitTypePosition)
    }

    private fun setupClickListeners() {
        ivClose.setOnClickListener { dismiss() }

        btnUpdate.setOnClickListener {
            if (validateInputs()) {
                val updatedGoal = etGoal.text.toString().trim()
                val updatedHabitName = etHabitName.text.toString().trim()
                val period = spinnerPeriod.selectedItem.toString()
                val habitType = spinnerHabitType.selectedItem.toString()

                // ✅ Fixed: Call with 4 parameters as expected
                onHabitUpdated(updatedGoal, updatedHabitName, period, habitType)
                dismiss()
            }
        }

        btnDelete.setOnClickListener {
            // Show delete confirmation
            val deleteDialog = DeleteConfirmationDialogFragment { confirmed ->
                if (confirmed) {
                    // Delete logic will be handled by parent
                    dismiss()
                }
            }
            deleteDialog.show(parentFragmentManager, "DeleteConfirmation")
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
