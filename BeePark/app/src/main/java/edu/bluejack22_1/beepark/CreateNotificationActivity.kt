package edu.bluejack22_1.beepark

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.bluejack22_1.beepark.databinding.ActivityCreateNotificationBinding
import edu.bluejack22_1.beepark.model.PushNotification
import edu.bluejack22_1.beepark.model.channelID
import edu.bluejack22_1.beepark.model.messageExtra
import edu.bluejack22_1.beepark.model.notificationID
import java.text.DateFormat
import java.util.*

class CreateNotificationActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        binding.submitButton.setOnClickListener{
            scheduleNotification()
        }
    }

    private fun scheduleNotification() {
        val intent = Intent(applicationContext, PushNotification::class.java)
        val parkingText = binding.parkingReminderET.text.toString()
        intent.putExtra(messageExtra, parkingText)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time, title, parkingText)
    }

    private fun showAlert(time: Long, title: CharSequence?, parkingText: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notif")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + parkingText +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
            .setPositiveButton("Oke"){
                _,_->
            }
            .show()
    }

    private fun getTime(): Long {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour,minute)
        return calendar.timeInMillis
    }

    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A Description of The Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID,name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            channel
        )

    }
}