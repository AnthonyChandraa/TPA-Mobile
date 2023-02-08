package edu.bluejack22_1.beepark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import edu.bluejack22_1.beepark.controllers.UserController
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnLogout : Button;
    private lateinit var firebaseAuth: FirebaseAuth;
    private lateinit var userController: UserController
    private lateinit var userId :String;
    private var isGoogle: Boolean = false
    private lateinit var profileUsername : TextView;
    private lateinit var profileEmail : TextView;
    private lateinit var profileImage : ImageView;
    private lateinit var profileLicensePlate : TextView;

    private lateinit var btnNotification : Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        firebaseAuth = Firebase.auth
        profileUsername = findViewById(R.id.username)
        profileEmail = findViewById(R.id.email)
        profileImage = findViewById(R.id.profile_image)
        profileLicensePlate = findViewById(R.id.licensePlateTv)
        btnNotification = findViewById(R.id.btnNotification)

        val intentUpdate = Intent(this, UpdateProfileActivity::class.java)
        userId = intent.extras?.getString("userId").toString()
        isGoogle = intent.extras?.getBoolean("isGoogle").toString().toBoolean()

        userController = UserController(this)
        userController.setUsername(profileUsername, userId, intentUpdate)
        userController.setEmail(profileEmail, userId, intentUpdate)
        userController.setProfilePicture(profileImage, userId, intentUpdate)

        profileLicensePlate.visibility = View.INVISIBLE
        userController.setLicensePlate(profileLicensePlate, userId, intentUpdate)


        val userRef = userController.getUserRef(userId)

        userRef.get()
            .addOnSuccessListener {
                    document ->
                var url = document.data?.get("imageUrl").toString()
                if(url.isEmpty()){
                    profileImage.setImageResource(R.drawable.ic_person)
                }else{
                    Picasso.get().load(url).placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(profileImage)
                }
            }

        btnLogout = findViewById(R.id.btnLogout)


        btnLogout.setOnClickListener{
            firebaseAuth.signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googlesigninclient = baseContext?.let { it1 -> GoogleSignIn.getClient(it1, gso) }
            if (googlesigninclient != null) {
                googlesigninclient.signOut()
            }
            finishAndRemoveTask()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val btnCreateNotif = findViewById<Button>(R.id.btnCreateReminder)
        btnCreateNotif.setOnClickListener{
            startActivity(Intent(this, CreateNotificationActivity::class.java))
        }

        btnNotification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java).putExtra("userId", userId))
        }

        val btnUpdateProfile = findViewById<Button>(R.id.btnUpdateProfile)
        btnUpdateProfile.setOnClickListener{
            intentUpdate.putExtra("isGoogle", isGoogle)
            intentUpdate.putExtra("userId", userId)
            startActivity(intentUpdate)
        }
    }


}