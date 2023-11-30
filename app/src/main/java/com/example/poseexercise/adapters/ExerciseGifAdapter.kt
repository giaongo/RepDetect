package com.example.poseexercise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.poseexercise.R


class ExerciseGifAdapter(
    private val exerciseGifs: List<Pair<String, Int>>,
    private val onSkipClickListener: () -> Unit
) : RecyclerView.Adapter<ExerciseGifAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gif, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (exerciseType, gifResourceId) = exerciseGifs[position]
        // Load and display the GIF using a library like Glide or any GIF library
        Glide.with(holder.itemView.context)
            .load(gifResourceId)
            .into(holder.imageView)

        // Set onClickListener for the Skip button
        holder.skipButton.setOnClickListener {
            onSkipClickListener.invoke()
        }
    }

    override fun getItemCount(): Int {
        return exerciseGifs.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.exerciseGifImageView)
        val skipButton: Button = itemView.findViewById(R.id.skipButton)
    }
}