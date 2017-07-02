package com.ocwvar.darkpurple.Units

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.ocwvar.whattoeat.R
import java.lang.ref.WeakReference

/**
 * Project DarkPurple
 * Created by OCWVAR
 * On 2017/05/30 5:16 PM
 * File Location com.ocwvar.darkpurple.Units
 * This file use to :   自定义Toast显示器
 */
object ToastMaker {

    //Toast背景颜色
    val TOAST_COLOR_NORMAL: Int = Color.rgb(80, 80, 80)
    val TOAST_COLOR_WARNING: Int = Color.rgb(108, 0, 0)

    //Toast布局缓存容器
    private var layoutKeeper: WeakReference<TextView?> = WeakReference(null)

    /**
     * 显示Toast
     * @param   resource 要显示的内容资源ID
     * @param   color   背景颜色,自定义颜色或默认配置颜色
     * @see TOAST_COLOR_NORMAL
     * @see TOAST_COLOR_WARNING
     */
    fun show(context: Context, @StringRes resource: Int, @ColorInt color: Int) {
        show(context, context.getString(resource), color)
    }

    /**
     * 显示Toast
     * @param   message 要显示的内容
     */
    fun show(context: Context, message: String, @ColorInt color: Int) {
        val context: Context = context.applicationContext
        var toastTextView: TextView? = layoutKeeper.get()

        if (toastTextView == null) {
            toastTextView = LayoutInflater.from(context).inflate(R.layout.toast_layout, null) as TextView
            layoutKeeper = WeakReference(toastTextView)
        }
        toastTextView.text = message
        toastTextView.setBackgroundColor(color)

        val toast: Toast = Toast(context)
        toast.view = toastTextView
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

}