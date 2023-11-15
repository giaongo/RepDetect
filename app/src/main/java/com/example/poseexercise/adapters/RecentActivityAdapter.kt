package com.example.poseexercise.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.poseexercise.R
import com.example.poseexercise.data.results.RecentActivityItem

class RecentActivityAdapter(private var recentActivityList: List<RecentActivityItem>) :
    RecyclerView.Adapter<RecentActivityAdapter.MyViewHolder>() {

    // Inflates the layout for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_recent_activity, parent, false)
        return MyViewHolder(itemView)
    }

    // Binds data to the views in each ViewHolder
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = recentActivityList[position]

        // Set data to views
        holder.exerciseImage.setImageResource(currentItem.imageResId)
        holder.exerciseType.text = currentItem.exerciseType
        holder.reps.text = currentItem.reps
    }

    // Returns the number of items in the RecyclerView
    override fun getItemCount(): Int {
        return recentActivityList.size
    }

    // ViewHolder class to hold references to views for each item in the RecyclerView
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseImage: ImageView = itemView.findViewById(R.id.imageView)
        val exerciseType: TextView = itemView.findViewById(R.id.textViewExerciseType)
        val reps: TextView = itemView.findViewById(R.id.textViewReps)
    }

    // Method to update the data in the adapter
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<RecentActivityItem>) {
        recentActivityList = newData
        notifyDataSetChanged()// Notifies the adapter that the data set has changed
    }

}

