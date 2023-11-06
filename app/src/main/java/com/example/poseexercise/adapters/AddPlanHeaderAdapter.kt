package com.example.poseexercise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.poseexercise.R

class AddPlanHeaderAdapter: RecyclerView.Adapter<AddPlanHeaderAdapter.HeaderViewHolder>() {

    /* ViewHolder for displaying header. */
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }

    /* Inflates view and returns HeaderViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.header_add_plan, parent, false)
        return HeaderViewHolder(view)
    }

    /* Binds data to the header. */
    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {

    }

    /* Returns number of items, since there is only one item in the header return one  */
    override fun getItemCount(): Int {
        return 1
    }

    /* Updates header to display select level when a level is choose. */

}