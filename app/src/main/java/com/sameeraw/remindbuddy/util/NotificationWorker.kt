package com.sameeraw.remindbuddy.util

import androidx.work.Worker
import androidx.work.WorkerParameters
import android.content.Context

class NotificationWorker (

    val context: Context,
    userParameters: WorkerParameters,
) : Worker(context, userParameters) {

    override fun doWork(): Result {
           NotificationHelper(context).createNotification(
               inputData.getString("title").toString(),
               inputData.getString("message").toString(),
               inputData.getLong("reminderId",-1)
           )
           return Result.success();
    }
}