package edu.bluejack22_1.beepark

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import edu.bluejack22_1.beepark.UIString.UiString
import edu.bluejack22_1.beepark.controllers.BookingController
import edu.bluejack22_1.beepark.controllers.BuildingController
import edu.bluejack22_1.beepark.controllers.ParkingSpotController
import edu.bluejack22_1.beepark.databinding.ActivityBookingBinding
import edu.bluejack22_1.beepark.model.ParkingSpot
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*

class BookingActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: ActivityBookingBinding

    private lateinit var buildingController: BuildingController
    private lateinit var parkingSpotController: ParkingSpotController
    private lateinit var bookingController: BookingController

    private lateinit var userId: String
    private var spot: ParkingSpot? = null

    private var isUpdate: Boolean = false
    private lateinit var bookingId : String

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var isStart : Boolean = false

    var savedDay = -1
    var savedMonth = -1
    var savedYear = -1
    var savedStartHour = -1
    var savedEndHour = -1
    var savedStartMinute = -1
    var savedEndMinute = -1

    var inputDate: Date? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(this.layoutInflater)

        userId = intent.extras?.getString("userId").toString()
        var spotCode: String = intent.extras?.getString("spotCode").toString()
        var buildingId: Int = intent.extras?.getInt("buildingId").toString().toInt()
        var floorNumber: Int = intent.extras?.getInt("floorNumber").toString().toInt()
        var openStatus: Boolean = intent.extras?.getBoolean("statusOpen").toString().toBoolean()

        isUpdate = intent.extras?.getBoolean("isUpdate").toString().toBoolean()
        bookingId = intent.extras?.getString("bookingId").toString()

        if(spotCode != null){
            spot = ParkingSpot(spotCode, buildingId, floorNumber, openStatus)
        }

        parkingSpotController = ParkingSpotController(this)
        buildingController = BuildingController(this, parkingSpotController)
        bookingController = BookingController(this)

        setUpButton()
        setUpDropdowns()
        setUpPicker()

        setContentView(binding.root)
    }

    private fun setCurrDate(){
        var cal : Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    private fun setUpPicker() {
        setCurrDate()

        binding.datePicker.setOnClickListener(View.OnClickListener {
            DatePickerDialog(this, this, year, month, day).show()
        })

        binding.startTimePicker.setOnClickListener(View.OnClickListener {
            isStart = true
            TimePickerDialog(this, this, hour, minute, true).show()
        })

        binding.endTimePicker.setOnClickListener(View.OnClickListener {
            isStart = false
            TimePickerDialog(this, this, hour, minute, true).show()
        })
    }

    private fun setUpButton(){
        binding.btnBack.setOnClickListener(View.OnClickListener {
            finishAndRemoveTask()

            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("fragment", "home")
            startActivity(intent)
        })

        if(isUpdate){
            binding.btnBook.text = UiString.StringResource(R.string.update).asString(this)
            binding.btnBook.backgroundTintList = this.resources.getColorStateList(R.color.blue_crayola, this.theme)
        } else {
            binding.btnBook.text = UiString.StringResource(R.string.book).asString(this)
            binding.btnBook.backgroundTintList = this.resources.getColorStateList(R.color.persian_green, this.theme)

        }

        binding.btnBook.setOnClickListener(View.OnClickListener {
            bookAction()
        })
    }

    private fun bookAction(){

        if(savedDay == -1 || savedStartHour == -1 || savedEndHour == -1){
            binding.errorTv.text = UiString.StringResource(resId = R.string.errorEmpty).asString(this)
            return
        }

        val currDate = LocalDate.now()
        val inputDate = LocalDate.of(savedYear, savedMonth+1, savedDay)

        if(currDate > inputDate){
            binding.errorTv.text = UiString.StringResource(resId = R.string.invalid_date_before).asString(this)
            return
        }

        val period = Period.between(currDate, inputDate)

        if(period.days > 10){
            binding.errorTv.text = UiString.StringResource(resId = R.string.invalid_date_more).asString(this)
            return
        }

        if(savedStartHour < 4 || savedEndHour > 23 || savedStartHour > 23 || savedEndHour < 4){
            binding.errorTv.text = UiString.StringResource(resId = R.string.invalid_book_time).asString(this)
            return
        }else if(savedEndHour >= 23 && savedEndMinute > 0){
            binding.errorTv.text = UiString.StringResource(resId = R.string.invalid_book_time).asString(this)
            return
        }


        val startCalendar = Calendar.getInstance()
        startCalendar.set(Calendar.YEAR, savedYear)
        startCalendar.set(Calendar.MONTH, savedMonth)
        startCalendar.set(Calendar.DAY_OF_MONTH, savedDay)
        startCalendar.set(Calendar.HOUR_OF_DAY, savedStartHour)
        startCalendar.set(Calendar.MINUTE, savedStartMinute)

        val endCalendar = Calendar.getInstance()
        endCalendar.set(Calendar.YEAR, savedYear)
        endCalendar.set(Calendar.MONTH, savedMonth)
        endCalendar.set(Calendar.DAY_OF_MONTH, savedDay)
        endCalendar.set(Calendar.HOUR_OF_DAY, savedEndHour)
        endCalendar.set(Calendar.MINUTE, savedEndMinute)

        bookingController.bookParkingSpot(startCalendar.time, endCalendar.time,
            binding.dropdownParkSpot.selectedItem.toString(), userId, binding.errorTv, isUpdate, bookingId)
    }

    private fun setUpDropdowns(){
        buildingController.setBuildingsDropdown(binding.dropdownBuilding,
            binding.dropdownFloor, null, false, userId, binding.dropdownParkSpot, spot)
    }


    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, savedYear)
        calendar.set(Calendar.MONTH, savedMonth)
        calendar.set(Calendar.DAY_OF_MONTH, savedDay)

        inputDate = calendar.time

        val dateFormat = SimpleDateFormat("MM/dd/yyyy")

        binding.datePicker.text = dateFormat.format(inputDate)
    }

    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        val timeFormat = SimpleDateFormat("hh:mm aa")

        if(isStart){
            savedStartHour = hourOfDay
            savedStartMinute = minute

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, savedStartHour)
            calendar.set(Calendar.MINUTE, savedStartMinute)

            val startTime = timeFormat.format(calendar.time)
            binding.startTimePicker.text = startTime

        } else {
            savedEndHour = hourOfDay
            savedEndMinute = minute

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, savedEndHour)
            calendar.set(Calendar.MINUTE, savedEndMinute)

            val endTime = timeFormat.format(calendar.time)
            binding.endTimePicker.text = endTime
        }

    }
}