package com.example.poseexercise.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.poseexercise.R
import com.example.poseexercise.data.plan.Plan

class PlanAdapter internal constructor(context: Context):
    RecyclerView.Adapter<PlanAdapter.ViewHolder>(){
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var planList: List<Plan> = emptyList()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val workoutName: TextView = itemView.findViewById(R.id.exercise_title)
        val repeat: TextView = itemView.findViewById(R.id.exercise_rep)
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
        holder.workoutName.text = currentPlan.exercise
        holder.repeat.text = "${currentPlan.repeatCount} ${currentPlan.exercise} a day"
    }

    fun setPlans(plans: List<Plan>) {
        this.planList = plans
        notifyDataSetChanged()
    }

}