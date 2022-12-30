package edu.bluejack22_1.beepark.model

import com.google.firebase.Timestamp

class Booking(var bookingId: String, var spotCode: String, var userId: String, var startTime: Timestamp, var endTime: Timestamp) {
}