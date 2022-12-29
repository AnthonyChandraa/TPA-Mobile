package edu.bluejack22_1.beepark.controllers

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.R
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beepark.HomeActivity
import edu.bluejack22_1.beepark.model.Building
import java.util.ArrayList
import java.util.Vector

class BuildingController(var context: Context, var parkingSpotController: ParkingSpotController) {
    private var db = Firebase.firestore

    private fun setFloorDropdown(spinner: Spinner, size: Int, spotsRv: RecyclerView, isAdmin: Boolean, buildingId: Int){
        val floorList: MutableList<String> = ArrayList()

        for(i in 1..size){
            floorList.add("Floor $i")
        }

        val adapterDropdownFloor = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, floorList)
        spinner.adapter = adapterDropdownFloor

        spinner.onItemSelectedListener = object :
        AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spotsRv.adapter = null
                parkingSpotController.setUpRecycler(spotsRv, p2+1, isAdmin, buildingId)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

    }

    public fun setBuildingsDropdown(spinnerBuilding: Spinner, spinnerFloor: Spinner, spotsRv: RecyclerView, isAdmin: Boolean) {
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

                for(i in 0 until buildings.size){
                    buildingList.add(buildings[i].buildingName)
                }

                spinnerBuilding.adapter = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, buildingList)

                spinnerBuilding.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            setFloorDropdown(spinnerFloor, buildings[p2].moxFloor, spotsRv, isAdmin, buildings[p2].buildingId)
                        }
                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("database building", "Error getting documents: ", exception)
            }
    }

}