package com.example.poseexercise.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.poseexercise.R
import com.example.poseexercise.data.plan.Plan
import java.util.Collections

class PlanAdapter internal constructor(context: Context):
    RecyclerView.Adapter<PlanAdapter.ViewHolder>(){
    val TAG = "RepDetect Debug - Plan Adapter"
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var planList: MutableList<Plan> = Collections.emptyList()
    private lateinit var listener: ItemListener

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
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
        holder.workoutName.text = currentPlan.exercise
        holder.repeat.text = "${currentPlan.repeatCount} ${currentPlan.exercise} a day"
        holder.deleteButton.setOnClickListener {
            listener.onItemClicked(currentPlan.id, position)
            notifyDataSetChanged()
            Log.d(TAG, "adapater position is $position")
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

}