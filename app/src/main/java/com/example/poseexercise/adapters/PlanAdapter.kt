package com.example.poseexercise.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.poseexercise.R
import com.example.poseexercise.data.plan.Plan
import com.example.poseexercise.posedetector.classification.PoseClassifierProcessor.CHEST_PRESS_CLASS
import com.example.poseexercise.posedetector.classification.PoseClassifierProcessor.DEAD_LIFT_CLASS
import com.example.poseexercise.posedetector.classification.PoseClassifierProcessor.LUNGES_CLASS
import com.example.poseexercise.posedetector.classification.PoseClassifierProcessor.PUSHUPS_CLASS
import com.example.poseexercise.posedetector.classification.PoseClassifierProcessor.SHOULDER_PRESS_CLASS
import com.example.poseexercise.posedetector.classification.PoseClassifierProcessor.SITUP_UP_CLASS
import com.example.poseexercise.posedetector.classification.PoseClassifierProcessor.SQUATS_CLASS
import com.example.poseexercise.posedetector.classification.PoseClassifierProcessor.WARRIOR_CLASS
import com.example.poseexercise.posedetector.classification.PoseClassifierProcessor.YOGA_TREE_CLASS
import com.example.poseexercise.util.MyUtils.Companion.databaseNameToClassification
import java.util.Collections

class PlanAdapter internal constructor(context: Context):
    RecyclerView.Adapter<PlanAdapter.ViewHolder>(){
    val TAG = "RepDetect Debug - Plan Adapter"
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var planList: MutableList<Plan> = Collections.emptyList()
    private lateinit var listener: ItemListener



    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val workoutImage: ImageView = itemView.findViewById(R.id.imageView)
        val workoutName: TextView = itemView.findViewById(R.id.exercise_title)
        val repeat: TextView = itemView.findViewById(R.id.exercise_rep)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = inflater.inflate(R.layout.plan_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return planList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPlan = planList[position]
        Log.d(TAG,currentPlan.exercise)
        holder.workoutImage.setImageResource(getDrawableResourceIdExercise(databaseNameToClassification(currentPlan.exercise)))
        holder.workoutName.text = currentPlan.exercise
        holder.repeat.text = "${currentPlan.repeatCount} ${currentPlan.exercise} a day"
        holder.deleteButton.setOnClickListener {
            listener.onItemClicked(currentPlan.id, position)
            notifyDataSetChanged()
        }
    }

    interface ItemListener {
        fun onItemClicked(planId: Int, position: Int)
    }

    fun setListener(listener: ItemListener){
        this.listener = listener
    }

    fun setPlans(plans: MutableList<Plan>) {
        this.planList = plans
        notifyDataSetChanged()
    }

    /**
     * List of yoga images
     */
    private val exerciseImages = mapOf(
        SQUATS_CLASS to  R.drawable.squat,
        LUNGES_CLASS to  R.drawable.reverse_lunges,
        SITUP_UP_CLASS to  R.drawable.sit_ups,
        PUSHUPS_CLASS to  R.drawable.push_up,
        CHEST_PRESS_CLASS to  R.drawable.chest_press,
        DEAD_LIFT_CLASS to  R.drawable.dead_lift,
        SHOULDER_PRESS_CLASS to  R.drawable.shoulder_press
    )

    private fun getDrawableResourceIdExercise(exerciseKey: String): Int {
        return exerciseImages[exerciseKey] ?: throw IllegalArgumentException("Invalid yoga pose key: $exerciseKey")
    }

}