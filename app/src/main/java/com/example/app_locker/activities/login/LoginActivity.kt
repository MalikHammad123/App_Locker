package com.example.app_locker.activities.login

import com.example.app_locker.bottomsheets.SecurityQuestionsBottomSheet
import android.Manifest
import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.TextureView
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.app_locker.R
import com.example.app_locker.activities.main.MainActivity
import com.example.app_locker.directory.utils.BiometricHelper
import com.example.app_locker.directory.utils.SwitchHandler
import com.example.app_locker.directory.utils.SwitchHandler.SWITCH_STATE_KEY
import com.example.app_locker.services.AppCheckerService
import com.example.app_locker.services.AppCheckerWorker
import java.io.File
import java.util.concurrent.TimeUnit



class LoginActivity : AppCompatActivity() {
    private val passcode = StringBuilder()
    private var confirmingPasscode: String? = null
    private var isConfirming = false
    private var isResetting = false
    private var failedAttempts = 0
    private var myboolean: Boolean = false
    private var allowCameraAccess = false
    private lateinit var sharedPreferencesWrongAttempt: SharedPreferences
    private lateinit var sharedPreferencesIntruder: SharedPreferences

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var layout: LinearLayout

        fun setBackgroundImage(background: Drawable) {
            layout.background = background
        }
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private const val PREFS_NAME = "IntruderPrefs"
        private const val SWITCH_STATE_KEY = "switchState"
        private val REQUEST_CODE_PERMISSIONS = 101
        var isActive = false
        private val REQUEST_PERMISSION_CODE = 1
        const val DESIRED_APP_PACKAGE_NAME = "DESIRED_APP_PACKAGE_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        layout=findViewById(R.id.linear_layout)
        val image1:ImageView=findViewById(R.id.image1)
        val image3:ImageView=findViewById(R.id.image3)
        sharedPreferencesIntruder = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Retrieve the switch state from SharedPreferences
        val switchStateIntruder = sharedPreferencesIntruder.getBoolean(SWITCH_STATE_KEY, false)




