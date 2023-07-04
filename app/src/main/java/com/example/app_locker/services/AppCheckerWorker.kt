package com.example.app_locker.services

import android.app.ActivityManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.*
import com.example.app_locker.activities.login.LoginActivity
import com.example.app_locker.directory.utils.GetCheckedApps
import java.util.*
import java.util.concurrent.TimeUnit

class AppCheckerWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val checkInterval = 2000L

    override fun doWork(): Result {
        val foregroundAppPackageName = getForegroundApp()

        val selectedApps = GetCheckedApps.getSelectedApps(applicationContext)

        if (!LoginActivity.isActive && !foregroundAppPackageName.isNullOrEmpty()
            && selectedApps.contains(foregroundAppPackageName)
        ) {

            val lockIntent = Intent(applicationContext, LoginActivity::class.java)
            lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            lockIntent.putExtra("DESIRED_APP_PACKAGE_NAME", foregroundAppPackageName)
            lockIntent.putExtra("abc", true)

            Log.d("lastapp", "hello" + foregroundAppPackageName)
            applicationContext.startActivity(lockIntent)
        }

        scheduleNextWork()
        return Result.success()
    }

    fun getForegroundApp(): String {
        var currentApp = "NULL"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val usm =
                applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
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
            val am =
                applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val tasks = am?.runningAppProcesses
            currentApp = tasks?.get(0)?.processName ?: "NULL"
        }
        Log.d("lastpackagename", "hello" + currentApp)
        return currentApp
    }

    private fun scheduleNextWork() {
        val workRequest = OneTimeWorkRequestBuilder<AppCheckerWorker>()
            .setInitialDelay(checkInterval, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork("app_checker_work", ExistingWorkPolicy.REPLACE, workRequest)
    }
}
