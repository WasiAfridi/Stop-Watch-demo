package com.swistechnologies.servicedemo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import java.util.*

class MyBackgroundService : Service() {

    val timer = Timer()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val time = intent.getDoubleExtra(CURRENT_TIME, 0.0)
        timer.scheduleAtFixedRate(StopWatchTimer(time), 0, 1000)
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show()
        return START_NOT_STICKY

    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onDestroy() {
        timer.cancel()
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show()
        super.onDestroy()
    }

    companion object{
        const val CURRENT_TIME = "current time"
        const val UPDATED_TIME = "updated time"
    }

    private inner class StopWatchTimer(private var time: Double):TimerTask(){
        override fun run() {
            val intent = Intent(UPDATED_TIME)
            time++
            intent.putExtra(CURRENT_TIME,time)
            sendBroadcast(intent)
        }

    }


}