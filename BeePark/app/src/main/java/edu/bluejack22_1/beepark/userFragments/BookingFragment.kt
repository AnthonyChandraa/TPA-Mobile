package edu.bluejack22_1.beepark.userFragments

import android.os.Bundle
import edu.bluejack22_1.beepark.userFragments.BookingFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.bluejack22_1.beepark.R
import edu.bluejack22_1.beepark.databinding.FragmentBookingBinding
import edu.bluejack22_1.beepark.databinding.FragmentBookingHistoryBinding

class BookingFragment : Fragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!

    private lateinit var userId: String
    private var isAdmin: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        val bundle = arguments

        isAdmin = bundle?.getBoolean("isAdmin").toString().toBoolean()
        userId = bundle?.getString("userId").toString()

        return binding.root
    }
}