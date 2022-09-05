package uz.mobiler.lesson75

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import uz.mobiler.lesson75.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navView.itemIconTintList = null

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_popular,
                R.id.nav_random,
                R.id.nav_liked,
                R.id.nav_history,
                R.id.nav_about
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.pinterest.setOnClickListener {
            val uri = Uri.parse("https://www.pexels.com/")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        binding.unsplash.setOnClickListener {
            val uri = Uri.parse("https://unsplash.com/")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        binding.appBarMain.contentMain.homeBtn.setOnClickListener {
            hideCircles()
            binding.appBarMain.contentMain.circle1.visibility = View.VISIBLE
            navController.popBackStack()
            navController.navigate(R.id.nav_home)
        }

        binding.appBarMain.contentMain.popularBtn.setOnClickListener {
            hideCircles()
            binding.appBarMain.contentMain.circle2.visibility = View.VISIBLE
            navController.popBackStack()
            navController.navigate(R.id.nav_popular)
        }

        binding.appBarMain.contentMain.randomBtn.setOnClickListener {
            hideCircles()
            binding.appBarMain.contentMain.circle3.visibility = View.VISIBLE
            navController.popBackStack()
            navController.navigate(R.id.nav_random)
        }

        binding.appBarMain.contentMain.likeBtn.setOnClickListener {
            hideCircles()
            binding.appBarMain.contentMain.circle4.visibility = View.VISIBLE
            navController.popBackStack()
            navController.navigate(R.id.nav_liked)
        }

        binding.navView.setNavigationItemSelectedListener { item ->
            navController.popBackStack()
            if (item.itemId == R.id.nav_home) {
                hideCircles()
                binding.appBarMain.contentMain.circle1.visibility = View.VISIBLE
                binding.appBarMain.contentMain.cardBarView.visibility = View.VISIBLE
                navController.navigate(R.id.nav_home)
                drawerLayout.closeDrawers()
            } else if (item.itemId == R.id.nav_popular) {
                hideCircles()
                binding.appBarMain.contentMain.circle2.visibility = View.VISIBLE
                binding.appBarMain.contentMain.cardBarView.visibility = View.VISIBLE
                navController.navigate(R.id.nav_popular)
                drawerLayout.closeDrawers()
            } else if (item.itemId == R.id.nav_random) {
                hideCircles()
                binding.appBarMain.contentMain.circle3.visibility = View.VISIBLE
                binding.appBarMain.contentMain.cardBarView.visibility = View.VISIBLE
                navController.navigate(R.id.nav_random)
                drawerLayout.closeDrawers()
            } else if (item.itemId == R.id.nav_liked) {
                hideCircles()
                binding.appBarMain.contentMain.circle4.visibility = View.VISIBLE
                binding.appBarMain.contentMain.cardBarView.visibility = View.VISIBLE
                navController.navigate(R.id.nav_liked)
                drawerLayout.closeDrawers()
            } else if (item.itemId == R.id.nav_history) {
                binding.appBarMain.contentMain.cardBarView.visibility = View.INVISIBLE
                navController.navigate(R.id.nav_history)
                drawerLayout.closeDrawers()
            } else if (item.itemId == R.id.nav_about) {
                binding.appBarMain.contentMain.cardBarView.visibility = View.INVISIBLE
                navController.navigate(R.id.nav_about)
                drawerLayout.closeDrawers()
            }
            false
        }

        navController.addOnDestinationChangedListener { navController, navDestination, bundle ->
            if (navDestination.id == R.id.imageInfoFragment) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                hide()
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun hideCircles() {
        binding.appBarMain.contentMain.circle1.visibility = View.GONE
        binding.appBarMain.contentMain.circle2.visibility = View.GONE
        binding.appBarMain.contentMain.circle3.visibility = View.GONE
        binding.appBarMain.contentMain.circle4.visibility = View.GONE
    }

    fun hide() {
        supportActionBar?.hide()
        binding.appBarMain.contentMain.cardBarView.visibility = View.INVISIBLE
    }

    fun hideCard() {
        binding.appBarMain.contentMain.cardBarView.visibility = View.INVISIBLE
    }

    fun show() {
        supportActionBar?.show()
        binding.appBarMain.contentMain.cardBarView.visibility = View.VISIBLE
    }
}