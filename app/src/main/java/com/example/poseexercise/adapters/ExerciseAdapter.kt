package com.example.poseexercise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.poseexercise.R
import com.example.poseexercise.data.Exercise
import com.google.android.material.chip.Chip

class Adapter(private val exerciselist: List<Exercise>) : RecyclerView.Adapter<Adapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_exercise_type, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return exerciselist.size
    }

    // This method binds the data to the ViewHolder object
    // for each item in the RecyclerView
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentExercise = exerciselist[position]

        holder.name.text = currentExercise.name
        currentExercise.image?.let { holder.image.setImageResource(it) }
        holder.level.text = currentExercise.level
        holder.calorieBurned.text = "${currentExercise.calorie} kCal"
    }

    // This class defines the ViewHolder object for each item in the RecyclerView
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentExercise: Exercise? = null

        val name: TextView = itemView.findViewById(R.id.exercise_name)
        val image: ImageView = itemView.findViewById(R.id.exercise_image)
        val level: Chip = itemView.findViewById(R.id.chip)
        val calorieBurned: TextView = itemView.findViewById(R.id.exercise_calories)
    }
}


