package com.example.app_locker.activities.setting

import android.annotation.SuppressLint
import com.example.app_locker.bottomsheets.SecurityQuestionsBottomSheet
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.app_locker.R
import com.example.app_locker.activities.login.LoginActivity
import com.example.app_locker.databinding.ActivitySettingBinding
import com.example.app_locker.directory.utils.SwitchHandler
import com.example.app_locker.fragments.vip.VipFragment
import com.google.android.material.appbar.MaterialToolbar

class Setting : AppCompatActivity(), SecurityQuestionsBottomSheet.SecurityQuestionsListener {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var securityQuestionsBottomSheet: SecurityQuestionsBottomSheet
    private var question: String = ""
    private var answer: String = ""
    private lateinit var passwordType: TextView

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val materialToolbar: MaterialToolbar = findViewById(R.id.material_tb)
        materialToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        securityQuestionsBottomSheet = SecurityQuestionsBottomSheet.newInstance()
        securityQuestionsBottomSheet.setListener(this)

        passwordType = binding.passwordtypeTv3
        val setQuestionsButton: FrameLayout = binding.framef5
        val share: FrameLayout = binding.framef6
        val VIP: FrameLayout = binding.framef0
        val fingerprint: Switch = binding.fingerprintSv
        val privacypolicy: FrameLayout = binding.framef8
        val passwordreset: FrameLayout = binding.framef4

        // Listen to switch state changes
        val switchState = SwitchHandler.getSwitchState(this)
        fingerprint.isChecked = switchState

        fingerprint.setOnCheckedChangeListener { _, isChecked ->
            SwitchHandler.saveSwitchState(this, isChecked)
        }


        passwordreset.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("reset_password_from_setting", true)
            startActivity(intent)
        }
        privacypolicy.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW, Uri.parse("https://www.xilliapps.com/privacy-policy.html")
            )
            startActivity(intent)
        }

        VIP.setOnClickListener {

            val fragment = VipFragment()
            supportFragmentManager.beginTransaction().replace(android.R.id.content, fragment)
                .commit()
        }

        passwordType.setOnClickListener { view ->
            showPopupMenu(view)
        }

        setQuestionsButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("hideVerifyButton", true)
            // Set the Bundle as the arguments of the Fragment
            securityQuestionsBottomSheet.arguments = bundle

            securityQuestionsBottomSheet.show(supportFragmentManager, "securityQuestions")
        }

        share.setOnClickListener {
            shareApplication()
        }
    }

    private fun shareApplication() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out this awesome app: https://example.com/your-app-download-link"
            )
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Share via"))
    }

    private fun showPopupMenu(anchorView: View) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_menu, null)
        val popupWindow = PopupWindow(
            popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val radioGroup = popupView.findViewById<RadioGroup>(R.id.radioGroup)
        val options = arrayOf("Pattern", "PIN")

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<RadioButton>(checkedId)
            val selectedOption = radioButton.text.toString()

            // Update the passwordType text based on the selected option
            when (selectedOption) {
                "Pattern" -> passwordType.text = "Pattern"
                "PIN" -> passwordType.text = "PIN"
            }

            // Dismiss the popup menu
            popupWindow.dismiss()

        }

        for (option in options) {
            val radioButton = RadioButton(this)
            radioButton.text = option
            radioGroup.addView(radioButton)
        }

        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val anchorViewX = location[0]
        val anchorViewY = location[1]
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set outside touchable to dismiss the popup when clicked outside
        popupWindow.isOutsideTouchable = true

        popupWindow.showAtLocation(
            anchorView, Gravity.NO_GRAVITY, anchorViewX, anchorViewY + anchorView.height
        )
    }

    override fun onSetQuestions(question: String, answer: String) {
        this.question = question
        this.answer = answer
        Toast.makeText(this, "Answers set", Toast.LENGTH_SHORT).show()

    }

    override fun onVerifyQuestions(answer: String): Boolean {
        // Verify the security question answer
        return this.answer == answer
    }
}
