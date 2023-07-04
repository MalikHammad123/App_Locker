package com.example.app_locker.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.app_locker.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.app_locker.activities.main.MainActivity
class SecurityQuestionsBottomSheet : BottomSheetDialogFragment() {
    private lateinit var questionSpinner: Spinner
    private lateinit var answerEditText: EditText
    private lateinit var setQuestionsButton: TextView
    private lateinit var verifyQuestionsButton: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var listener: SecurityQuestionsListener? = null

    interface SecurityQuestionsListener {
        fun onSetQuestions(question: String, answer: String)
        fun onVerifyQuestions(answer: String): Boolean
    }

    private val questions = listOf(
        "What’s your favorite color?",
        "What’s your pet name?",
        "What’s your lucky number?"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_navigation_sheet, container, false)

        // Initialize views
        questionSpinner = view.findViewById(R.id.questionSpinner)
        answerEditText = view.findViewById(R.id.answerEditText)
        setQuestionsButton = view.findViewById(R.id.setQuestions)
        verifyQuestionsButton = view.findViewById(R.id.verifyQuestions)
        //verifyQuestionsButton.visibility = View.INVISIBLE

        // Set up question spinner
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, questions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        questionSpinner.adapter = adapter

        questionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // clear the EditText field whenever an item is selected
                answerEditText.setText("")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        arguments?.let {
            if (it.getBoolean("hideSavedButton", false)) {
                // If the "hideButton" Boolean is true, hide the button
                setQuestionsButton.visibility = View.INVISIBLE
            }
        }

        arguments?.let {
            if (it.getBoolean("hideVerifyButton", false)) {
                // If the "hideButton" Boolean is true, hide the button
                verifyQuestionsButton.visibility = View.INVISIBLE
            }
        }

        // Initialize SharedPreferences
        sharedPreferences =
            requireContext().getSharedPreferences("SecurityPreferences", Context.MODE_PRIVATE)
        setQuestionsButton.setOnClickListener {
            val question = questionSpinner.selectedItem.toString()
            val answer = answerEditText.text.toString()

            if (question.isEmpty() || answer.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT)
                    .show()
            } else {
                saveAnswer(question, answer)
                listener?.onSetQuestions(question, answer)

                // Check if all answers are set
                val allAnswersSet = areAllAnswersSet()
                if (allAnswersSet) {
                    Toast.makeText(requireContext(), "All fields set", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please set answers for all questions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }



        verifyQuestionsButton.setOnClickListener {
            val answer = answerEditText.text.toString()
            val isCorrect = verifyAnswer(answer)
            if (isCorrect) {
                val allAnswersSet = areAllAnswersSet()
                if (allAnswersSet) {
                    Toast.makeText(requireContext(), "All answers are correct", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(context, MainActivity::class.java))
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please set answers for all questions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(requireContext(), "Answers are wrong", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun saveAnswer(question: String, answer: String) {
        val editor = sharedPreferences.edit()
        editor.putString(question, answer)
        editor.apply()
    }

    private fun verifyAnswer(answer: String): Boolean {
        val question = questionSpinner.selectedItem.toString()
        val savedAnswer = sharedPreferences.getString(question, "")
        return savedAnswer == answer
    }

    private fun areAllAnswersSet(): Boolean {
        for (question in questions) {
            val answer = sharedPreferences.getString(question, "")
            if (answer.isNullOrEmpty()) {
                return false
            }
        }
        return true
    }

    fun setListener(listener: SecurityQuestionsListener) {
        this.listener = listener
    }

    companion object {
        fun newInstance(): SecurityQuestionsBottomSheet {
            return SecurityQuestionsBottomSheet()
        }
    }
}

