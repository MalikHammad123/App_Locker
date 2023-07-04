package com.example.app_locker.bottomsheets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.app_locker.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WrongAttemptBottomSheet : BottomSheetDialogFragment() {
    private lateinit var cancel: TextView
    private lateinit var ok: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            requireContext().getSharedPreferences("wrongattempts", Context.MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.wrong_attempt_bottomsheet, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
        cancel = view.findViewById(R.id.cancel)
        ok = view.findViewById(R.id.ok)

        cancel.setOnClickListener {
            dismiss()
        }
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<RadioButton>(checkedId)
            val buttonIndex = group.indexOfChild(radioButton) + 1
            ok.setOnClickListener {
                saveButtonState(buttonIndex)
                dismiss()
            }
        }

        val selectedButton = sharedPreferences.getInt("selectedButton", -1)
        if (selectedButton != -1) {
            val radioButton = radioGroup.getChildAt(selectedButton - 1) as RadioButton
            radioButton.isChecked = true
        }
    }

    private fun saveButtonState(buttonIndex: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("selectedButton", buttonIndex)
        editor.apply()
    }
}