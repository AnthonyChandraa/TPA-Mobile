package edu.bluejack22_1.beepark

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.bluejack22_1.beepark.R
import edu.bluejack22_1.beepark.databinding.ActivityDetailSpotBinding

class DetailSpotActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailSpotBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailSpotBinding.inflate(this.layoutInflater)

        binding.spotCode.text = intent.extras?.getString("spotCode")

        setContentView(binding.root)
    }
}