package edu.bluejack22_1.beepark.controllers

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import edu.bluejack22_1.beepark.HomeActivity
import edu.bluejack22_1.beepark.ProfileActivity
import edu.bluejack22_1.beepark.R
import edu.bluejack22_1.beepark.UIString.UiString
import edu.bluejack22_1.beepark.adapters.NotificationAdapter

class UserController(private var context: Context) {
    private var db = Firebase.firestore

    public fun createNewUser(username: String?, email: String?, isGoogle:Boolean, imageUrl: String){
        val newUser = hashMapOf(
            "email" to email,
            "role" to "user",
            "username" to username,
            "imageUrl" to imageUrl,
            "vehicleLicenseNumber" to ""
        )

        db.collection("Users")
            .add(newUser)
            .addOnSuccessListener {
                Log.w("Create User", "success")
                if(isGoogle){
                    loginUser(email, isGoogle)
                }
            }
            .addOnFailureListener {
                Log.w("Create User", "failed")
            }


    }

//    public fun signInFromGoogle(user: FirebaseUser?){
//        val username = user?.displayName
//        val email = user?.email
//        val photoUrl = user?.photoUrl
//
//        if(!checkIfUserExists(email)){
//            val newUser = hashMapOf(
//                "email" to email,
//                "role" to "user",
//                "username" to username,
//                "imageUrl" to photoUrl,
//                "vehicleLicenseNumber" to ""
//            )
//
//            db.collection("Users")
//                .add(newUser)
//                .addOnSuccessListener {
//                    Log.w("Create User", "success")
//                    loginUser(email)
//                }
//                .addOnFailureListener {
//                    Log.w("Create User", "failed")
//                }
//
//        }else{
//            loginUser(email)
//        }
//    }

    private fun checkIfUserExists(email: String?): Boolean {
        var userExists = true;
        db.collection("Users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    userExists = false
                    return@addOnSuccessListener
                }
                else userExists = true
            }
        return userExists
    }

    public fun loginUser(email: String?, isGoogle: Boolean){
        db.collection("Users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    Log.w("Login", "not found")
                    Toast.makeText(context, UiString.StringResource(resId = R.string.errorCredentials).asString(context), Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for(document in it){
                    var intent = Intent(context, HomeActivity::class.java)
                    intent.putExtra("userId", document.id)
                    intent.putExtra("isGoogle", isGoogle)
                    context.startActivity(intent)
                    return@addOnSuccessListener
                }
            }
            .addOnFailureListener {
                Log.w("Login", "failed")
            }
    }

    public fun setUsername(usernameTv:TextView, userId: String, intent: Intent){
        db.collection("Users")
            .document(userId)
            .get()
            .addOnSuccessListener {
                document ->
                    if(document != null){
                        var username =  document.data?.get("username").toString()
                        usernameTv.text = username
                        intent.putExtra("username", username)
                    }
            }
            .addOnFailureListener {
                Log.w("Username", "failed")
            }
    }

    public fun insertFeedback(
        userId: String,
        type: String,
        reportText: String,
        format: String
    ){
        var message = ""
        db.collection("Users")
            .document(userId)
            .get()
            .addOnSuccessListener {
                    document ->
                if(document != null)
                    message =  "["+type+" | "+format+"]" + " " +  document.data?.get("username").toString() + " : " + reportText
                    db.collection("Users").document("679LmHp1Mm0MbFwTDolY")
                        .update("notifications", FieldValue.arrayUnion(message))

                context.startActivity(Intent(context, HomeActivity::class.java).putExtra("userId", userId))
            }
            .addOnFailureListener {
                Log.w("Username", "failed")
            }
    }



    public fun setEmail(emailTv:TextView, userId: String, intent: Intent){
        db.collection("Users")
            .document(userId)
            .get()
            .addOnSuccessListener {
                    document ->
                if(document != null){
                    emailTv.text = document.data?.get("email").toString()
                    intent.putExtra("email", document.data?.get("email").toString())
                }
            }
            .addOnFailureListener {
                Log.w("Email", "failed")
            }
    }

    public fun setProfilePicture(profileIv:ImageView, userId: String, intent: Intent){
        db.collection("Users")
            .document(userId)
            .get()
            .addOnSuccessListener {
                    document ->
                if(document != null)
                    if(document.data?.get("imageUrl").toString() != ""){
                        Picasso.get()
                            .load(document.data?.get("imageUrl").toString())
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .into(profileIv);

                        intent.putExtra("imageUrl", document.data?.get("imageUrl").toString())
                    }else{
                        profileIv.setBackgroundResource(R.drawable.ic_person)
                    }


            }
            .addOnFailureListener {
                Log.w("Picture", "failed")
                profileIv.setImageDrawable(R.drawable.ic_person.toDrawable())
            }
    }

    public fun getUserRef(userId: String) : DocumentReference{
        return db.collection("Users").document(userId)
    }

    fun setLicensePlate(profileLicensePlate: TextView, userId: String, intent: Intent) {
        db.collection("Users")
            .document(userId)
            .get()
            .addOnSuccessListener {
                    document ->
                if(document != null){
                    if(document.data?.get("vehicleLicenseNumber") != ""){
                        profileLicensePlate.visibility = View.VISIBLE
                        profileLicensePlate.text = document.data?.get("vehicleLicenseNumber").toString()
                        intent.putExtra("licensePlate", document.data?.get("vehicleLicenseNumber").toString())
                    }
                }
            }
            .addOnFailureListener {
                Log.w("Email", "failed")
            }
    }

    fun getNotifications(notifRv: RecyclerView, notificationAdapter: NotificationAdapter, userId: String) {
        db.collection("Users")
            .document(userId)
            .addSnapshotListener {
                    doc, error ->
                    if(error != null) Log.e("error", "$error")


                    var userNotifications = doc?.data?.get("notifications") as? List<String>
                    if(userNotifications == null){
                        userNotifications = ArrayList<String>()
                    }

                    Log.w("user notifications", "$userNotifications")
                    notificationAdapter.setNotifications(userNotifications as ArrayList<String>)
                    notificationAdapter.notifyDataSetChanged()

                    notifRv.layoutManager = LinearLayoutManager(context)
                    notifRv.adapter = notificationAdapter
            }
    }

    fun insertNotification(userId: String, type: String, content: String, format: String) {
        var message = ""
            message = "[$type | $format] Admin Has $content your request"

        db.collection("Users").document(userId)
            .update("notifications", FieldValue.arrayUnion(message))

    }

    fun updateUser(username: String, email: String, licensePlate: String, imageUrl: String , userId: String) {
        db.collection("Users").document(userId)
            .update(mapOf(
                "username" to username,
                "email" to email,
                "imageUrl" to imageUrl,
                "vehicleLicenseNumber" to licensePlate
            ))
            .addOnSuccessListener {

                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("userId", userId)
                context.startActivity(intent)
            }
    }
}