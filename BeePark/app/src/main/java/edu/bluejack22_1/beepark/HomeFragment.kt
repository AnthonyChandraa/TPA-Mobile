package edu.bluejack22_1.beepark

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beepark.controllers.BuildingController
import edu.bluejack22_1.beepark.controllers.ParkingSpotController
import edu.bluejack22_1.beepark.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var buildingController: BuildingController
    private lateinit var parkingSpotController: ParkingSpotController
    private var isAdmin : Boolean = false
    private lateinit var userId :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val bundle = arguments

        isAdmin = bundle?.getBoolean("isAdmin").toString().toBoolean()
        userId = bundle?.getString("userId").toString()

        setUpButtonAction()
        setUpDropdownAndRv()
        return binding.root;
    }

    private fun setUpButtonAction() {

        if(isAdmin){
            binding.btnBookParking.visibility = View.INVISIBLE
        } else {
            binding.btnBookParking.visibility = View.VISIBLE
        }

        binding.btnBookParking.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), BookingActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        })
    }


    private fun setUpDropdownAndRv(){
        recyclerView = binding.parkingSpotsRv
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 5)

        parkingSpotController = ParkingSpotController(requireContext())
        buildingController = BuildingController(requireContext(), parkingSpotController)
        buildingController.setBuildingsDropdown(binding.dropdownBuilding, binding.dropdownFloor, recyclerView, isAdmin, userId, null, null)
    }

}