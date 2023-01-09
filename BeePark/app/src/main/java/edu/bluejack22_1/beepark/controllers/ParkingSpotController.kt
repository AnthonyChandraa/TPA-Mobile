package edu.bluejack22_1.beepark.controllers

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beepark.BookingActivity
import edu.bluejack22_1.beepark.R
import edu.bluejack22_1.beepark.ReportFeedbackActivity
import edu.bluejack22_1.beepark.UIString.UiString
import edu.bluejack22_1.beepark.adapters.BookingAdapter
import edu.bluejack22_1.beepark.adapters.ParkingSpotsAdapter
import edu.bluejack22_1.beepark.model.Booking
import edu.bluejack22_1.beepark.model.ParkingSpot
import java.util.ArrayList
import java.util.Vector

class ParkingSpotController(var context: Context) {
    private var db = Firebase.firestore
    private var buildingController = BuildingController(context, this)
    public fun setUpRecycler(spotsRv: RecyclerView, floorNumber: Int, isAdmin: Boolean, buildingId: Int, userId: String){
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

                    if((data["statusOpen"].toString().toBoolean() && !isAdmin) || isAdmin) {
                        dataParkingSpot.add(
                            ParkingSpot(
                                document.id,
                                data["buildingId"].toString().toInt(),
                                data["floorNumber"].toString().toInt(),
                                data["statusOpen"].toString().toBoolean()
                            )
                        )
                    }
                }

                spotsRv.adapter = ParkingSpotsAdapter(dataParkingSpot, isAdmin, userId, context)
            }
            .addOnFailureListener {
                Log.w("Error Database Spot", "Get data error")
            }
    }

    public fun setUpSpotDetail(spotCode:String, spotTv: TextView?, buildingTv: TextView?, floorTv: TextView?, isAdmin: Boolean, btnAction: Button?, userId: String){
        db.collection("ParkingSpot")
            .document(spotCode)
            .get()
            .addOnSuccessListener {
                document ->
                    if (buildingTv != null) {
                        buildingController.setBuildingName(document.data?.get("buildingId").toString(), buildingTv)
                    }
                    floorTv?.text = "Floor " + document.data?.get("floorNumber").toString()
                    spotTv?.text = "Spot $spotCode"

                if (btnAction != null) {
                    if(isAdmin){

                    } else{
                        btnAction.setOnClickListener(View.OnClickListener {
                            val intent = Intent(context, BookingActivity::class.java)
                            intent.putExtra("userId", userId)
                            intent.putExtra("spotCode", spotCode)
                            intent.putExtra("buildingId", document.data?.get("buildingId").toString().toInt())
                            intent.putExtra("floorNumber", document.data?.get("floorNumber").toString().toInt())
                            intent.putExtra("statusOpen", document.data?.get("statusOpen").toString().toBoolean())
                            context.startActivity(intent)
                        })
                    }

                }

            }



    }

    public fun setAdminButton(spotCode: String, button: Button){
        db.collection("ParkingSpot")
            .document(spotCode)
            .addSnapshotListener {
                    snapshot, e ->
                    val isOpen = snapshot?.data?.get("statusOpen").toString().toBoolean()
                    if(isOpen){
                        button.backgroundTintList = context.resources.getColorStateList(R.color.red_rose, context.theme)
                        button.text = UiString.StringResource(resId = R.string.close).asString(context)
                    } else {
                        button.backgroundTintList = context.resources.getColorStateList(R.color.blue_crayola, context.theme)
                        button.text = UiString.StringResource(resId = R.string.open).asString(context)
                    }
                    button.setOnClickListener(View.OnClickListener {
                        updateStatus(spotCode, isOpen)
                    })
            }
    }

    private fun updateStatus(spotCode: String, isOpen: Boolean){
        val mapUpdate = mapOf(
            "statusOpen" to !isOpen
        )

        db.collection("ParkingSpot")
            .document(spotCode)
            .update(mapUpdate)
            .addOnSuccessListener {
                Log.w("update status", "success")
            }
            .addOnFailureListener {
                Log.w("update status", "failed")
            }
    }

    fun search(bookings:Vector<Booking>, search: String, bookingAdapter: BookingAdapter) {
        var newVector = Vector<Booking>()
        bookingAdapter.setBookings(newVector)
        bookingAdapter.notifyDataSetChanged()
        for(booking in bookings){


            db.collection("ParkingSpot")
                .document(booking.spotCode)
                .get()
                .addOnSuccessListener{
                        document ->
                        val data = document.data
                        val floorString = "Floor " + data?.get("floorNumber").toString()
                        Log.w("floor", "${document.id}")
                    if (document != null) {
                        if(document.id.contains(search) || floorString.contains(search)
                            || data?.get("buildingName").toString().contains(search)){
                            newVector.add(booking)
                            bookingAdapter.setBookings(newVector)
                            bookingAdapter.notifyDataSetChanged()
                        }
                    }
                }
        }
    }

    fun setUpSpinnerSpot(spinnerSpots: Spinner?, floorNumber: Int, buildingId: Int, spot: ParkingSpot?) {
        var dataParkingSpot : MutableList<String> = ArrayList()

        var selectedPos: Int = 0
        var i: Int = 0
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

                    if(data?.get("statusOpen").toString().toBoolean()) dataParkingSpot.add(document.id)

                    if ( spot != null && spot.spotCode == document.id) {
                        selectedPos = i
                    }

                    i++
                }

                spinnerSpots?.adapter = ArrayAdapter(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, dataParkingSpot)
                spinnerSpots?.setSelection(selectedPos)
            }
            .addOnFailureListener {
                Log.w("Error Database Spot", "Get data error")
            }
    }

    fun openUpdateActivity(booking: Booking, userId: String) {
        db.collection("ParkingSpot")
            .document(booking.spotCode)
            .get()
            .addOnSuccessListener {
                    document ->

                val data = document.data
                val intent = Intent(context, BookingActivity::class.java)
                intent.putExtra("userId", userId)
                intent.putExtra("isUpdate", true)
                intent.putExtra("bookingId", booking.bookingId)
                intent.putExtra("spotCode", booking.spotCode)
                intent.putExtra("buildingId", data?.get("buildingId").toString().toInt())
                intent.putExtra("floorNumber", data?.get("floorNumber").toString().toInt())
                intent.putExtra("statusOpen", data?.get("statusOpen").toString().toBoolean())

                context.startActivity(intent)

            }
            .addOnFailureListener {
                Log.w("failed", "failed load Update")
            }


    }
}