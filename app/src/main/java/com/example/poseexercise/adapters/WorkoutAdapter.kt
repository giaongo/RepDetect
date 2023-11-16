package com.example.poseexercise.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.poseexercise.R
import com.example.poseexercise.data.plan.ExerciseData
import com.example.poseexercise.data.plan.ExercisePlan
import com.example.poseexercise.util.MyUtils.Companion.exerciseNameToDisplay
import com.example.poseexercise.viewmodels.WorkoutViewHolder

class WorkoutAdapter(
    private val exerciseList: List<ExerciseData>,
    private val workoutPlan: List<ExercisePlan>
) : RecyclerView.Adapter<WorkoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.exercise_item, parent, false)
        return WorkoutViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val currentExercise = exerciseList[position]

        val repetition: Int? = workoutPlan.find {
            it.exerciseName.equals(
                currentExercise.exerciseName,
                ignoreCase = true
            )
        }?.repetitions

        if (currentExercise.isComplete) {
            holder.exerciseNameTextView.setTextColor(Color.GREEN)
            holder.repetitionsTextView.setTextColor(Color.GREEN)
            holder.isCompleteTextView.setTextColor(Color.GREEN)
        }
        holder.exerciseNameTextView.text =
            "${exerciseNameToDisplay(currentExercise.exerciseName)}: "
        holder.repetitionsTextView.text = if (repetition != null) {
            "${currentExercise.repetitions}/${repetition} "
        } else {
            "${currentExercise.repetitions}"
        }
        holder.isCompleteTextView.text = if (currentExercise.isComplete) {
            "Complete"
        } else {
            ""
        }
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

}