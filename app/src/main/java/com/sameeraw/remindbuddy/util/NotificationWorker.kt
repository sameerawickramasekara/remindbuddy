package com.sameeraw.remindbuddy.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource


class NotificationWorker (

    val context: Context,
    userParameters: WorkerParameters,
) : Worker(context, userParameters) {

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {

        val lat = inputData.getString("lat")
        val lon = inputData.getString("lon")


        if(lat != null && lon != null ){

            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ){

                    val cts = CancellationTokenSource()
                    val locationResult = fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,cts.token)

                    locationResult.addOnCompleteListener {
                            task ->
                        if (task.isSuccessful){
                            val lastKnownLocation = task.result

                            if (lastKnownLocation != null){
                                Log.d("TAG",lastKnownLocation.latitude.toString())
                                val temp = Location(LocationManager.GPS_PROVIDER)
                                temp.latitude = lat.toDouble()
                                temp.longitude = lon.toDouble()

                                if (lastKnownLocation.distanceTo(temp) < 200){

                                    Log.i("NOTIFY","INSIDE")
                                    NotificationHelper(context).createNotification(
                                        inputData.getString("title").toString(),
                                        inputData.getString("message").toString(),
                                        inputData.getLong("reminderId",-1)
                                    )

                                }
                            }
                        }else{
                            Log.d("Exception"," Current User location is null")
                        }
                    }

                }

            }catch (e: SecurityException){
                Log.d("Exception", "Exception:  $e.message.toString()")
            }
        }else {
            NotificationHelper(context).createNotification(
                inputData.getString("title").toString(),
                inputData.getString("message").toString(),
                inputData.getLong("reminderId",-1)
            )
        }



           return Result.success();
    }
}