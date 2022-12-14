package edu.bluejack22_1.beepark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RegisActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regis)

        val btnToLogin = findViewById<Button>(R.id.btnLogin)
        btnToLogin.setOnClickListener{
            finishAndRemoveTask()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // submit regis
        val buttonRegis = findViewById<Button>(R.id.btnRegister)
        buttonRegis.setOnClickListener(View.OnClickListener {
            finishAndRemoveTask()
            startActivity(Intent(this, HomeActivity::class.java))
        })
    }
}