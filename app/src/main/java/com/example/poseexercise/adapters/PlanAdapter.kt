package com.example.poseexercise.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.poseexercise.R
import com.example.poseexercise.data.plan.Exercise
import com.example.poseexercise.data.plan.TodaysPlan
import com.google.android.material.card.MaterialCardView

class PlanAdapter(
    private val exercises: List<TodaysPlan>,
    private val navController: NavController
) : RecyclerView.Adapter<PlanAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_todays_plan, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentExercise = exercises[position]
        holder.name.text = currentExercise.name
        holder.repetition.text = currentExercise.reps.toString()
        currentExercise.image?.let { holder.image.setImageResource(it) }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.card)
        val name: TextView = itemView.findViewById(R.id.exercise_name)
        val repetition: TextView = itemView.findViewById(R.id.exercise_reptition)
        val image: ImageView = itemView.findViewById(R.id.exercise_image)
    }
}
