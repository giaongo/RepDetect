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
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.poseexercise.views.fragment.HomeFragment
import com.example.poseexercise.views.fragment.PlanFragment
import com.example.poseexercise.views.fragment.ProfileFragment
import com.example.poseexercise.R
import com.example.poseexercise.views.fragment.WorkoutFragment

class MainActivity : AppCompatActivity() {
    private lateinit var prefManager: PrefManager
    private lateinit var bottomNavigation: MeowBottomNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_main)

        prefManager = PrefManager(this)
        if (prefManager.isFirstTimeLaunch()) {
            // Show the onboarding screen
            startActivity(Intent(this, OnboardingActivity::class.java))
            // Close the main activity
            finish()
        }

        bottomNavigation = findViewById(R.id.bottomNavigation)
        switchFragment(HomeFragment())

        // Sets home page as default page and adds clicked animation to home button
        bottomNavigation.show(1, true)
        setupBottomNavigation()
        handleBottomNavigation()

        // Set up ListView and Adapter
//        val listView = findViewById<ListView>(R.id.test_activity_list_view)
//        val adapter = MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES)
//        adapter.setDescriptionIds(DESCRIPTION_IDS)
//        listView.adapter = adapter
//        listView.onItemClickListener = this
    }
    private fun setupBottomNavigation() {
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.home))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.workout))
        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.plan))
        bottomNavigation.add(MeowBottomNavigation.Model(4, R.drawable.profile))
    }

    private fun handleBottomNavigation(){
        bottomNavigation.setOnClickMenuListener { item ->
            when (item.id) {
                1 -> switchFragment(HomeFragment())
                2 -> switchFragment(WorkoutFragment())
                3 -> switchFragment(PlanFragment())
                4 -> switchFragment(ProfileFragment())
            }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null) // Optional: Add the transaction to the back stack
        fragmentTransaction.commit()
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