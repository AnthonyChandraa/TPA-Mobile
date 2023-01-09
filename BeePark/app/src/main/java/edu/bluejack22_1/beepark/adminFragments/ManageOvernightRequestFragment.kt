package edu.bluejack22_1.beepark.adminFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import edu.bluejack22_1.beepark.adapters.OvernightRequestAdapter
import edu.bluejack22_1.beepark.controllers.BookingController
import edu.bluejack22_1.beepark.controllers.OvernightRequestController
import edu.bluejack22_1.beepark.databinding.FragmentManageOvernightRequestBinding
import edu.bluejack22_1.beepark.model.OvernightRequest
import java.util.*

class ManageOvernightRequestFragment : Fragment() {

    private lateinit var bookingController: BookingController
    private lateinit var overnightRequestController: OvernightRequestController

    private lateinit var userId: String
    private var isAdmin: Boolean = false
    private var _binding: FragmentManageOvernightRequestBinding? = null

    private val binding get() = _binding!!
    private lateinit var overnightRequestAdapter: OvernightRequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentManageOvernightRequestBinding.inflate(inflater, container, false)

        val bundle = arguments

        isAdmin = bundle?.getBoolean("isAdmin").toString().toBoolean()
        userId = bundle?.getString("userId").toString()

        bookingController = BookingController(requireContext())
        overnightRequestController = OvernightRequestController(requireContext(), bookingController)

        setUpRv()
        return binding.root
    }

    private fun setUpRv() {
        var recyclerView = binding.requestRv
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        overnightRequestAdapter = OvernightRequestAdapter(Vector<OvernightRequest>(), requireContext(), userId)

        overnightRequestController.setUpRecycler(recyclerView, overnightRequestAdapter)
    }

}