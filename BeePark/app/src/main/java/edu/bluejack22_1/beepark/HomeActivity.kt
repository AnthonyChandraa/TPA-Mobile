package edu.bluejack22_1.beepark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity() {

    private var pressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnProfile = findViewById<LinearLayout>(R.id.btnProfile)
        btnProfile.setOnClickListener {
            startActivity(Intent(this, RegisActivity::class.java))
        }

        val contentFragmentTrans = supportFragmentManager.beginTransaction()

        val isAdmin = true
        val isUser = false
        if(isUser){
            contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            contentFragmentTrans.add(R.id.contentFragment, UserHomeFragment(), "userHomeFragment").commitAllowingStateLoss()
        } else if(isAdmin){
            contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            contentFragmentTrans.add(R.id.contentFragment, AdminHomeFragment(), "adminHomeFragment").commitAllowingStateLoss()
        }

    }

    override fun onBackPressed() {
//        super.onBackPressed()
        if(pressedTime + 3000 > System.currentTimeMillis()){
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press one more to logout", Toast.LENGTH_SHORT).show()
        }

        pressedTime = System.currentTimeMillis()
    }
}