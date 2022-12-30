package edu.bluejack22_1.beepark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beepark.controllers.UserController

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnLogout : Button;
    private lateinit var firebaseAuth: FirebaseAuth;
    private lateinit var userController: UserController
    private lateinit var userId :String;
    private lateinit var tvUsername : TextView;
    private lateinit var profileUsername : TextView;
    private lateinit var profileEmail : TextView;
    private lateinit var profileImage : ImageView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        firebaseAuth = Firebase.auth
        tvUsername = findViewById(R.id.usernameTv)
        profileUsername = findViewById(R.id.username)
        profileEmail = findViewById(R.id.email)
        profileImage = findViewById(R.id.profile_image)


        userId = intent.extras?.getString("userId").toString()
        userController = UserController(this)
        userController.setUsername(tvUsername, userId)
        userController.setUsername(profileUsername, userId)
        userController.setEmail(profileEmail, userId)
        userController.setProfilePicture(profileImage, userId)

        btnLogout = findViewById(R.id.btnLogout)

        btnLogout.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }


}