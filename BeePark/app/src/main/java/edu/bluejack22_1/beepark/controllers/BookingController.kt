package edu.bluejack22_1.beepark.controllers

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beepark.adapters.BookingAdapter
import edu.bluejack22_1.beepark.model.Booking
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Vector

class BookingController(private var context: Context) {
    private var db = Firebase.firestore
    private var collectionRef = db.collection("Bookings")

    public fun setActiveBooking(userId: String, bookingsRv: RecyclerView, bookingAdapter: BookingAdapter){
        collectionRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, err ->
                if (err != null) {
                    Log.w("error Active Booking", "Listen failed.", err)
                    return@addSnapshotListener
                }

                var bookings = Vector<Booking>()
                for (doc in snapshot!!){

                    Log.w("data", "${doc.data}")
                    val data = doc.data
                    val endTime : Timestamp = data["endTime"] as Timestamp
                    val currTime = Timestamp.now()

                    if(currTime < endTime){
                        bookings.add(Booking(doc.id, data["spotCode"].toString(),
                            data["userId"].toString(), data["startTime"] as Timestamp,
                            data["endTime"] as Timestamp))
                    }

                }
                bookingAdapter.setBookings(bookings)
                bookingsRv.adapter = bookingAdapter
            }
    }
}