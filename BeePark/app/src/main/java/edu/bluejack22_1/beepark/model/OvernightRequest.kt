package edu.bluejack22_1.beepark.model

import com.google.firebase.Timestamp

class OvernightRequest(var requestId: String,var spotCode:String, var userId: String, var startTime:Timestamp, var endTime: Timestamp,
                       var reason: String, statusApproved : Boolean
)