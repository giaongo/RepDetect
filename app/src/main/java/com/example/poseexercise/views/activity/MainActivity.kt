package com.example.poseexercise.views.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.poseexercise.R

class MainActivity : AppCompatActivity() {
    private lateinit var prefManager: PrefManager
    private lateinit var bottomNavigation: MeowBottomNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_main)

        prefManager = PrefManager(this)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.home))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.workout))
        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.plan))
        bottomNavigation.add(MeowBottomNavigation.Model(4, R.drawable.profile))

        bottomNavigation.setOnClickMenuListener { item ->
            when (item.id) {
                1 -> navController.navigate(R.id.homeFragment)
                2 -> navController.navigate(R.id.workoutFragment)
                3 -> navController.navigate(R.id.planFragment)
                4 -> navController.navigate(R.id.profileFragment)
            }
        }


        if (prefManager.isFirstTimeLaunch()) {
            // Show the onboarding screen
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()  // Close the main activity
        }

        // Set up ListView and Adapter
//        val listView = findViewById<ListView>(R.id.test_activity_list_view)
//        val adapter = MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES)
//        adapter.setDescriptionIds(DESCRIPTION_IDS)
//        listView.adapter = adapter
//        listView.onItemClickListener = this

    }


    private class MyArrayAdapter(
        private val ctx: Context,
        resource: Int,
        private val classes: Array<Class<*>>
    ) : ArrayAdapter<Class<*>>(ctx, resource, classes) {
        private var descriptionIds: IntArray? = null

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView

            if (convertView == null) {
                val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(android.R.layout.simple_list_item_2, null)
            }

            (view!!.findViewById<View>(android.R.id.text1) as TextView).text =
                classes[position].simpleName
            descriptionIds?.let {
                (view.findViewById<View>(android.R.id.text2) as TextView).setText(it[position])
            }

            return view
        }

        fun setDescriptionIds(descriptionIds: IntArray) {
            this.descriptionIds = descriptionIds
        }
    }




    companion object {
        private const val TAG = "ChooserActivity"
        private val CLASSES =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                arrayOf<Class<*>>(
                    LivePreviewActivity::class.java,
                )
            else
                arrayOf<Class<*>>(
                    LivePreviewActivity::class.java,
                    CameraXLivePreviewActivity::class.java,
                )
        private val DESCRIPTION_IDS =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                intArrayOf(
                    R.string.desc_camera_source_activity,
                )
            else
                intArrayOf(
                    R.string.desc_camera_source_activity,
                    R.string.desc_camerax_live_preview_activity,
                )
    }
}