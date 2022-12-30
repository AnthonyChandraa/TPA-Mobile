package edu.bluejack22_1.beepark


import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beepark.UIString.UiString
import edu.bluejack22_1.beepark.controllers.UserController


class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth;
    private lateinit var etEmail : EditText;
    private lateinit var etPassword : EditText;
    private lateinit var btnLogin : Button;
    private lateinit var btnToRegis : Button;
    private lateinit var btnSignInWithGoogle : SignInButton;
    private lateinit var googleSignInClient : GoogleSignInClient;
    private lateinit var userController: UserController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

//        val currentUser = firebaseAuth.currentUser
//        if(currentUser != null){
//            startActivity(Intent(this, HomeActivity::class.java))
//        }

        userController = UserController(this)

        btnSignInWithGoogle = findViewById(R.id.signInWithGoogleButton)
        btnSignInWithGoogle.setSize(SignInButton.SIZE_STANDARD)

        btnSignInWithGoogle.setOnClickListener {
            signInWithGoogle()
        }

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

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(baseContext, UiString.StringResource(resId = R.string.errorEmpty).asString(this), Toast.LENGTH_SHORT).show()
            }

            if(email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){ task->
                    if(task.isSuccessful){
                        userController.loginUser(email)
                    }else{
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, UiString.StringResource(resId = R.string.errorLogin).asString(this), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun signInWithGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
            if(result.resultCode == Activity.RESULT_OK){

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if(account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
//                val user = firebaseAuth.currentUser
//                userController.signInFromGoogle(user);
                startActivity(Intent(this, HomeActivity::class.java))
            }else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


}
