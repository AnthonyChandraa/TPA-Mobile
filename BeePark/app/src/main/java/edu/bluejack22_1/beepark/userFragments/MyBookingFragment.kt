package edu.bluejack22_1.beepark.userFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beepark.adapters.BookingAdapter
import edu.bluejack22_1.beepark.controllers.BookingController
import edu.bluejack22_1.beepark.controllers.BuildingController
import edu.bluejack22_1.beepark.controllers.ParkingSpotController
import edu.bluejack22_1.beepark.databinding.FragmentMyBookingBinding
import edu.bluejack22_1.beepark.model.Booking
import java.util.Vector

class MyBookingFragment : Fragment() {

    private lateinit var bookingController: BookingController
    private lateinit var parkingSpotController: ParkingSpotController
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter

    private lateinit var searchEt: EditText

    private lateinit var userId: String
    private var isAdmin: Boolean = false
    private var _binding: FragmentMyBookingBinding? = null
    private val binding get() = _binding!!

    private var isSet:Boolean = false
    private lateinit var bookings: Vector<Booking>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMyBookingBinding.inflate(inflater, container, false)
        val bundle = arguments

        isAdmin = bundle?.getBoolean("isAdmin").toString().toBoolean()
        userId = bundle?.getString("userId").toString()

        searchEt = binding.searchEt

        parkingSpotController = ParkingSpotController(requireContext())

        setUpRecycler()
        setUpButton()
        return binding.root
    }

    private fun setUpRecycler(){
        recyclerView = binding.bookingRv
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        bookingAdapter = BookingAdapter(Vector<Booking>(), requireContext())

        bookingController = BookingController(requireContext())
        bookingController.setActiveBooking(userId, recyclerView, bookingAdapter)
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
                setUpRecycler()
            }

        })
    }
}