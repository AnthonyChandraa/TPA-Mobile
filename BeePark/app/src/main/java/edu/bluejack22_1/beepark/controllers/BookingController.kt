package edu.bluejack22_1.beepark.controllers

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beepark.adapters.BookingAdapter
import edu.bluejack22_1.beepark.model.Booking
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
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

    public fun setHistoryBooking(userId: String, bookingsRv: RecyclerView, bookingAdapter: BookingAdapter){
        collectionRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, err ->
                if (err != null) {
                    Log.w("error Active Booking", "Listen failed.", err)
                    return@addSnapshotListener
                }

                var bookings = Vector<Booking>()
                for (doc in snapshot!!){

                    val data = doc.data
                    val endTime : Timestamp = data["endTime"] as Timestamp
                    val currTime = Timestamp.now()

                    if(currTime >= endTime){
                        bookings.add(Booking(doc.id, data["spotCode"].toString(),
                            data["userId"].toString(), data["startTime"] as Timestamp,
                            data["endTime"] as Timestamp))
                    }

                }
                bookingAdapter.setBookings(bookings)
                bookingsRv.adapter = bookingAdapter
            }
    }

    public fun setParkingHistoryBooking(userId: String, bookingsRv: RecyclerView, bookingAdapter: BookingAdapter){
        collectionRef
            .addSnapshotListener { snapshot, err ->
                if (err != null) {
                    Log.w("error Active Booking", "Listen failed.", err)
                    return@addSnapshotListener
                }

                var bookings = Vector<Booking>()
                for (doc in snapshot!!){

                    val data = doc.data

                    bookings.add(Booking(doc.id, data["spotCode"].toString(),
                        data["userId"].toString(), data["startTime"] as Timestamp,
                        data["endTime"] as Timestamp))


                }
                bookingAdapter.setBookings(bookings)
                bookingsRv.adapter = bookingAdapter
            }
    }

    fun searchTime(bookings:Vector<Booking>, dateTime: LocalDateTime, bookingAdapter: BookingAdapter) {
        var newVector = Vector<Booking>()
        bookingAdapter.setBookings(newVector)
        bookingAdapter.notifyDataSetChanged()

        var searchTime = java.sql.Timestamp(dateTime.toEpochSecond(ZoneId.systemDefault().rules.getOffset(
            Instant.now())))


        for(booking in bookings){
            var startTimeMs = (booking.startTime.seconds * 1000 + booking.startTime.nanoseconds / 1000000)/1000
            var endTimeMs = (booking.endTime.seconds * 1000 + booking.endTime.nanoseconds / 1000000)/1000
            var searchTimeMs = (Timestamp(searchTime).seconds * 1000 + Timestamp(searchTime).nanoseconds / 1000000)
            Log.w("TimeStamp", "$searchTimeMs $startTimeMs $endTimeMs")
            if(searchTimeMs == startTimeMs || searchTimeMs == endTimeMs){
                newVector.add(booking)
                bookingAdapter.setBookings(newVector)
                bookingAdapter.notifyDataSetChanged()
            }
        }
    }
}