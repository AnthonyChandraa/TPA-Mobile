package edu.bluejack22_1.beepark

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beepark.UIString.UiString
import edu.bluejack22_1.beepark.controllers.UserController

class RegisActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var etUsername : EditText
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var etConfirm : EditText
    private lateinit var btnRegister : Button
    private lateinit var btnToLogin : Button
    private lateinit var userController: UserController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regis)
        firebaseAuth = Firebase.auth
//        val currentUser = firebaseAuth.currentUser
//        if(currentUser != null){
//            startActivity(Intent(this, HomeActivity::class.java))
//        }

        userController = UserController(this)

        etUsername = findViewById(R.id.usernameInput)
        etEmail = findViewById(R.id.emailInput)
        etPassword = findViewById(R.id.passwordInput)
        etConfirm = findViewById(R.id.confirmInput)
        btnRegister = findViewById(R.id.btnSubmitRegister)
        btnToLogin = findViewById(R.id.btnLogin)

        btnToLogin.setOnClickListener{
            finishAndRemoveTask()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener{
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirm  = etConfirm.text.toString().trim()

            if(username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirm.isNotEmpty()){
                if(password == confirm){
                    if(password.length in 8..20){
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){ task ->
                            if(task.isSuccessful){
                                userController.createNewUser(username, email, false, "")
                                startActivity(Intent(this, LoginActivity::class.java))

                            }else{
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(this, UiString.StringResource(resId = R.string.errorRegis).asString(this), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        Toast.makeText(this, UiString.StringResource(resId = R.string.errorPasswordLength).asString(this), Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, UiString.StringResource(resId = R.string.errorConfirmPassword).asString(this), Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, UiString.StringResource(resId = R.string.errorEmpty).asString(this), Toast.LENGTH_SHORT).show()
            }
        }
    }
}