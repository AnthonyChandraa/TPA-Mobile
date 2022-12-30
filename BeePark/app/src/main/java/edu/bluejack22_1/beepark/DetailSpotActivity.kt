package edu.bluejack22_1.beepark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import edu.bluejack22_1.beepark.UIString.UiString
import edu.bluejack22_1.beepark.controllers.ParkingSpotController
import edu.bluejack22_1.beepark.databinding.ActivityDetailSpotBinding

class DetailSpotActivity : AppCompatActivity() {

    private lateinit var parkingSpotController: ParkingSpotController
    private lateinit var binding: ActivityDetailSpotBinding
    private var isAdmin: Boolean = false
    private lateinit var userId: String
    private lateinit var spotCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailSpotBinding.inflate(this.layoutInflater)

        spotCode = intent.extras?.getString("spotCode").toString()
        isAdmin = intent.extras?.getBoolean("isAdmin").toString().toBoolean()
        userId = intent.extras?.getString("userId").toString()

        binding.spotCode.text = spotCode

        parkingSpotController = ParkingSpotController(this)
        parkingSpotController.setUpSpotDetail(spotCode, binding.spotCode, binding.buildingTv, binding.floorTv)

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

        if(isAdmin){
            binding.btnReportOrFeedback.visibility = View.INVISIBLE

//            action button
            parkingSpotController.setAdminButton(spotCode, binding.btnAction)

        } else {
            binding.btnReportOrFeedback.visibility = View.VISIBLE
            binding.btnReportOrFeedback.setOnClickListener(View.OnClickListener {
                val intent = Intent(this, ReportFeedbackActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
            })
            binding.btnAction.text = UiString.StringResource(resId = R.string.book).asString(this)
        }
    }
}