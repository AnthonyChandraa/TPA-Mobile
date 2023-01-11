package edu.bluejack22_1.beepark.controllers

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beepark.adapters.OvernightRequestAdapter
import edu.bluejack22_1.beepark.model.Booking
import edu.bluejack22_1.beepark.model.OvernightRequest
import edu.bluejack22_1.beepark.model.User
import java.text.SimpleDateFormat
import java.util.*

class OvernightRequestController (private var context: Context, var bookingController: BookingController) {
    private var db = Firebase.firestore
    private var collectionRef = db.collection("OvernightRequests")
    private var userController: UserController = UserController(context)

    fun insertNewRequest(
        userId: String,
        spotCode: String,
        startTimestamp: Timestamp,
        endTimestamp: Timestamp,
        reason: String
    ) {

        val newRequest = hashMapOf(
            "spotCode" to spotCode,
            "startTime" to startTimestamp,
            "endTime" to endTimestamp,
            "userId" to userId,
            "reason" to reason,
            "statusApproved" to false
        )

        collectionRef
            .add(newRequest)
            .addOnSuccessListener {

            }
            .addOnFailureListener {
                Log.w("Create request", "failed")
            }

    }

    fun updateApproval(requestId: String, startTimestamp: Timestamp, endTimestamp: Timestamp,
                       spotCode: String, userId: String, isApproved: Boolean) {
        if(isApproved) bookingController.insertOvernightBooking(startTimestamp, endTimestamp, spotCode, userId)

            var nowTimeStamp = Timestamp.now().seconds * 1000 + Timestamp.now().nanoseconds / 1000000
            var nowDate = Date(nowTimeStamp)
            var dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm aa")

            collectionRef.document(requestId)
                .delete()
                .addOnSuccessListener {
                    if(isApproved){
                        userController.insertNotification(userId, "Overnight Request",
                            "Approved", dateFormat.format(nowDate))
                    } else{
                        userController.insertNotification(userId, "Overnight Request",
                            "Declined", dateFormat.format(nowDate))
                    }
                }
                .addOnFailureListener {
                    Log.w("update request", "Failed")
                }
    }

    fun setUpRecycler(requestRv: RecyclerView, requestAdapter: OvernightRequestAdapter) {
        collectionRef
            .addSnapshotListener { snapshot, err ->
                if (err != null) {
                    Log.w("error Active Booking", "Listen failed.", err)
                    return@addSnapshotListener
                }

                var requests = Vector<OvernightRequest>()
                for (doc in snapshot!!){

                    val data = doc.data

                    requests.add(
                        OvernightRequest(doc.id, data["spotCode"].toString(),
                        data["userId"].toString(), data["startTime"] as Timestamp,
                        data["endTime"] as Timestamp, data["reason"].toString(),
                            data["statusApproved"].toString().toBoolean())
                    )

                }
                requestAdapter.setRequests(requests)
                requestAdapter.notifyDataSetChanged()
                requestRv.adapter = requestAdapter
            }
    }


}