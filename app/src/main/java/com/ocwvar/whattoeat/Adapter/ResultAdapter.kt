package com.ocwvar.whattoeat.Adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
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
 * On 17-6-29 下午11:20
 * File Location com.ocwvar.whattoeat.Adapter
 * This file use to :   结果列表适配器
 */
class ResultAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_MESSAGE: Int = 0
    private val TYPE_RESULT: Int = 1
    private val source: ArrayList<Any> = ArrayList()

    override fun getItemViewType(position: Int): Int {
        if (source[position] is ResultMessage) {
            return TYPE_MESSAGE
        } else if (source[position] is ResultItem) {
            return TYPE_RESULT
        } else {
            return -1
        }
    }

    /**
     * 清空列表
     */
    fun clearSrouce() {
        source.clear()
    }

    /**
     * 添加数据源
     * @param   messageObject    文字数据对象
     */
    fun addSource(messageObject: ResultMessage) {
        source.add(messageObject)
    }

    /**
     * 添加数据源
     * @param   resultObject    结果数据对象
     */
    fun addSource(resultObject: ResultItem) {
        source.add(resultObject)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        parent ?: return null
        when (viewType) {
            TYPE_MESSAGE -> {
                return MessageViewHolder(TextView(parent.context))
            }

            TYPE_RESULT -> {
                return ResultItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false))
            }

            else -> {
                return null
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        holder ?: return
        if (holder is MessageViewHolder) {
            //设定文字显示
            val messageObject: ResultMessage = source[position] as ResultMessage
            holder.textView.text = messageObject.message
            holder.textView.textSize = messageObject.textSize
        } else {
            //设定结果条目显示
            val views: ResultItemViewHolder = holder as ResultItemViewHolder
            val resultObject: ResultItem = source[position] as ResultItem

            views.owner.text = String.format("%s%s", views.itemView.context.getString(R.string.result_owner_header), resultObject.ownerTitle)
            views.foodTitle.text = resultObject.food.title
            views.foodMessage.text = resultObject.food.message

            views.icon.visibility = View.GONE
            if (!TextUtils.isEmpty(resultObject.food.icon)) {
                TODO("添加图像读取处")
            }
        }
    }

    override fun getItemCount(): Int = source.size

    /**
     * 文字消息对象
     */
    data class ResultMessage(val message: String, val textSize: Float = 12.0f)

    /**
     * 结果数据对象
     */
    data class ResultItem(val food: Food, val ownerTitle: String)


    private class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textView: TextView = itemView as TextView

        init {
            textView.setTextColor(Color.WHITE)
            textView.setPadding(70, 30, 10, 20)
            textView.textSize = 14.0f
        }

    }

    private class ResultItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.item_result_icon)
        val owner: TextView = itemView.findViewById(R.id.item_result_ownerTitle)
        val foodTitle: TextView = itemView.findViewById(R.id.item_result_foodTitle)
        val foodMessage: TextView = itemView.findViewById(R.id.item_result_foodMessage)
    }

}