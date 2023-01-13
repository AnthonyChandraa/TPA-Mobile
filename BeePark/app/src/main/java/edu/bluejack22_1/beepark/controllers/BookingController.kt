package edu.bluejack22_1.beepark.controllers

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beepark.HomeActivity
import edu.bluejack22_1.beepark.R
import edu.bluejack22_1.beepark.UIString.UiString
import edu.bluejack22_1.beepark.adapters.BookingAdapter
import edu.bluejack22_1.beepark.adapters.NotificationAdapter
import edu.bluejack22_1.beepark.model.Booking
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.Date
import java.util.Vector

class BookingController(private var context: Context) {
    private var db = Firebase.firestore
    private var collectionRef = db.collection("Bookings")

    private var overnightRequestController = OvernightRequestController(context, this)

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
                            data["endTime"] as Timestamp, data["type"].toString()))
                    }

                }
                bookingAdapter.setBookings(bookings)
                bookingAdapter.notifyDataSetChanged()
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
                            data["endTime"] as Timestamp, data["type"].toString()))
                    }

                }
                bookingAdapter.setBookings(bookings)
                bookingAdapter.notifyDataSetChanged()
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
                        data["endTime"] as Timestamp, data["type"].toString()))


                }
                bookingAdapter.setBookings(bookings)
                bookingAdapter.notifyDataSetChanged()
                bookingsRv.adapter = bookingAdapter
            }
    }

    fun searchTime(bookings:Vector<Booking>, dateTime: Date, bookingAdapter: BookingAdapter) {
        var newVector = Vector<Booking>()
        bookingAdapter.setBookings(newVector)
        bookingAdapter.notifyDataSetChanged()

        var searchDate = Timestamp(dateTime.toInstant().epochSecond, dateTime.toInstant().nano)


        for(booking in bookings){
//            var startTimeMs = (booking.startTime.seconds * 1000 + booking.startTime.nanoseconds / 1000000)
//            var endTimeMs = (booking.endTime.seconds * 1000 + booking.endTime.nanoseconds / 1000000)
//            var searchTimeMs = (Timestamp(searchTime).seconds * 1000 + Timestamp(searchTime).nanoseconds / 1000000)
            Log.w("TimeStamp", "$searchDate ${booking.startTime} ${booking.endTime}")
            if(booking.startTime <= searchDate && booking.endTime >= searchDate){
                newVector.add(booking)
                bookingAdapter.setBookings(newVector)
                bookingAdapter.notifyDataSetChanged()
            }
        }
    }

    fun bookParkingSpot(inputStartTime: Date, inputEndTime: Date, spotCode: String, userId: String, errorTv: TextView,
                        isUpdate:Boolean, bookingId: String?){

        var startTimestamp = Timestamp(inputStartTime.toInstant().epochSecond, inputStartTime.toInstant().nano)
        var endTimestamp = Timestamp(inputEndTime.toInstant().epochSecond, inputEndTime.toInstant().nano)

        collectionRef
            .whereEqualTo("spotCode", spotCode)
            .get()
            .addOnSuccessListener{ documents ->

                var valid = true

                if(!documents.isEmpty){
                    for (doc in documents!!){

                        if(isUpdate && doc.id == bookingId){
                            continue
                        }

                        val data = doc.data
                        val startTime : Timestamp = data["startTime"] as Timestamp
                        val endTime : Timestamp = data["endTime"] as Timestamp

                        var startTimeMs = startTime.seconds * 1000 + startTime.nanoseconds / 1000000
                        var startNetDate = Date(startTimeMs)

                        var range = Duration.between(startNetDate.toInstant(), inputStartTime.toInstant()).toDays().toInt()

                        if(range == 0 && data["userId"].toString() == userId){
                            errorTv.text = UiString.StringResource(resId = R.string.errorBookOneDay).asString(context)
                            valid = false
                            break
                        } else if((startTimestamp > startTime && startTimestamp < endTime) ||
                            (endTimestamp > startTime && endTimestamp < endTime)||
                            (startTime > startTimestamp && startTime < endTimestamp) ||
                            (endTime > startTimestamp && endTime < endTimestamp)){
                            errorTv.text = UiString.StringResource(resId = R.string.errorNotAvail).asString(context)
                            valid = false
                            break
                        }
                    }
                }


                if(valid) {

                    if(!isUpdate){
                        insertNewBook(spotCode, startTimestamp, endTimestamp, userId)
                    } else{
                        updateBook(spotCode, startTimestamp, endTimestamp, userId, bookingId)
                    }
                }
            }
    }

    private fun insertNewBook(spotCode: String, startTime: Timestamp, endTime: Timestamp, userId: String){

        val intent = Intent(context, HomeActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("fragment", "home")
        context.startActivity(intent)

        val newBooking = hashMapOf(
            "spotCode" to spotCode,
            "startTime" to startTime,
            "endTime" to endTime,
            "userId" to userId,
            "type" to "Normal"
        )

        db.collection("Bookings")
            .add(newBooking)
            .addOnSuccessListener {
                Log.w("create User", "Success")
            }
            .addOnFailureListener {
                Log.w("Create User", "failed")
            }
    }

    private fun updateBook(spotCode: String, startTime: Timestamp, endTime: Timestamp, userId: String, bookingId: String?){
        if (bookingId != null) {
            db.collection("Bookings").document(bookingId)
                .update(mapOf(
                    "spotCode" to spotCode,
                    "startTime" to startTime,
                    "endTime" to endTime,
                    "userId" to userId
                ))
                .addOnSuccessListener {

                    val intent = Intent(context, HomeActivity::class.java)
                    intent.putExtra("userId", userId)
                    intent.putExtra("fragment", "myBooking")
                    context.startActivity(intent)
                }

        }
    }

    fun deleteBooking(booking: Booking) {
        db.collection("Bookings").document(booking.bookingId)
            .delete()
            .addOnSuccessListener {
                Log.w("Delete Booking", "Success")
            }
            .addOnFailureListener {
                Log.w("Delete Booking", "Failed")
            }
    }

    fun addOvernightRequest(inputStartTime: Date, inputEndTime: Date, spotCode: String, userId: String, errorTv: TextView
                            , reason: String){

        var startTimestamp = Timestamp(inputStartTime.toInstant().epochSecond, inputStartTime.toInstant().nano)
        var endTimestamp = Timestamp(inputEndTime.toInstant().epochSecond, inputEndTime.toInstant().nano)

        collectionRef
            .whereEqualTo("spotCode", spotCode)
            .get()
            .addOnSuccessListener{ documents ->

                var valid = true

                if(!documents.isEmpty){
                    for (doc in documents!!){

                        val data = doc.data
                        val startTime : Timestamp = data["startTime"] as Timestamp
                        val endTime : Timestamp = data["endTime"] as Timestamp

                        var startTimeMs = startTime.seconds * 1000 + startTime.nanoseconds / 1000000
                        var startNetDate = Date(startTimeMs)

                        var range = Duration.between(startNetDate.toInstant(), inputStartTime.toInstant()).toDays().toInt()

                        if((startTimestamp > startTime && startTimestamp < endTime) ||
                            (endTimestamp > startTime && endTimestamp < endTime) ||
                            (startTime > startTimestamp && startTime < endTimestamp) ||
                            (endTime > startTimestamp && endTime < endTimestamp)){
                            errorTv.text = UiString.StringResource(resId = R.string.errorNotAvail).asString(context)
                            valid = false
                            break
                        }
                    }
                }


                if(valid) {
                    errorTv.text = ""

                    val intent = Intent(context, HomeActivity::class.java)
                    intent.putExtra("userId", userId)
                    intent.putExtra("fragment", "home")
                    context.startActivity(intent)

                    overnightRequestController.insertNewRequest(userId, spotCode, startTimestamp, endTimestamp, reason)

                }
            }
    }

    fun insertOvernightBooking(startTimestamp: Timestamp, endTimestamp: Timestamp, spotCode: String, userId: String) {
        val newBooking = hashMapOf(
            "spotCode" to spotCode,
            "startTime" to startTimestamp,
            "endTime" to endTimestamp,
            "userId" to userId,
            "type" to "Overnight"
        )

        db.collection("Bookings")
            .add(newBooking)
            .addOnSuccessListener {
                Log.w("create User", "Success")
            }
            .addOnFailureListener {
                Log.w("Create User", "failed")
            }
    }

    fun getNotifications(bookedRv: RecyclerView, notificationAdapter: NotificationAdapter, spotCode: String) {
        db.collection("Bookings")
            .whereEqualTo("spotCode", spotCode)
            .addSnapshotListener {
                    docs, error ->
                if(error != null) Log.e("error", "$error")
                var bookedDates: ArrayList<String> = ArrayList()
                if (docs != null) {
                    for(doc in docs){
                        val data = doc.data
                        val startTime : Timestamp = data["startTime"] as Timestamp
                        var startTimeMs = startTime.seconds * 1000 + startTime.nanoseconds / 1000000
                        var startNetDate = Date(startTimeMs)

                        var dateFormat = SimpleDateFormat("dd/MM/yyyy")

                        if(startTime >= Timestamp.now()){
                            bookedDates.add(dateFormat.format(startNetDate))
                        }
                    }
                }

                notificationAdapter.setNotifications(bookedDates)
                notificationAdapter.notifyDataSetChanged()

                bookedRv.layoutManager = LinearLayoutManager(context)
                bookedRv.adapter = notificationAdapter
            }
    }
}