package com.example.poseexercise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.poseexercise.R

/**
 * Initialize the ExerciseGifAdapter class, responsible for managing the GIFs in the RecyclerView
 */
class ExerciseGifAdapter(
    private val exerciseGifs: List<Pair<String, Int>>,
    private val onSkipClickListener: () -> Unit
) : RecyclerView.Adapter<ExerciseGifAdapter.ViewHolder>() {

    // Create ViewHolder instances for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gif, parent, false)
        return ViewHolder(view)
    }

    // Bind the data to the ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (_, gifResourceId) = exerciseGifs[position]
        // Load and display the GIF using a library like Glide or any GIF library
        Glide.with(holder.itemView.context)
            .load(gifResourceId)
            .into(holder.imageView)

        // Set onClickListener for the Skip button
        holder.skipButton.setOnClickListener {
            onSkipClickListener.invoke()
        }
    }

    // Return the total number of items in the RecyclerView
    override fun getItemCount(): Int {
        return exerciseGifs.size
    }

    // ViewHolder class holds references to the UI components for each item
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.exerciseGifImageView)
        val skipButton: Button = itemView.findViewById(R.id.skipButton)
    }
}