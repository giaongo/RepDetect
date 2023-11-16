package com.example.poseexercise.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.poseexercise.R
import com.example.poseexercise.data.plan.Exercise
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip

class ExerciseAdapter internal constructor(context: Context, private val navController: NavController):
    RecyclerView.Adapter<ExerciseAdapter.MyViewHolder>(), Filterable {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var exerciseList: List<Exercise> = emptyList()
    var exerciseFilterList = mutableListOf<Exercise>()

    // This class defines the ViewHolder object for each item in the RecyclerView
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            exerciseFilterList = exerciseList as MutableList<Exercise>
        }
        val cardView: MaterialCardView = itemView.findViewById(R.id.card)
        val name: TextView = itemView.findViewById(R.id.exercise_name)
        val image: ImageView = itemView.findViewById(R.id.exercise_image)
        val level: Chip = itemView.findViewById(R.id.chip)
        val calorieBurned: TextView = itemView.findViewById(R.id.exercise_calories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_exercise_type, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return exerciseFilterList.size
    }

    // This method binds the data to the ViewHolder object
    // for each item in the RecyclerView
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentExercise = exerciseFilterList[position]
        holder.name.text = currentExercise.name
        currentExercise.image?.let { holder.image.setImageResource(it) }
        holder.level.text = currentExercise.level
        holder.calorieBurned.text = "${currentExercise.calorie} kCal"
        holder.level.isCheckable = false

        // Set a click event listener for the CardView
        holder.cardView.setOnClickListener {
            // Create a bundle to hold data that need to pass to next fragment
            val bundle = bundleOf(
                "exerciseName" to currentExercise.name,
                "caloriesPerRep" to currentExercise.calorie
            )
            // Navigate to another fragment using the NavController
            navController.navigate(R.id.action_planStepOneFragment_to_planStepTwoFragment, bundle)
        }
    }

    fun setExercises(exercises: List<Exercise>) {
        this.exerciseList = exercises
        exerciseFilterList = exercises as MutableList<Exercise>
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isNullOrEmpty()) {
                    exerciseFilterList = exerciseList as MutableList<Exercise>
                } else {
                    val resultList = mutableListOf<Exercise>()
                    for (row in exerciseList) {
                        if (row.level.lowercase().contains(charSearch.lowercase())) {
                            resultList.add(row)
                        }
                    }
                    exerciseFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = exerciseFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                exerciseFilterList = results?.values as MutableList<Exercise>
                notifyDataSetChanged()
            }
        }
    }
}


