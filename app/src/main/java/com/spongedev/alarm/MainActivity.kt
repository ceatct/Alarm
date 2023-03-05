package com.spongedev.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.spongedev.alarm.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var picker : MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotifyChannel()

        binding.cancelBt.setOnClickListener{
            cancelAlarm()
        }

        binding.selectBt.setOnClickListener{
            showTimePicker()
        }

        binding.setBt.setOnClickListener{
            setAlarm()
        }

    }

    private fun cancelAlarm(){

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        alarmManager.cancel(pendingIntent)

        Toast.makeText(this, "Alarm canceled", Toast.LENGTH_SHORT).show()

    }

    private fun setAlarm(){

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        alarmManager.setRepeating(

            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent

        )

        Toast.makeText(this, "Alarm set", Toast.LENGTH_SHORT).show()

    }

    private fun showTimePicker(){

        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()

        picker.show(supportFragmentManager, "alarm")

        picker.addOnPositiveButtonClickListener{

            if(picker.hour > 12){
                binding.timeTxt.text = String().format("%02d", picker.hour - 12) + " : " + String.format("%02d", picker.minute) + "PM"
            }
            else{
                binding.timeTxt.text = String().format("%02d", picker.hour - 12) + " : " + String.format("%02d", picker.minute) + "AM"
            }

            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0

        }

    }

    private fun createNotifyChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val name : CharSequence = "alarmReminderChannel"
            val description = "Channel for Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alarm", name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)

        }
    }

}