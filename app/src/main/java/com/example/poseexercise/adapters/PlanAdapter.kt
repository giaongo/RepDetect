package com.example.poseexercise.adapters

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.poseexercise.R
import com.example.poseexercise.data.plan.Plan
import com.example.poseexercise.viewmodels.HomeViewModel
import java.util.Date

class PlanAdapter internal constructor(context: Context, homeViewModel: HomeViewModel):
    RecyclerView.Adapter<PlanAdapter.ViewHolder>(){
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val homeViewModel: HomeViewModel = homeViewModel
    private var planList: List<Plan> = emptyList()
    var today : String = DateFormat.format("EEEE" , Date()) as String

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val workoutName = itemView.findViewById<TextView>(R.id.exercise_title)
        val repeat = itemView.findViewById<TextView>(R.id.exercise_rep)
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