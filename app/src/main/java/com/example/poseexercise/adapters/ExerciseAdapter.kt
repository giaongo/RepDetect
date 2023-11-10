package com.example.poseexercise.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.poseexercise.R
import com.example.poseexercise.data.Exercise
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip

class ExerciseAdapter(
    private val exercises: List<Exercise>,
    private val navController: NavController) :
    RecyclerView.Adapter<ExerciseAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_exercise_type, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    // This method binds the data to the ViewHolder object
    // for each item in the RecyclerView
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentExercise = exercises[position]
        holder.name.text = currentExercise.name
        currentExercise.image?.let { holder.image.setImageResource(it) }
        holder.level.text = currentExercise.level
        holder.calorieBurned.text = "${currentExercise.calorie} kCal"

        // Set a click event listener for the CardView
        holder.cardView.setOnClickListener {
            // Navigate to another fragment using the NavController
            navController.navigate(R.id.action_planStepOneFragment_to_planStepTwoFragment)
        }
    }

    // This class defines the ViewHolder object for each item in the RecyclerView
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentExercise: Exercise? = null

        val cardView: MaterialCardView = itemView.findViewById(R.id.card)
        val name: TextView = itemView.findViewById(R.id.exercise_name)
        val image: ImageView = itemView.findViewById(R.id.exercise_image)
        val level: Chip = itemView.findViewById(R.id.chip)
        val calorieBurned: TextView = itemView.findViewById(R.id.exercise_calories)

    }
}


