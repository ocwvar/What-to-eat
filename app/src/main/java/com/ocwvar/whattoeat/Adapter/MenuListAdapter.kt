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
 * On 17-6-26 下午2:50
 * File Location com.ocwvar.whattoeat.Adapter
 * This file use to :   菜单列表适配器
 */
class MenuListAdapter(val callback: Callback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        parent ?: return null
        return MenuViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        holder ?: return
        //菜单数据
        val menuObject: Menu = DATA.menus[position]
        //随机数值
        val randomCount: Int = DATA.indexRandomCount(menuObject.title)
        //是否已启用
        val isEnabled: Boolean = DATA.indexEnable(menuObject.title)
        //View的数据
        val views: MenuViewHolder = holder as MenuViewHolder

        views.title.text = menuObject.title
        views.message.text = menuObject.message
        views.count.text = String.format("%d/%d", randomCount, menuObject.foods.size)

        //默认未启用状态样式
        views.status.setBackgroundColor(views.itemView.resources.getColor(R.color.colorPrimary))
        if (isEnabled) {
            //已启用样式
            views.status.setBackgroundColor(views.itemView.resources.getColor(R.color.colorAccent))
        }
    }

    override fun getItemCount(): Int = DATA.menus.size

    interface Callback {
        fun onClick(menu: Menu, position: Int, itemView: View)
        fun onLongClick(menu: Menu, position: Int, itemView: View): Boolean
    }

    private inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.item_menu_title)
        val message: TextView = itemView.findViewById(R.id.item_menu_message)
        val status: View = itemView.findViewById(R.id.item_menu_status_bar)
        val count: TextView = itemView.findViewById(R.id.item_menu_count)

        init {
            itemView.setOnClickListener {
                callback.onClick(DATA.menus[adapterPosition], adapterPosition, itemView)
            }
            itemView.setOnLongClickListener {
                callback.onLongClick(DATA.menus[adapterPosition], adapterPosition, itemView)
            }
        }

    }

}