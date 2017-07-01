package com.ocwvar.whattoeat.Adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ocwvar.whattoeat.R
import com.ocwvar.whattoeat.Unit.DATA
import com.ocwvar.whattoeat.Unit.Menu

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-7-1 下午7:40
 * File Location com.ocwvar.whattoeat.Adapter
 * This file use to :   记录列表适配器
 */
class RecordAdapter(val callBack: CallBack) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        parent ?: return null
        return RecordViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false))
    }

    override fun getItemCount(): Int = DATA.records.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        holder ?: return
        val views: RecordViewHolder = holder as RecordViewHolder
        val recordObject: Menu = DATA.records[position]

        views.name.text = recordObject.title
        views.message.text = recordObject.message
    }

    interface CallBack {
        fun onClick(record: Menu, position: Int)
        fun onLongClick(record: Menu, position: Int)
    }

    private inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.item_record_name)
        val message: TextView = itemView.findViewById(R.id.item_record_message)

        init {
            itemView.setOnClickListener {
                callBack.onClick(DATA.records[adapterPosition], adapterPosition)
            }
            itemView.setOnLongClickListener {
                callBack.onLongClick(DATA.records[adapterPosition], adapterPosition)
                true
            }
        }

    }

}