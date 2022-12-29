package edu.bluejack22_1.beepark.controllers

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beepark.adapters.ParkingSpotsAdapter
import edu.bluejack22_1.beepark.model.Building
import edu.bluejack22_1.beepark.model.ParkingSpot
import java.util.Vector

class ParkingSpotController(var context: Context) {
    private var db = Firebase.firestore

    public fun setUpRecycler(spotsRv: RecyclerView, floorNumber: Int, isAdmin: Boolean, buildingId: Int){
        var dataParkingSpot = Vector<ParkingSpot>()

        db.collection("ParkingSpot")
            .whereEqualTo("floorNumber", floorNumber)
            .whereEqualTo("buildingId", buildingId)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty){
                    Toast.makeText(context, "Empty Spots", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                for (document in it) {
                    var data = document.data
                    dataParkingSpot.add(ParkingSpot(document.id, data["buildingId"].toString().toInt(),
                            data["floorNumber"].toString().toInt(), data["statusOpen"].toString().toBoolean()))
                }

                spotsRv.adapter = ParkingSpotsAdapter(dataParkingSpot, isAdmin ,context)
            }
            .addOnFailureListener {
                Log.w("Error Database Spot", "Get data error")
            }
    }
}