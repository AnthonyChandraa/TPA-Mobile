package edu.bluejack22_1.beepark

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beepark.controllers.BuildingController
import edu.bluejack22_1.beepark.controllers.ParkingSpotController
import edu.bluejack22_1.beepark.controllers.UserController
import edu.bluejack22_1.beepark.databinding.ActivityHomeBinding
import edu.bluejack22_1.beepark.userFragments.MyBookingFragment
import java.util.*

class HomeActivity : AppCompatActivity() {

    private var pressedTime = 0L
    private lateinit var binding: ActivityHomeBinding
    private var isAdmin = false
    private lateinit var userController: UserController
    private lateinit var userId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(LayoutInflater.from(this))

        val btnProfile = binding.btnProfile
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).putExtra("userId", intent.extras?.getString("userId").toString()))
        }

       val contentFragmentTrans = supportFragmentManager.beginTransaction()
//      authorization user/admin
//        if(!isAdmin){
//            contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            contentFragmentTrans.add(R.id.contentFragment, userHomeFragment, "userHomeFragment").commitAllowingStateLoss()
//        } else {
//            contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            contentFragmentTrans.add(R.id.contentFragment, AdminHomeFragment(), "adminHomeFragment").commitAllowingStateLoss()
//        }

        userId = intent.extras?.getString("userId").toString()
        userController = UserController(this)
        userController.setUsername(binding.usernameTv, userId)

//        openHomeFragment(contentFragmentTrans)
        openMyBookingFragment(contentFragmentTrans)
        setContentView(binding.root)
    }

    private fun openMyBookingFragment(contentFragmentTrans: FragmentTransaction){
        contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        val myBookingFragment = MyBookingFragment()
        myBookingFragment.arguments = Bundle().apply {
            putBoolean("isAdmin", isAdmin)
            putString("userId", userId)
        }
        contentFragmentTrans.add(R.id.contentFragment, myBookingFragment, "homeFragment").commitAllowingStateLoss()
    }

    private fun openHomeFragment(contentFragmentTrans: FragmentTransaction){
        val userRef = userController.getUserRef(userId)
        userRef.get()
            .addOnSuccessListener {
                    document ->
                isAdmin = document.data?.get("role").toString() != "user"
                contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                val homeFragment = HomeFragment()
                homeFragment.arguments = Bundle().apply {
                    putBoolean("isAdmin", isAdmin)
                    putString("userId", userId)
                }
                contentFragmentTrans.add(R.id.contentFragment, homeFragment, "homeFragment").commitAllowingStateLoss()
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