package com.sameeraw.remindbuddy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.sameeraw.remindbuddy.util.NotificationWorker
import java.util.concurrent.TimeUnit

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    // ...
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent!!)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e("TAG", errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        Log.i("BROADCAST", "GOT MSG")
        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            val name = intent.getStringExtra("reminderName")
            val desc = intent.getStringExtra("reminderDesc")
            val myWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(2, TimeUnit.SECONDS)
                .setInputData(
                    workDataOf(
                        "title" to name,
                        "message" to desc,
                        "reminderId" to -1
                    )
                )
                .build()



            WorkManager.getInstance(context!!).enqueueUniqueWork(
                "locationTrigger",
                ExistingWorkPolicy.REPLACE, myWorkRequest
            )
        } else {
            // Log the error.
            Log.e("TAG", "ERROR")
        }
    }
}