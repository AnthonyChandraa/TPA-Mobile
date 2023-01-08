package edu.bluejack22_1.beepark.controllers

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.R
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beepark.model.Booking
import edu.bluejack22_1.beepark.model.Building
import edu.bluejack22_1.beepark.model.ParkingSpot
import java.util.ArrayList
import java.util.Vector

class BuildingController(var context: Context, var parkingSpotController: ParkingSpotController) {
    private var db = Firebase.firestore

    private fun setFloorDropdown(spinner: Spinner, size: Int, spotsRv: RecyclerView?, isAdmin: Boolean, buildingId: Int, userId: String, spinnerSpots: Spinner?, spot: ParkingSpot?){
        val floorList: MutableList<String> = ArrayList()

        var selectedPos : Int = 0
        for(i in 1..size){
            floorList.add("Floor $i")
            if( spot != null && spot.floorNumber == i)  selectedPos = i-1
        }

        val adapterDropdownFloor = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, floorList)
        spinner.adapter = adapterDropdownFloor
        spinner.setSelection(selectedPos)

        spinner.onItemSelectedListener = object :
        AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (spotsRv != null) {
                    spotsRv.adapter = null
                    parkingSpotController.setUpRecycler(spotsRv, p2+1, isAdmin, buildingId, userId)
                } else {
                    parkingSpotController.setUpSpinnerSpot(spinnerSpots, p2+1, buildingId, spot)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

    }

    public fun setBuildingsDropdown(spinnerBuilding: Spinner, spinnerFloor: Spinner, spotsRv: RecyclerView?, isAdmin: Boolean, userId: String, spinnerSpots: Spinner?, spot: ParkingSpot?) {
        var buildings = Vector<Building>()

        db.collection("Building")
            .get()
            .addOnSuccessListener {
                if (it.isEmpty){
                    Toast.makeText(context, "no task found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                for (document in it) {
                    var data = document.data
                    buildings.add(Building(document.id.toInt(), data["buildingName"].toString(), data.get("maxFloor").toString().toInt()))
                }

                val buildingList: MutableList<String> = ArrayList()

                var selectedPos: Int = 0

                for(i in 0 until buildings.size){
                    buildingList.add(buildings[i].buildingName)
                    if(spot != null && spot.buildingId == buildings[i].buildingId) selectedPos = i
                }

                spinnerBuilding.adapter = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, buildingList)

                spinnerBuilding.setSelection(selectedPos)

                spinnerBuilding.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            setFloorDropdown(spinnerFloor, buildings[p2].moxFloor, spotsRv, isAdmin, buildings[p2].buildingId, userId, spinnerSpots, spot)
                        }
                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("database building", "Error getting documents: ", exception)
            }
    }

    public fun setBuildingName(id:String,buildingTv:TextView){
        db.collection("Building")
            .document(id)
            .get()
            .addOnSuccessListener {
                document ->
                buildingTv.text = document.data?.get("buildingName").toString()
            }
    }

    public fun search(bookings: Vector<Booking>) {

    }

}