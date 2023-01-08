package edu.bluejack22_1.beepark.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beepark.R
import edu.bluejack22_1.beepark.controllers.ParkingSpotController
import edu.bluejack22_1.beepark.model.Booking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Vector

class BookingAdapter(private var bookings: Vector<Booking>, var isActiveBook: Boolean, var context: Context) :
    RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

    private var parkingSpotController = ParkingSpotController(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.booking_item_template, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bookings[position])
    }

    override fun getItemCount(): Int {
        return bookings.size
    }

    public fun setBookings(bookings: Vector<Booking>) {
        this.bookings = bookings
    }

    public fun getBookings(): Vector<Booking>{
        return this.bookings
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var timeTv :TextView? = null
        private var buildingTv :TextView? = null
        private var floorTv :TextView? = null
        private var spotCodeTv :TextView? = null

        private var btnEdit: Button? = null
        private var btnCancel: Button? = null

        init {
            timeTv = itemView.findViewById(R.id.timeTv)
            buildingTv = itemView.findViewById(R.id.buildingTv)
            floorTv = itemView.findViewById(R.id.floorTv)
            spotCodeTv = itemView.findViewById(R.id.spotCodeTv)

            btnEdit = itemView.findViewById(R.id.btnEdit)
            btnCancel = itemView.findViewById(R.id.btnCancel)
        }

        fun bind(booking: Booking){

            val formatDate = SimpleDateFormat("MM/dd/yyyy")
            val formatTime = SimpleDateFormat("hh:mm a")

            var startTimeMs = booking.startTime.seconds * 1000 + booking.startTime.nanoseconds / 1000000
            var startNetDate = Date(startTimeMs)

            var endDateMs = booking.endTime.seconds * 1000 + booking.endTime.nanoseconds / 1000000
            var endNetDate = Date(endDateMs)

            timeTv?.text = formatDate.format(startNetDate).toString() +
                    " (" +
                    formatTime.format(startNetDate).toString() +
                    " ~ " + formatTime.format(endNetDate).toString() + ")"

            parkingSpotController.setUpSpotDetail(booking.spotCode, spotCodeTv, buildingTv, floorTv)

            if (isActiveBook){
                btnEdit?.visibility = View.VISIBLE
                btnCancel?.visibility = View.VISIBLE
            } else {
                btnEdit?.visibility = View.INVISIBLE
                btnCancel?.visibility = View.INVISIBLE
            }

        }

    }

}