        sharedPreferencesWrongAttempt = getSharedPreferences("wrongattempts", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferencesWrongAttempt.getInt("selectedButton", -1)
        myboolean = intent.getBooleanExtra("abc", false)
        ////////////////////////////////////////////////
        val textViewPasscode1 = findViewById<TextView>(R.id.textViewPasscode1)
        val textViewPasscode = findViewById<TextView>(R.id.textViewPasscode)
        val buttonReset = findViewById<Button>(R.id.buttonReset)

        if (hasUsageStatsPermission()) {
            startAppCheckerService()
        } else {
            Toast.makeText(this, "Usage stats permission not granted!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        val imageViews = listOf(
            findViewById<ImageView>(R.id.imageView1),
            findViewById<ImageView>(R.id.imageView2),
            findViewById<ImageView>(R.id.imageView3),
            findViewById<ImageView>(R.id.imageView4),
            findViewById<ImageView>(R.id.imageView5),
            findViewById<ImageView>(R.id.imageView6),
            findViewById<ImageView>(R.id.imageView7),
            findViewById<ImageView>(R.id.imageView8),
            findViewById<ImageView>(R.id.imageView9),
            findViewById<ImageView>(R.id.imageView0)
        )
        val imageViewX = findViewById<ImageView>(R.id.imageViewx)

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        var savedPasscode = sharedPreferences.getString("passcode", null)
        buttonReset.setOnClickListener {
            if (savedPasscode != null) {
                isResetting = true
                textViewPasscode1.text = getString(R.string.EnterSavedPassword)


            } else {
                Toast.makeText(this, "No passcode to reset", Toast.LENGTH_SHORT).show()
            }
        }
        if (savedPasscode == null) {
            textViewPasscode1.text = getString(R.string.Create4DigitPIN)
        } else {
            textViewPasscode1.text = getString(R.string.Confrm4DigitPIN)

        }
////////////////////////////////////////////////fingerprint authentification//////////////////////////

        val switchState = SwitchHandler.getSwitchState(this)

        if (switchState) {
            BiometricHelper.init(this,
                this,
                MainActivity::class.java,
                { intent, requestCode -> startActivityForResult(intent, requestCode) })

            BiometricHelper.checkDeviceCanAuthenticateWithBiometrics()
            BiometricHelper.authenticateWithBiometrics()
            Toast.makeText(this, "Switch is turnedd ON", Toast.LENGTH_SHORT).show()

        } else {
            // Switch in SettingsActivity is not checked
            Toast.makeText(this, "Switch is turnedd off", Toast.LENGTH_SHORT).show()
        }

        val fromSettingActivity = intent.getBooleanExtra("reset_password_from_setting", false)
        if (fromSettingActivity) {


            textViewPasscode1.text = getString(R.string.EnterSavedPassword)
            isResetting = true
            buttonReset.visibility = View.INVISIBLE
            // Rest of UI related to login should be made invisible or non-interactable
            // based on your design
        }
///////////////////////////////////////////////////////////////////////////////////////////////////////
        val onClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.imageView1,
                R.id.imageView2,
                R.id.imageView3,
                R.id.imageView4,
                R.id.imageView5,
                R.id.imageView6,
                R.id.imageView7,
                R.id.imageView8,
                R.id.imageView9,
                R.id.imageView0 -> {
                    if (passcode.length < 4) {
                        passcode.append((view.id - R.id.imageView0).toString())
                    } else {
                        Toast.makeText(
                            this,
                            "Passcode should be of 4 digits only",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                R.id.imageViewx -> {
                    if (passcode.isNotEmpty()) {
                        passcode.deleteCharAt(passcode.length - 1)
                    }
                }

            }
            if (passcode.length==4){
                if (isResetting) {
                    if (passcode.toString() == savedPasscode) {
                        // If old passcode matches, start the process of setting a new passcode
                        isConfirming = false
                        isResetting = false
                        savedPasscode = null

                        textViewPasscode1.text = getString(R.string.Create4DigitPIN)

                        sharedPreferences.edit().remove("passcode").apply()
                        Toast.makeText(this, "Passcode reset successful", Toast.LENGTH_SHORT)
                            .show()
                        textViewPasscode.text = null
                    } else {
                        Toast.makeText(this, "Incorrect passcode", Toast.LENGTH_SHORT).show()
                    }
                }
                else if (savedPasscode == null)
                {
                    if (isConfirming) {
                        if (confirmingPasscode == passcode.toString()) {


                            // Confirming passcode matches, save it
                            sharedPreferences.edit().putString("passcode", passcode.toString())
                                .apply()
                            savedPasscode = passcode.toString()
                            textViewPasscode.text = null
                            isConfirming = false
                            Toast.makeText(
                                this,
                                "Password..saved successfully and entering main",
                                Toast.LENGTH_SHORT
                            ).show()
                            image3.setImageResource(R.drawable.image3a)

                            /*WorkManager.getInstance(applicationContext)
                                .cancelUniqueWork("app_checker_work")
                            val workRequest =
                                OneTimeWorkRequestBuilder<AppCheckerWorker>().build()
                            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                                "app_checker_work",
                                ExistingWorkPolicy.REPLACE,
                                workRequest
                            )*/

                            stopService(Intent(this, AppCheckerService::class.java))
                            startActivity(Intent(this, MainActivity::class.java))


                        } else {
                            // Confirming passcode does not match
                            Toast.makeText(
                                this,
                                "Passcode Does Not Match, Try Again",
                                Toast.LENGTH_SHORT
                            ).show()
                            isConfirming = false
                        }
                    } else {
                        // Start confirming passcode
                        confirmingPasscode = passcode.toString()
                        textViewPasscode.text = null
                        textViewPasscode1.text = getString(R.string.Confrm4DigitPIN)

                        isConfirming = true
                        Toast.makeText(
                            this,
                            "Entre password again to validate",
                            Toast.LENGTH_SHORT
                        ).show()
                            image1.setImageResource(R.drawable.image1)
                    }
                } else if (savedPasscode == passcode.toString()) {
                    // If passcode matches, proceed to the next activity
                    textViewPasscode.text = null
                    failedAttempts = 0



                    if (myboolean) {


                        //stopService(Intent(this, AppCheckerService::class.java))
                        //finish()
                        Toast.makeText(
                            this,
                            "Correct PIN and service have been stopped",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Retrieve the desired app package name from intent extras
                        val desiredAppPackageName =
                            intent.getStringExtra(DESIRED_APP_PACKAGE_NAME)
                        Toast.makeText(this, "" + desiredAppPackageName, Toast.LENGTH_SHORT)
                            .show()

                        if (desiredAppPackageName != null) {
                            openDesiredApplication(desiredAppPackageName)
                        } else {
                            Toast.makeText(
                                this,
                                "Desired application package name not found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    } else {
                        Toast.makeText(this, "entering to mainactivity2", Toast.LENGTH_SHORT)
                            .show()

                        //stopService(Intent(this, AppCheckerService::class.java))
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                } else {

                    // If passcode does not match
                    textViewPasscode.text = null
                    failedAttempts++
                    if (failedAttempts == savedNumber && switchStateIntruder == true) {
                        allowCameraAccess = true
                        launchCamera()
                    }

                    if (failedAttempts == 6) {
                        // Show your custom dialog here
                        showDialog()
                        failedAttempts = 0
                    } else {
                        Toast.makeText(
                            this,
                            "Incorrect password...Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                passcode.clear()
            }
            //if (view.id != R.id.imageViewOK) {
                textViewPasscode.text = passcode.toString()
           //}
        }

        for (imageView in imageViews) {
            imageView.setOnClickListener(onClickListener)
        }
        imageViewX.setOnClickListener(onClickListener)
        //imageViewOK.setOnClickListener(onClickListener)
    }

    fun showDialog() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.forget_password_dialogue)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cancel = dialog.findViewById<TextView>(R.id.unlock_tv)
        val ok = dialog.findViewById<TextView>(R.id.lock_tv)

        cancel.setOnClickListener {
            // Add code to handle click events for the "Cross" button here
            dialog.dismiss() // close the dialog
        }

        ok.setOnClickListener {
            val bottomSheetFragment = SecurityQuestionsBottomSheet()

            // Create a Bundle and put a Boolean into it
            val bundle = Bundle()
            bundle.putBoolean("hideSavedButton", true)
            // Set the Bundle as the arguments of the Fragment
            bottomSheetFragment.arguments = bundle

            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        dialog.show()

    }

    override fun onResume() {
        super.onResume()
        isActive = true
    }

    override fun onPause() {
        super.onPause()
        isActive = false
    }

    private fun openDesiredApplication(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(launchIntent)
        } else {
            Toast.makeText(this, "Desired application not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                if (allPermissionsGranted()) {
                    launchCamera()
                } else {
                }
            }
            REQUEST_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start the AppCheckerService
                    startAppCheckerService()
                } else {
                }
            }
        }
    }


    private fun launchCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Initialize CameraX and start capturing images
            val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
            cameraProviderFuture.addListener(Runnable {
                val cameraProvider = cameraProviderFuture.get()
                val imageCapture = ImageCapture.Builder().build()

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture)

                    // Capture an image
                    val outputFile = File(cacheDir, "image_${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(this),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                // Image saved successfully
                            }

                            override fun onError(exception: ImageCaptureException) {
                                // Image capture failed
                            }
                        })
                } catch (exception: Exception) {
                    // Camera binding failed
                }
            }, ContextCompat.getMainExecutor(this))
        } else {
            // Request camera permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun startAppCheckerService() {
        // Start the AppCheckerService
        startService(Intent(this, AppCheckerService::class.java))

   /*     val workRequest = PeriodicWorkRequestBuilder<AppCheckerWorker>(
            15,
            TimeUnit.MINUTES
        ) // Adjust the interval as per your requirements
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "app_checker_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )*/

        Toast.makeText(this, "Service started running", Toast.LENGTH_SHORT).show()
    }
    //////////////////////////////////////////////////////////////


    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }



}