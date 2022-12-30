package edu.bluejack22_1.beepark.adminFragments

import android.os.Bundle
import edu.bluejack22_1.beepark.adminFragments.ParkingHistoryFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beepark.R
import edu.bluejack22_1.beepark.adapters.BookingAdapter
import edu.bluejack22_1.beepark.controllers.BookingController
import edu.bluejack22_1.beepark.controllers.ParkingSpotController
import edu.bluejack22_1.beepark.databinding.FragmentBookingHistoryBinding
import edu.bluejack22_1.beepark.databinding.FragmentParkingHistoryBinding
import edu.bluejack22_1.beepark.model.Booking
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [ParkingHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ParkingHistoryFragment : Fragment() {

    private lateinit var bookingController: BookingController
    private lateinit var parkingSpotController: ParkingSpotController
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter

    private lateinit var searchEt: EditText

    private lateinit var userId: String
    private var isAdmin: Boolean = false
    private var _binding: FragmentParkingHistoryBinding? = null
    private val binding get() = _binding!!

    private var isSet:Boolean = false
    private lateinit var bookings: Vector<Booking>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentParkingHistoryBinding.inflate(inflater, container, false)
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
        bookingAdapter = BookingAdapter(Vector<Booking>(), false, requireContext())

        bookingController = BookingController(requireContext())
        bookingController.setParkingHistoryBooking(userId, recyclerView, bookingAdapter)
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