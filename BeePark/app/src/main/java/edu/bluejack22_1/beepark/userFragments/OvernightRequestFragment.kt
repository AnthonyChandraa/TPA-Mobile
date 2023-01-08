package edu.bluejack22_1.beepark.userFragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack22_1.beepark.R
import edu.bluejack22_1.beepark.UIString.UiString
import edu.bluejack22_1.beepark.adapters.BookingAdapter
import edu.bluejack22_1.beepark.controllers.BookingController
import edu.bluejack22_1.beepark.controllers.BuildingController
import edu.bluejack22_1.beepark.controllers.ParkingSpotController
import edu.bluejack22_1.beepark.databinding.FragmentOvernightRequestBinding
import edu.bluejack22_1.beepark.model.Booking
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.Period
import java.util.*

class OvernightRequestFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var bookingController: BookingController
    private lateinit var parkingSpotController: ParkingSpotController
    private lateinit var buildingController: BuildingController

    private lateinit var userId: String
    private var isAdmin: Boolean = false
    private var _binding: FragmentOvernightRequestBinding? = null
    private val binding get() = _binding!!

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedStartDay = -1
    var savedStartMonth = -1
    var savedStartYear = -1
    var savedStartHour = -1
    var savedStartMinute = -1

    var savedEndDay = -1
    var savedEndMonth = -1
    var savedEndYear = -1
    var savedEndHour = -1
    var savedEndMinute = -1

    private var isStart:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOvernightRequestBinding.inflate(inflater, container, false)

        val bundle = arguments

        isAdmin = bundle?.getBoolean("isAdmin").toString().toBoolean()
        userId = bundle?.getString("userId").toString()

        bookingController = BookingController(requireContext())
        parkingSpotController = ParkingSpotController(requireContext())
        buildingController = BuildingController(requireContext(), parkingSpotController)

        setUpPicker()
        setUpButton()
        setUpDropdowns()
        return  binding.root
    }

    private fun setUpButton() {
        binding.btnRequest.setOnClickListener(View.OnClickListener {
            requestAction()
        })
    }

    private fun requestAction(){

        if(savedStartHour == -1 || savedEndHour == -1 || binding.reasonEt.text.isEmpty()){
            binding.errorTv.text = UiString.StringResource(resId = R.string.errorEmpty).asString(requireContext())
            return
        }

        val currDate = LocalDate.now()
        val inputDate = LocalDate.of(savedStartYear, savedStartMonth+1, savedStartDay)

        if(currDate >= inputDate){
            binding.errorTv.text = UiString.StringResource(resId = R.string.invalid_request_date).asString(requireContext())
            return
        }

        val startCalendar = Calendar.getInstance()
        startCalendar.set(Calendar.YEAR, savedStartYear)
        startCalendar.set(Calendar.MONTH, savedStartMonth)
        startCalendar.set(Calendar.DAY_OF_MONTH, savedStartDay)
        startCalendar.set(Calendar.HOUR_OF_DAY, savedStartHour)
        startCalendar.set(Calendar.MINUTE, savedStartMinute)

        val endCalendar = Calendar.getInstance()
        endCalendar.set(Calendar.YEAR, savedEndYear)
        endCalendar.set(Calendar.MONTH, savedEndMonth)
        endCalendar.set(Calendar.DAY_OF_MONTH, savedEndDay)
        endCalendar.set(Calendar.HOUR_OF_DAY, savedEndHour)
        endCalendar.set(Calendar.MINUTE, savedEndMinute)

        var duration = Duration.between(startCalendar.time.toInstant(), endCalendar.time.toInstant())
        var rangeHours = duration.toHours().toInt()
        var rangeMinute = duration.toMinutes().toInt()

        if(rangeHours > 24 || (rangeHours == 24 && rangeMinute > 0)){
            binding.errorTv.text = UiString.StringResource(resId = R.string.invalid_request_range).asString(requireContext())
            return
        }

        bookingController.addOvernightRequest(startCalendar.time, endCalendar.time,
            binding.dropdownParkSpot.selectedItem.toString(), userId, binding.errorTv, binding.reasonEt.text.toString())
    }

    private fun setUpDropdowns(){
        buildingController.setBuildingsDropdown(binding.dropdownBuilding,
            binding.dropdownFloor, null, false, userId, binding.dropdownParkSpot, null)
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

        binding.startTimePicker.setOnClickListener(View.OnClickListener {
            isStart = true
            DatePickerDialog(requireContext(), this, year, month, day).show()
        })

        binding.endTimePicker.setOnClickListener(View.OnClickListener {
            isStart = false
            DatePickerDialog(requireContext(), this, year, month, day).show()
        })
    }


    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        if(isStart){
            savedStartDay = dayOfMonth
            savedStartMonth = month
            savedStartYear = year
        } else{
            savedEndDay = dayOfMonth
            savedEndMonth = month
            savedEndYear = year
        }

        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm aa")

        val calendar = Calendar.getInstance()


        if(isStart){
            savedStartHour = hourOfDay
            savedStartMinute = minute

            calendar.set(Calendar.YEAR, savedStartYear)
            calendar.set(Calendar.MONTH, savedStartMonth)
            calendar.set(Calendar.DAY_OF_MONTH, savedStartDay)
            calendar.set(Calendar.HOUR_OF_DAY, savedStartHour)
            calendar.set(Calendar.MINUTE, savedStartMinute)

            val startTime = dateFormat.format(calendar.time)
            binding.startTimePicker.text = startTime

        } else {
            savedEndHour = hourOfDay
            savedEndMinute = minute

            calendar.set(Calendar.YEAR, savedEndYear)
            calendar.set(Calendar.MONTH, savedEndMonth)
            calendar.set(Calendar.DAY_OF_MONTH, savedEndDay)
            calendar.set(Calendar.HOUR_OF_DAY, savedEndHour)
            calendar.set(Calendar.MINUTE, savedEndMinute)

            val endTime = dateFormat.format(calendar.time)
            binding.endTimePicker.text = endTime
        }


    }
}