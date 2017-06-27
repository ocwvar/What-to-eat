package com.ocwvar.whattoeat.Adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ocwvar.whattoeat.R
import com.ocwvar.whattoeat.Unit.Food
import java.util.*

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-27 下午5:25
 * File Location com.ocwvar.whattoeat.Adapter
 * This file use to :   食物列表适配器
 */
class FoodListAdapter(val foods: ArrayList<Food>, val callback: Callback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        holder ?: return
        val foodObject: Food = foods[position]
        val views: FoodViewHolder = holder as FoodViewHolder
        views.title.text = foodObject.title
        views.message.text = foodObject.message
        views.icon.visibility = View.GONE
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        parent ?: return null
        return FoodViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false))
    }

    override fun getItemCount(): Int = foods.size

    interface Callback {
        fun onClick(food: Food, position: Int)
        fun onLongClick(food: Food, position: Int): Boolean
    }

    private inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.item_food_title)
        val message: TextView = itemView.findViewById(R.id.item_food_message)
        val icon: ImageView = itemView.findViewById(R.id.item_food_icon)

        init {
            itemView.setOnClickListener {
                callback.onClick(foods[adapterPosition], adapterPosition)
            }
            itemView.setOnLongClickListener {
                callback.onLongClick(foods[adapterPosition], adapterPosition)
            }
        }

    }

}