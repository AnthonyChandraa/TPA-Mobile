package edu.bluejack22_1.beepark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.Timestamp
import edu.bluejack22_1.beepark.UIString.UiString
import edu.bluejack22_1.beepark.controllers.UserController
import edu.bluejack22_1.beepark.databinding.ActivityReportFeedbackBinding
import java.text.SimpleDateFormat
import java.util.*

class ReportFeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportFeedbackBinding
    private lateinit var userId: String
    private lateinit var reportText: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportFeedbackBinding.inflate(this.layoutInflater)

        userId = intent.extras?.getString("userId").toString()
        setUpButton()
        setUpDropdown()
        reportHandler()
        setContentView(binding.root)
    }

    private fun setUpDropdown(){
        val reportOrFeedback: MutableList<String> = ArrayList()

        var selectedPos: Int = 0

        reportOrFeedback.add("Report")
        reportOrFeedback.add("Feedback")

        binding.dropdownReportOrFeedback.adapter = ArrayAdapter(this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, reportOrFeedback)


    }

    private fun reportHandler() {
        var userC = UserController(this)

        binding.btnSubmitReport.setOnClickListener{
            reportText = binding.etTextReport.text.toString()

            if(reportText.isEmpty()){
                Toast.makeText(baseContext, UiString.StringResource(resId = R.string.errorEmpty).asString(this), Toast.LENGTH_SHORT).show()

            }else{
//                val calendar = Calendar.getInstance()
//                calendar.set(Calendar.YEAR, Calendar.YEAR)
//                calendar.set(Calendar.MONTH,Calendar.MONTH)
//                calendar.set(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_MONTH)
                var type = binding.dropdownReportOrFeedback.selectedItem.toString()
                var nowTimeStamp = Timestamp.now().seconds * 1000 + Timestamp.now().nanoseconds / 1000000
                var nowDate = Date(nowTimeStamp)
                var dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm aa")
                userC.insertFeedback(userId, type, reportText, dateFormat.format(nowDate))
            }


        }
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