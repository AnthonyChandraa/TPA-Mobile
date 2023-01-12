package edu.bluejack22_1.beepark

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.squareup.picasso.Picasso
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
        userController.setUsername(binding.usernameTv, userId, Intent(this, HomeActivity::class.java))

        val userRef = userController.getUserRef(userId)

            userRef.get()
                .addOnSuccessListener {
                        document ->
                    isAdmin = document.data?.get("role").toString() != "user"
                    var url = document.data?.get("imageUrl").toString()
                    if(url.isEmpty()){
                        binding.profileIcon.setImageResource(R.drawable.ic_person)
                    }else{
                        Picasso.get().load(url).placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .into(binding.profileIcon)
                    }

                    setUpNavBar()
                }

        setContentView(binding.root)
    }

    private fun setUpNavBar(){
        if(isAdmin){
            binding.bottomNavUser.visibility = View.INVISIBLE
            binding.bottomNavAdmin.visibility = View.VISIBLE
        } else{
            binding.bottomNavUser.visibility = View.VISIBLE
            binding.bottomNavAdmin.visibility = View.INVISIBLE
        }

        if(isAdmin){
            var adapter = AdminDrawerAdapter(this, true, userId)

            binding.viewPager.adapter = adapter

            binding.bottomNavAdmin.setOnItemSelectedListener() { item ->
                when (item.itemId) {
                    R.id.menu_item1 -> binding.viewPager.currentItem = 0
                    R.id.menu_item2 -> binding.viewPager.currentItem = 1
                    R.id.menu_item3 -> binding.viewPager.currentItem = 2
                }
                true
            }

            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    // Handle onPageScrolled event
                }

                override fun onPageSelected(position: Int) {
                    if(position == 0) binding.bottomNavAdmin.selectedItemId = R.id.menu_item1
                    if(position == 1) binding.bottomNavAdmin.selectedItemId = R.id.menu_item2
                    if(position == 2) binding.bottomNavAdmin.selectedItemId = R.id.menu_item3
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // Handle onPageScrollStateChanged event
                }
            })

        } else {
            var adapter = UserDrawerAdapter(this, false, userId)

            binding.viewPager.adapter = adapter

            binding.bottomNavUser.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_item1 -> binding.viewPager.currentItem = 0
                    R.id.menu_item2 -> binding.viewPager.currentItem = 1
                    R.id.menu_item3 -> binding.viewPager.currentItem = 2
                    R.id.menu_item4 -> binding.viewPager.currentItem = 3
                }
                true
            }

            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    // Handle onPageScrolled event
                }

                override fun onPageSelected(position: Int) {
                    if(position == 0) binding.bottomNavUser.selectedItemId = R.id.menu_item1
                    if(position == 1) binding.bottomNavUser.selectedItemId = R.id.menu_item2
                    if(position == 2) binding.bottomNavUser.selectedItemId = R.id.menu_item3
                    if(position == 3) binding.bottomNavUser.selectedItemId = R.id.menu_item4
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // Handle onPageScrollStateChanged event
                }
            })

        }
    }


    private fun setUpButtonAction() {
        val btnProfile = binding.btnProfile

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).putExtra("userId", intent.extras?.getString("userId").toString()))
        }
    }

    override fun onBackPressed() {
        if(pressedTime + 3000 > System.currentTimeMillis()){
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press one more to logout", Toast.LENGTH_SHORT).show()
        }

        pressedTime = System.currentTimeMillis()
    }

    class AdminDrawerAdapter(fragmentActivity: FragmentActivity, var isAdmin: Boolean,
                             var userId: String) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount() = 3
        override fun createFragment(position: Int): Fragment {
            // Return a new instance of the fragment for the given position
            return when (position) {
                0 -> openHomeFragment()
                1 -> openManageOvernightRequestFragment()
                2 -> openParkingHistoryFragment()
                else -> openHomeFragment()
            }
        }

        private fun openHomeFragment() : HomeFragment{
            val homeFragment = HomeFragment()
            homeFragment.arguments = Bundle().apply {
                putBoolean("isAdmin", isAdmin)
                putString("userId", userId)

            }
            return homeFragment
        }

        private fun openParkingHistoryFragment() : ParkingHistoryFragment {
            val parkingHistoryFragment = ParkingHistoryFragment()
            parkingHistoryFragment.arguments = Bundle().apply {
                putBoolean("isAdmin", isAdmin)
                putString("userId", userId)
            }
            return  parkingHistoryFragment
        }

        private fun openManageOvernightRequestFragment() : ManageOvernightRequestFragment{
            val manageOvernightRequestFragment = ManageOvernightRequestFragment()
            manageOvernightRequestFragment.arguments = Bundle().apply {
                putBoolean("isAdmin", isAdmin)
                putString("userId", userId)
            }
            return manageOvernightRequestFragment
        }
    }


    class UserDrawerAdapter(fragmentActivity: FragmentActivity, var isAdmin: Boolean, var userId: String)
        : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount() = 4
        override fun createFragment(position: Int): Fragment {
            // Return a new instance of the fragment for the given position
            return when (position) {
                0 -> openHomeFragment()
                1 -> openMyBookingFragment()
                2 -> openBookingHistoryFragment()
                3 -> openOvernightRequestFragment()
                else -> openHomeFragment()
            }
        }

        private fun openHomeFragment() : HomeFragment{
            val homeFragment = HomeFragment()
            homeFragment.arguments = Bundle().apply {
                putBoolean("isAdmin", isAdmin)
                putString("userId", userId)

            }
            return homeFragment
        }

        private fun openMyBookingFragment() : MyBookingFragment{
            val myBookingFragment = MyBookingFragment()
            myBookingFragment.arguments = Bundle().apply {
                putBoolean("isAdmin", isAdmin)
                putString("userId", userId)
            }
            return myBookingFragment
        }

        private fun openBookingHistoryFragment(): BookingHistoryFragment{
            val bookingHistoryFragment = BookingHistoryFragment()
            bookingHistoryFragment.arguments = Bundle().apply {
                putBoolean("isAdmin", isAdmin)
                putString("userId", userId)
            }
            return bookingHistoryFragment
        }

        private fun openOvernightRequestFragment() : OvernightRequestFragment{
            val overnightRequestFragment = OvernightRequestFragment()
            overnightRequestFragment.arguments = Bundle().apply {
                putBoolean("isAdmin", isAdmin)
                putString("userId", userId)
            }
            return overnightRequestFragment
        }
    }
}