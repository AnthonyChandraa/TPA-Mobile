package edu.bluejack22_1.beepark

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import edu.bluejack22_1.beepark.adminFragments.ManageOvernightRequestFragment
import edu.bluejack22_1.beepark.adminFragments.ParkingHistoryFragment
import edu.bluejack22_1.beepark.controllers.UserController
import edu.bluejack22_1.beepark.databinding.ActivityHomeBinding
import edu.bluejack22_1.beepark.userFragments.BookingHistoryFragment
import edu.bluejack22_1.beepark.userFragments.MyBookingFragment
import edu.bluejack22_1.beepark.userFragments.OvernightRequestFragment

class HomeActivity : AppCompatActivity() {

    private var pressedTime = 0L
    private lateinit var binding: ActivityHomeBinding
    private var isAdmin = false
    private lateinit var userController: UserController
    private lateinit var userId : String
    private var fragmentType : String = "home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(LayoutInflater.from(this))

        setUpButtonAction()

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
//        openMyBookingFragment(contentFragmentTrans)
//        openBookingHistoryFragment(contentFragmentTrans)
//        openOvernightRequestFragment(contentFragmentTrans)

//        admin
//        openParkingHistoryFragment(contentFragmentTrans)
        openManageOvernightRequestFragment(contentFragmentTrans)
        setContentView(binding.root)
    }

    private fun setUpButtonAction() {
        val btnProfile = binding.btnProfile
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).putExtra("userId", intent.extras?.getString("userId").toString()))
        }
    }

    //    admin view punya
    private fun openParkingHistoryFragment(contentFragmentTrans: FragmentTransaction){
        contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        val parkingHistoryFragment = ParkingHistoryFragment()
        parkingHistoryFragment.arguments = Bundle().apply {
            putBoolean("isAdmin", isAdmin)
            putString("userId", userId)
        }
        contentFragmentTrans.add(R.id.contentFragment, parkingHistoryFragment, "parkingHistoryFragment").commitAllowingStateLoss()

    }

    private fun openManageOvernightRequestFragment(contentFragmentTrans: FragmentTransaction){
        contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        val manageOvernightRequestFragment = ManageOvernightRequestFragment()
        manageOvernightRequestFragment.arguments = Bundle().apply {
            putBoolean("isAdmin", isAdmin)
            putString("userId", userId)
        }
        contentFragmentTrans.add(R.id.contentFragment, manageOvernightRequestFragment, "manageOvernightRequestFragment").commitAllowingStateLoss()
    }

    private fun openOvernightRequestFragment(contentFragmentTrans: FragmentTransaction){
        contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        val overnightRequestFragment = OvernightRequestFragment()
        overnightRequestFragment.arguments = Bundle().apply {
            putBoolean("isAdmin", isAdmin)
            putString("userId", userId)
        }
        contentFragmentTrans.add(R.id.contentFragment, overnightRequestFragment, "overnightRequestFragment").commitAllowingStateLoss()
    }

    private fun openBookingHistoryFragment(contentFragmentTrans: FragmentTransaction){
        contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        val bookingHistoryFragment = BookingHistoryFragment()
        bookingHistoryFragment.arguments = Bundle().apply {
            putBoolean("isAdmin", isAdmin)
            putString("userId", userId)
        }
        contentFragmentTrans.add(R.id.contentFragment, bookingHistoryFragment, "bookingHistoryFragment").commitAllowingStateLoss()
    }

    private fun openMyBookingFragment(contentFragmentTrans: FragmentTransaction){
        contentFragmentTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        val myBookingFragment = MyBookingFragment()
        myBookingFragment.arguments = Bundle().apply {
            putBoolean("isAdmin", isAdmin)
            putString("userId", userId)
        }
        contentFragmentTrans.add(R.id.contentFragment, myBookingFragment, "myBookingFragment").commitAllowingStateLoss()
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