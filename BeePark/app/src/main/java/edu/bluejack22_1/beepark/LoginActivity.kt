package edu.bluejack22_1.beepark

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth;
    private lateinit var etEmail : EditText;
    private lateinit var etPassword : EditText;
    private lateinit var btnLogin : Button;
    private lateinit var btnToRegis : Button;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = Firebase.auth;

//        val currentUser = firebaseAuth.currentUser
//        if(currentUser != null){
//            startActivity(Intent(this, HomeActivity::class.java))
//        }

        etEmail = findViewById(R.id.emailInput)
        etPassword = findViewById(R.id.passwordInput)
        btnLogin = findViewById(R.id.btnLogin)
        btnToRegis = findViewById(R.id.btnRegis)

        btnToRegis.setOnClickListener{
            finishAndRemoveTask()
            startActivity(Intent(this, RegisActivity::class.java))
        }

        btnLogin.setOnClickListener{
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if(email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){ task->
                    if(task.isSuccessful){
                        startActivity(Intent(this, HomeActivity::class.java))
                    }else{
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Login failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}