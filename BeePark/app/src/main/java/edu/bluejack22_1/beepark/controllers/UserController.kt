package edu.bluejack22_1.beepark.controllers

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.TextView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beepark.HomeActivity
import kotlin.math.log

class UserController(private var context: Context) {
    private var db = Firebase.firestore

    public fun createNewUser(username: String, email: String){
        val newUser = hashMapOf(
            "email" to email,
            "role" to "user",
            "username" to username,
            "imageUrl" to "",
            "vehicleLicenseNumber" to "-"
        )

        db.collection("Users")
            .add(newUser)
            .addOnSuccessListener {
                Log.w("Create User", "success")
            }
            .addOnFailureListener {
                Log.w("Create User", "failed")
            }
    }

    public fun loginUser(email: String){
        db.collection("Users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    Log.w("Login", "not found")
                    return@addOnSuccessListener
                }

                for(document in it){
                    var intent = Intent(context, HomeActivity::class.java)
                    intent.putExtra("userId", document.id)
                    context.startActivity(intent)
                    return@addOnSuccessListener
                }
            }
            .addOnFailureListener {
                Log.w("Login", "failed")
            }
    }

    public fun setUsername(usernameTv:TextView, userId: String){
        db.collection("Users")
            .document(userId)
            .get()
            .addOnSuccessListener {
                document ->
                    if(document != null)
                        usernameTv.text = document.data?.get("username").toString()
            }
            .addOnFailureListener {
                Log.w("Username", "failed")
            }
    }

    public fun getUserRef(userId: String) : DocumentReference{
        return db.collection("Users").document(userId)
    }
}