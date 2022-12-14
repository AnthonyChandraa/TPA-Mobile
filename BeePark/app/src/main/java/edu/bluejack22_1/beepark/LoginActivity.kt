package edu.bluejack22_1.beepark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val btnToRegis = findViewById<Button>(R.id.btnRegis)
        btnToRegis.setOnClickListener{
            finishAndRemoveTask()
            startActivity(Intent(this, RegisActivity::class.java))
        }

        // submit login
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener{
            finishAndRemoveTask()
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}