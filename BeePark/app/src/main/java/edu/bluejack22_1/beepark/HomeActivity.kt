package edu.bluejack22_1.beepark

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.R
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beepark.adapters.ParkingSpotsAdapter
import edu.bluejack22_1.beepark.databinding.ActivityHomeBinding
import edu.bluejack22_1.beepark.model.ParkingSpot
import java.util.*

class HomeActivity : AppCompatActivity() {

    private var pressedTime = 0L
    private lateinit var binding: ActivityHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var parkingSpotsAdapter: ParkingSpotsAdapter
    private val isAdmin = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(LayoutInflater.from(this))

        val btnProfile = binding.btnProfile
        btnProfile.setOnClickListener {
            startActivity(Intent(this, RegisActivity::class.java))
        }

        val contentFragmentTrans = supportFragmentManager.beginTransaction()

        val bundle = Bundle()
        bundle.putBoolean("isAdmin", isAdmin)

        val userHomeFragment = UserHomeFragment()
        userHomeFragment.arguments = bundle

//      authorization user/admin
//        if(!isAdmin){
//            contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            contentFragmentTrans.add(R.id.contentFragment, userHomeFragment, "userHomeFragment").commitAllowingStateLoss()
//        } else {
//            contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            contentFragmentTrans.add(R.id.contentFragment, AdminHomeFragment(), "adminHomeFragment").commitAllowingStateLoss()
//        }

        // simpen data building
        val buildingList: MutableList<String> = ArrayList()

        // simpen data floor
        val floorList: MutableList<String> = ArrayList()

        for(i in 1..3){
            buildingList.add("Building $i")
        }

        for(i in 1..8){
            floorList.add("Floor $i")
        }

        val adapterDropdownBuilding = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, buildingList)

        val adapterDropdownFloor = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, floorList)


        binding.dropdownBuilding.adapter = adapterDropdownBuilding
        binding.dropdownFloor.adapter = adapterDropdownFloor

        setUpAdapter()
        setUpRecycler()

        setContentView(binding.root)
    }

    private fun setUpAdapter(){
        var dataParkingSpot = Vector<ParkingSpot>()

        dataParkingSpot.add(ParkingSpot("A01", 1, 1))
        dataParkingSpot.add(ParkingSpot("A02", 1, 1))
        dataParkingSpot.add(ParkingSpot("A03", 1, 1))
        dataParkingSpot.add(ParkingSpot("A04", 1, 1))
        dataParkingSpot.add(ParkingSpot("A05", 1, 1))
        dataParkingSpot.add(ParkingSpot("A06", 1, 1))
        dataParkingSpot.add(ParkingSpot("A07", 1, 1))
        parkingSpotsAdapter = ParkingSpotsAdapter(dataParkingSpot, isAdmin ,this)
    }

    private fun setUpRecycler(){

        recyclerView = binding.parkingSpotsRv

        recyclerView.layoutManager = GridLayoutManager(this, 5)
        recyclerView.adapter = parkingSpotsAdapter
    }

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