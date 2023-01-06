package edu.bluejack22_1.beepark.userFragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beepark.R
import edu.bluejack22_1.beepark.UIString.UiString
import edu.bluejack22_1.beepark.adapters.BookingAdapter
import edu.bluejack22_1.beepark.controllers.BookingController
import edu.bluejack22_1.beepark.controllers.ParkingSpotController
import edu.bluejack22_1.beepark.databinding.FragmentBookingHistoryBinding
import edu.bluejack22_1.beepark.model.Booking
import java.time.LocalDateTime
import java.util.*

class BookingHistoryFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var bookingController: BookingController
    private lateinit var parkingSpotController: ParkingSpotController
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter

    private lateinit var searchEt: EditText
    private lateinit var timePicker: TextView

    private lateinit var userId: String
    private var isAdmin: Boolean = false
    private var _binding: FragmentBookingHistoryBinding? = null
    private val binding get() = _binding!!

    private var isSet:Boolean = false
    private lateinit var bookings: Vector<Booking>

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookingHistoryBinding.inflate(inflater, container, false)
        val bundle = arguments

        isAdmin = bundle?.getBoolean("isAdmin").toString().toBoolean()
        userId = bundle?.getString("userId").toString()

        searchEt = binding.searchEt
        timePicker = binding.timePicker

        parkingSpotController = ParkingSpotController(requireContext())
        bookingController = BookingController(requireContext())

        setUpRecycler()
        setUpButton()
        setUpTimePicker()
        return binding.root
    }

    private fun setCurrDate(){
        var cal : Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    private fun setUpTimePicker() {

        timePicker.setOnClickListener(View.OnClickListener {
            setCurrDate()

            DatePickerDialog(requireContext(), this, year, month, day).show()
        })
    }

    private fun setUpRecycler(){
        recyclerView = binding.bookingRv
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        bookingAdapter = BookingAdapter(Vector<Booking>(), false, requireContext())

        bookingController.setHistoryBooking(userId, recyclerView, bookingAdapter)
    }

    private fun setUpButton(){
        binding.btnSearch.setOnClickListener(View.OnClickListener {
            val search = searchEt.text.toString()

            if(search.isNotEmpty()){
                if(!isSet) {
                    isSet = true
                    bookings = bookingAdapter.getBookings()
                }
                parkingSpotController.search(bookings, search, bookingAdapter)
            } else {
                isSet = false
                timePicker.text = UiString.StringResource(resId = R.string.filter_time).asString(requireContext())
                setUpRecycler()
            }

        })
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month + 1
        savedYear = year

        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        var dateTime = LocalDateTime.of(savedYear, savedMonth, savedDay, savedHour, savedMinute)

        timePicker.text = String.format("%02d/%02d/%04d %02d:%02d",
            savedDay, savedMonth, savedYear, savedHour, savedMinute)

        if(!isSet) {
            isSet = true
            bookings = bookingAdapter.getBookings()
        }
        bookingController.searchTime(bookings, dateTime, bookingAdapter)
    }

}