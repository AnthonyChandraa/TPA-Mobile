package edu.bluejack22_1.beepark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnToRegis = findViewById<Button>(R.id.btnRegis)
        btnToRegis.setOnClickListener{
            startActivity(Intent(this, RegisActivity::class.java))
        }

        val btnToLogin = findViewById<Button>(R.id.btnLogin)
        btnToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}