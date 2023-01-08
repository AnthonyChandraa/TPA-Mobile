package edu.bluejack22_1.beepark.controllers

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import edu.bluejack22_1.beepark.HomeActivity
import edu.bluejack22_1.beepark.R
import edu.bluejack22_1.beepark.UIString.UiString
import kotlin.math.log

class UserController(private var context: Context) {
    private var db = Firebase.firestore

    public fun createNewUser(username: String?, email: String?, isGoogle: Boolean){
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
                if(isGoogle){
                    loginUser(email)
                }
            }
            .addOnFailureListener {
                Log.w("Create User", "failed")
            }
    }

    public fun signInFromGoogle(user: FirebaseUser?){
        val username = user?.displayName
        val email = user?.email
        val photoUrl = user?.photoUrl

        if(!checkIfUserExists(email)){
            val newUser = hashMapOf(
                "email" to email,
                "role" to "user",
                "username" to username,
                "imageUrl" to photoUrl,
                "vehicleLicenseNumber" to "-"
            )

            db.collection("Users")
                .add(newUser)
                .addOnSuccessListener {
                    Log.w("Create User", "success")
                    loginUser(email)
                }
                .addOnFailureListener {
                    Log.w("Create User", "failed")
                }

        }else{
            loginUser(email)
        }
    }

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

    public fun loginUser(email: String?){
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

    public fun setEmail(emailTv:TextView, userId: String){
        db.collection("Users")
            .document(userId)
            .get()
            .addOnSuccessListener {
                    document ->
                if(document != null)
                    emailTv.text = document.data?.get("email").toString()
            }
            .addOnFailureListener {
                Log.w("Email", "failed")
            }
    }

    public fun setProfilePicture(profileIv:ImageView, userId: String){
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
}