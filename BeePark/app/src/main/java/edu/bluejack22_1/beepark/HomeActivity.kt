package edu.bluejack22_1.beepark

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beepark.adapters.ParkingSpotsAdapter
import edu.bluejack22_1.beepark.controllers.BuildingController
import edu.bluejack22_1.beepark.controllers.ParkingSpotController
import edu.bluejack22_1.beepark.databinding.ActivityHomeBinding
import edu.bluejack22_1.beepark.model.ParkingSpot
import java.util.*

class HomeActivity : AppCompatActivity() {

    private var pressedTime = 0L
    private lateinit var binding: ActivityHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var parkingSpotsAdapter: ParkingSpotsAdapter
    private val isAdmin = true
    private lateinit var buildingController: BuildingController
    private lateinit var parkingSpotController: ParkingSpotController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(LayoutInflater.from(this))

        val btnProfile = binding.btnProfile
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

//       val contentFragmentTrans = supportFragmentManager.beginTransaction()
//      authorization user/admin
//        if(!isAdmin){
//            contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            contentFragmentTrans.add(R.id.contentFragment, userHomeFragment, "userHomeFragment").commitAllowingStateLoss()
//        } else {
//            contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            contentFragmentTrans.add(R.id.contentFragment, AdminHomeFragment(), "adminHomeFragment").commitAllowingStateLoss()
//        }

        setUpDropdownAndRv()

//        setUpAdapter()
//        setUpRecycler()

        setContentView(binding.root)
    }

    private fun setUpDropdownAndRv(){
        recyclerView = binding.parkingSpotsRv
        recyclerView.layoutManager = GridLayoutManager(this, 5)

        parkingSpotController = ParkingSpotController(this)
        buildingController = BuildingController(this, parkingSpotController)
        buildingController.setBuildingsDropdown(binding.dropdownBuilding, binding.dropdownFloor, recyclerView, isAdmin)
    }

//    private fun setUpAdapter(){
//        var dataParkingSpot = Vector<ParkingSpot>()
//        parkingSpotsAdapter = ParkingSpotsAdapter(dataParkingSpot, isAdmin ,this)
//    }

//    private fun setUpRecycler(){
//        recyclerView = binding.parkingSpotsRv
//        recyclerView.layoutManager = GridLayoutManager(this, 5)
//
//        recyclerView.adapter = parkingSpotsAdapter
//    }

    override fun onBackPressed() {
//        super.onBackPressed()
        if(pressedTime + 3000 > System.currentTimeMillis()){
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press one more to logout", Toast.LENGTH_SHORT).show()
        }

        pressedTime = System.currentTimeMillis()
    }
}