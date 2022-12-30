package edu.bluejack22_1.beepark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import edu.bluejack22_1.beepark.R
import edu.bluejack22_1.beepark.databinding.ActivityBookingBinding

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(this.layoutInflater)

        userId = intent.extras?.getString("userId").toString()

        setUpButton()

        setContentView(binding.root)
    }

    private fun setUpButton(){
        binding.btnBack.setOnClickListener(View.OnClickListener {
            finishAndRemoveTask()
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        })
    }
}