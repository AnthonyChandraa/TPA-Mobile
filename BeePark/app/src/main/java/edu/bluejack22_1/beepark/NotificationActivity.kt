package edu.bluejack22_1.beepark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import edu.bluejack22_1.beepark.adapters.NotificationAdapter
import edu.bluejack22_1.beepark.controllers.UserController
import edu.bluejack22_1.beepark.databinding.ActivityHomeBinding
import edu.bluejack22_1.beepark.databinding.ActivityNotificationBinding
import java.util.*
import kotlin.collections.ArrayList

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var userId: String
    private lateinit var userController: UserController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationBinding.inflate(LayoutInflater.from(this))

        userId = intent.extras?.getString("userId").toString()
        userController = UserController(this)

        setUpActionButton()
        setUpRv()
        setContentView(binding.root)
    }

    private fun setUpRv() {
        val notificationAdapter = NotificationAdapter(ArrayList<String>(), this)
        userController.getNotifications(binding.notifRv, notificationAdapter, userId)
    }

    private fun setUpActionButton() {
        binding.btnBack.setOnClickListener {
            finishAndRemoveTask()
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
    }
}