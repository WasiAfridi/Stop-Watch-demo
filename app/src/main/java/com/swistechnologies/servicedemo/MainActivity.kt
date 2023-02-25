package com.swistechnologies.servicedemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.swistechnologies.servicedemo.databinding.ActivityMainBinding
import java.sql.Time
import kotlin.math.roundToInt

@Suppress("UNUSED_EXPRESSION")
class MainActivity : AppCompatActivity() {

    private val channelID = "com.swistechnologies.servicedemo.channel1"
    private var notificationManager : NotificationManager? = null
    private var isStarted: Boolean = false
    private lateinit var binding: ActivityMainBinding
    lateinit var serviceIntent : Intent
    private var time = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(channelID, "ServicesChannel", "This is Services Demo")


        binding.btnStart.setOnClickListener {
            startOrStop()
        }

        binding.btnReset.setOnClickListener {
            reset()
        }

        serviceIntent = Intent(this, MyBackgroundService::class.java)
        registerReceiver(updateTime, IntentFilter(MyBackgroundService.UPDATED_TIME))


    }

    private fun startOrStop(){
        if (isStarted)
            stop()
        else
            start()
    }

    private fun start(){
        displayNotification()
        serviceIntent.putExtra(MyBackgroundService.CURRENT_TIME, time)
        startService(serviceIntent)
        binding.btnStart.text = "Stop"
        isStarted = true
    }

    private fun stop(){
        stopService(serviceIntent)
        binding.btnStart.text = "Start"
        isStarted = false
    }

    private fun reset(){
        stop()
        time = 0.0
        binding.tvWatch.text = getFormattedTime(time)
    }

    private val updateTime: BroadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(MyBackgroundService.CURRENT_TIME, 0.0)
            binding.tvWatch.text = getFormattedTime(time)
        }

    }

    private fun getFormattedTime(time:Double):String{
        val timeInt = time.roundToInt()
        val hours = (timeInt % 86400)/3600
        val minutes = (timeInt % 86400 % 3600)/60
        val seconds = timeInt % 86400 % 3600 % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    //To display notification
    private fun displayNotification(){
        val notificationId = 15
        val notification = NotificationCompat.Builder(this@MainActivity, channelID)
            .setContentTitle("Service Demo")
            .setContentText("This Notification is Generated from Services Demo App")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH).build()

        notificationManager?.notify(notificationId,notification)

    }

    //To create notification Channel
    private fun createNotificationChannel(id: String, name: String, channelDescription: String){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val importance:Int = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

}