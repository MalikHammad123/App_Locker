package com.example.app_locker.services

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.app_locker.R
import com.example.app_locker.activities.login.LoginActivity
import com.example.app_locker.directory.utils.GetCheckedApps
import java.util.*


class AppCheckerService : Service() {


    private val checkInterval = 2000L  // Check every second
    private val mHandler = Handler(Looper.getMainLooper())

    private val mRunnable = object : Runnable {
        override fun run() {
            val foregroundAppPackageName = getForegroundApp()

            val selectedApps = GetCheckedApps.getSelectedApps(this@AppCheckerService)
            for ( i in selectedApps){
                Log.d("AAAAAAAAAAAAAA"," " + i)
            }

            // Check if the LockActivity is not active and the intended package name is not empty
            if (!LoginActivity.isActive && !foregroundAppPackageName.isNullOrEmpty()
                && selectedApps.contains(foregroundAppPackageName)
            ) {

                // Launch the login activity with the intended package name as an extra
                val lockIntent = Intent(this@AppCheckerService, LoginActivity::class.java)
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                lockIntent.putExtra("DESIRED_APP_PACKAGE_NAME", foregroundAppPackageName)
                lockIntent.putExtra("abc", true)

                Log.d("lastapp", "hello" + foregroundAppPackageName)
                startActivity(lockIntent)
            }

            mHandler.postDelayed(this, checkInterval)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(1, notification)
        Toast.makeText(this, "Service started running!", Toast.LENGTH_SHORT).show()
        mHandler.post(mRunnable)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(mRunnable)
    }

    fun getForegroundApp(): String {
        var currentApp = "NULL"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val usm = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            val time = System.currentTimeMillis()
            val appList =
                usm?.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
            if (appList != null && appList.isNotEmpty()) {
                val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
                for (usageStats in appList) {
                    mySortedMap[usageStats.lastTimeUsed] = usageStats
                }
                if (mySortedMap.isNotEmpty()) {
                    currentApp = mySortedMap[mySortedMap.lastKey()]?.packageName ?: "NULL"
                }
            }
        } else {
            val am = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val tasks = am?.runningAppProcesses
            currentApp = tasks?.get(0)?.processName ?: "NULL"
        }
        Log.d("lastpackagename", "hello" + currentApp)
        return currentApp
    }

    private fun createNotification(): Notification {
        val channelId = "your_channel_id"
        val channelName = "Your Service"
        val importance = NotificationManager.IMPORTANCE_LOW // Set the importance level

        val notificationChannel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationIntent = Intent(this, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Your Service is Running")
            .setContentText("This is running in the background.")
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with your app's icon
            .setContentIntent(pendingIntent)
            .build()
    }


}

