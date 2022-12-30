package edu.bluejack22_1.beepark.adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.provider.Settings.Global.getString
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beepark.model.ParkingSpot
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import edu.bluejack22_1.beepark.DetailSpotActivity
import edu.bluejack22_1.beepark.R
import edu.bluejack22_1.beepark.RegisActivity
import edu.bluejack22_1.beepark.UIString.UiString
import edu.bluejack22_1.beepark.controllers.ParkingSpotController
import java.util.*

class ParkingSpotsAdapter(private var parkingSpots: Vector<ParkingSpot>, var isAdmin: Boolean, var userId: String, var context: Context) :
    RecyclerView.Adapter<ParkingSpotsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.parking_spot_template, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(parkingSpots[position])
        holder.itemView.setOnClickListener(View.OnClickListener {
            var intent: Intent = Intent(context, DetailSpotActivity::class.java)
            intent.putExtra("isAdmin", isAdmin)
            intent.putExtra("userId", userId)
            intent.putExtra("spotCode", parkingSpots[position].spotCode)
            context.startActivity(intent)
        })
    }
    override fun getItemCount(): Int {
        return parkingSpots.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var codeTv: TextView? = null
        private var closeOpenBtn: Button? = null

        init{
            codeTv = itemView.findViewById(R.id.spotCode)
            closeOpenBtn = itemView.findViewById(R.id.btnCloseOrOpen)
        }

        fun bind(spot: ParkingSpot){
            codeTv?.text = spot.spotCode

            closeOpenBtn?.visibility = if(!isAdmin) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }

            val parkingSpotController = ParkingSpotController(context)
            closeOpenBtn?.let { parkingSpotController.setAdminButton(spot.spotCode, it) }

        }
    }
}