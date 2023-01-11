package edu.bluejack22_1.beepark.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beepark.R
import kotlin.collections.ArrayList

class NotificationAdapter(private var notifications:ArrayList<String>, private var context: Context) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

//    val userController = UserController(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.notfication_item_template, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    public fun setNotifications(notifications: ArrayList<String>){
        Log.w("notif", "$notifications")
        this.notifications = notifications
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private var notifTv : TextView? = null

        init {
            notifTv = itemView.findViewById(R.id.notifTv)
        }

        fun bind(notif: String){
            Log.w("notif", notif)
            notifTv?.text = notif
        }
    }
}