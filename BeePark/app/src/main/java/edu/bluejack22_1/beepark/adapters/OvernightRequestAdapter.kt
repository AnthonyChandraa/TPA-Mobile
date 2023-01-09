package edu.bluejack22_1.beepark.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beepark.R
import edu.bluejack22_1.beepark.controllers.BookingController
import edu.bluejack22_1.beepark.controllers.OvernightRequestController
import edu.bluejack22_1.beepark.controllers.ParkingSpotController
import edu.bluejack22_1.beepark.model.OvernightRequest
import java.text.SimpleDateFormat
import java.util.*

class OvernightRequestAdapter(private var overnightRequests: Vector<OvernightRequest>,
                              var context: Context, var userId: String)
    : RecyclerView.Adapter<OvernightRequestAdapter.ViewHolder>()  {

    private var parkingSpotController = ParkingSpotController(context)
    private var bookingController = BookingController(context)
    private var overnightRequestController = OvernightRequestController(context, bookingController)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.request_item_template, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(overnightRequests[position])
    }

    override fun getItemCount(): Int {
        return overnightRequests.size
    }

    public fun setRequests(requests : Vector<OvernightRequest>){
        this.overnightRequests = requests
    }

    public fun getRequests() : Vector<OvernightRequest>{
        return this.overnightRequests
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var timeTv : TextView? = null
        private var buildingTv : TextView? = null
        private var floorTv : TextView? = null
        private var spotCodeTv : TextView? = null

        private var reasonTv : TextView? = null
        private var btnApprove: Button? = null
        private var btnNotApprove: Button? = null

        init {
            timeTv = itemView.findViewById(R.id.timeTv)
            buildingTv = itemView.findViewById(R.id.buildingTv)
            floorTv = itemView.findViewById(R.id.floorTv)
            spotCodeTv = itemView.findViewById(R.id.spotCodeTv)

            btnApprove = itemView.findViewById(R.id.btnApprove)
            btnNotApprove = itemView.findViewById(R.id.btnNotApprove)
        }

        fun bind(request: OvernightRequest){

            val formatDate = SimpleDateFormat("MM/dd/yyyy")
            val formatTime = SimpleDateFormat("hh:mm a")

            var startTimeMs = request.startTime.seconds * 1000 + request.startTime.nanoseconds / 1000000
            var startNetDate = Date(startTimeMs)

            var endDateMs = request.endTime.seconds * 1000 + request.endTime.nanoseconds / 1000000
            var endNetDate = Date(endDateMs)

            timeTv?.text = formatDate.format(startNetDate).toString() +
                    " (" +
                    formatTime.format(startNetDate).toString() +
                    " ~ " + formatTime.format(endNetDate).toString() + ")"

            parkingSpotController.setUpSpotDetail(request.spotCode, spotCodeTv, buildingTv, floorTv, false, null, "")

            reasonTv?.text = request.reason

            setUpButton(request)
        }

        private fun setUpButton(request: OvernightRequest){
            btnApprove?.setOnClickListener(View.OnClickListener {
                overnightRequestController.updateApproval(request.requestId, request.startTime, request.endTime,
                    request.spotCode, request.userId,true)
            })

            btnNotApprove?.setOnClickListener(View.OnClickListener {
                overnightRequestController.updateApproval(request.requestId, request.startTime, request.endTime,
                    request.spotCode, request.userId,false)
            })
        }

    }

}