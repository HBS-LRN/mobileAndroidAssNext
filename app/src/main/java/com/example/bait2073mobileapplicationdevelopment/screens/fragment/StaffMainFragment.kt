package com.example.bait2073mobileapplicationdevelopment.screens.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bait2073mobileapplicationdevelopment.R
import com.example.bait2073mobileapplicationdevelopment.databinding.FragmentStaffMainBinding
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.screens.dialog.RatingDialog
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso

class StaffMainFragment : AppCompatActivity(){

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: FragmentStaffMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private  lateinit var viewModelUser: StaffMainFragmentViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentStaffMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_staff_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.reportDashboardFragment, R.id.userListFragment,R.id.symptomListFragment2,R.id.diseaseListFragment,R.id.recipeListFragment2,R.id.hospitalListFragment2,
                R.id.eventListFragment,R.id.adminListFragment, R.id.userRatingFragment,R.id.logoutFragment,R.id.workoutListFragment
               ),
            binding.drawerLayout
        )

        initUserViewModel()

        val userData = retrieveUserDataFromSharedPreferences(this)
        val userId = userData?.first

        loadUserData(userId)

// Hide or show the ActionBar back button based on the current destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in setOf(
                    R.id.homeFragment, R.id.reportFragment,
                    R.id.eventFragment,
                    R.id.diseasePreventionFragment,
                    R.id.dashboardFragment)) {
//                binding.bottomNav.visibility = View.VISIBLE
            } else {
//                binding.bottomNav.visibility = View.INVISIBLE
//                binding.bottomNav.menu.setGroupEnabled(R.id.bottom_nav, false) // Disable menu items
//                binding.bottomNav.setOnNavigationItemSelectedListener { false } // Prevent item clicks
//                binding.bottomNav.animate().translationY(binding.bottomNav.getHeight()).setDuration(1000);
//                val layoutParams = binding.bottomNav.layoutParams as CoordinatorLayout.LayoutParams
//                layoutParams.height = 0
//                layoutParams.width = 0
//                binding.bottomNav.layoutParams = layoutParams
            }
        }





        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
//        toolbar.setTitleTextColor(Color.WHITE)
//
//        val boldTitle = SpannableString("Healthy Life+")
//        boldTitle.setSpan(StyleSpan(Typeface.BOLD), 0, boldTitle.length, 0)
//        toolbar.title = boldTitle
//
        setSupportActionBar(toolbar)

        setupActionBarWithNavController(navController, appBarConfiguration)


//        binding.bottomNav.setupWithNavController(navController)
        binding.navView.setupWithNavController(navController)

//        setToolbarTitle("Healthy Life+", isBold = true)

//        val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
//            this, binding.drawerLayout, toolbar,
//            R.string.open_nav, R.string.close_nav
//        )
//        binding.drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//
//        val navigationView = findViewById<NavigationView>(R.id.nav_view)
//        navigationView.setNavigationItemSelectedListener(this)





//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction().replace(
//                R.id.frame_layout,
//                HomeFragment()
//            ).commit()
//            navigationView.setCheckedItem(R.id.nav_home)
//        }




//        fab.setOnClickListener {
//            showBottomDialog()
//        }

    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return if (item.itemId == androidx.navigation.ui.R.id.termsAndConditions) {
//            val action = NavGraphDirections.actionGlobalTermsFragment()
//            navController.navigate(action)
//            true
//        } else {
//            item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
//        }
//    }



    private fun loadUserData(user_id: Int?) {
        viewModelUser.getLoadUserObservable().observe(this, Observer<User?> {
            if (it != null) {

                val navigationView = findViewById<NavigationView>(R.id.nav_view)
                val navHeaderView = navigationView.getHeaderView(0) // Assuming it's the first header
                val navImage = navHeaderView.findViewById<ImageView>(R.id.navImage)
                val navName = navHeaderView.findViewById<TextView>(R.id.navName)
                val navEmail = navHeaderView.findViewById<TextView>(R.id.navEmail)
                navName.text = it.name
                navEmail.text = it.email

                if (!it.image.isNullOrBlank()) {

                    Picasso.get().load(it.image).into(navImage)


                } else {
                    Log.e("noimage", "noimage")
                    Picasso.get().load(R.drawable.img_person).into(navImage)
                }

            }
        })
        viewModelUser.getUserData(user_id)
    }
    private fun retrieveUserDataFromSharedPreferences(context: Context): Pair<Int, String>? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt(
            "UserId",
            -1
        ) // -1 is a default value if the key is not found
        val userName = sharedPreferences.getString(
            "UserName",
            null
        ) // null is a default value if the key is not found
        if (userId != -1 && userName != null) {
            return Pair(userId, userName)
        }
        return null
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


//    private fun showBottomDialog() {
//        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.bottomsheetlayout)
//
//        val videoLayout = dialog.findViewById<LinearLayout>(R.id.layoutVideo)
//        val shortsLayout = dialog.findViewById<LinearLayout>(R.id.layoutShorts)
//        val liveLayout = dialog.findViewById<LinearLayout>(R.id.layoutLive)
//        val cancelButton = dialog.findViewById<ImageView>(R.id.cancelButton)
//
//        videoLayout.setOnClickListener {
//            dialog.dismiss()
//            Toast.makeText(this, "Upload a Video is clicked", Toast.LENGTH_SHORT).show()
//        }
//
//        shortsLayout.setOnClickListener {
//            dialog.dismiss()
//            Toast.makeText(this, "Create a short is Clicked", Toast.LENGTH_SHORT).show()
//        }
//
//        liveLayout.setOnClickListener {
//            dialog.dismiss()
//            Toast.makeText(this, "Go live is Clicked", Toast.LENGTH_SHORT).show()
//        }
//
//        cancelButton.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.show()
//        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
//        dialog.window?.setGravity(Gravity.BOTTOM)
//    }


    private fun initUserViewModel() {
        viewModelUser = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(StaffMainFragmentViewModel::class.java)
    }

}