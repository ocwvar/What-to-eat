package com.ocwvar.whattoeat.Activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.ocwvar.darkpurple.Units.ToastMaker
import com.ocwvar.whattoeat.Adapter.MenuListAdapter
import com.ocwvar.whattoeat.R
import com.ocwvar.whattoeat.Unit.DATA
import com.ocwvar.whattoeat.Unit.DATAHelper
import com.ocwvar.whattoeat.Unit.Menu

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-26 下午3:12
 * File Location com.ocwvar.whattoeat
 * This file use to :   菜单管理页面
 */
class MenuManagerActivity : AppCompatActivity(), MenuListAdapter.Callback, View.OnClickListener {

    private val adapter: MenuListAdapter = MenuListAdapter(this@MenuManagerActivity)
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= 21) {

        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_manager)

        title = getString(R.string.menu_list_name)

        //ToolBar属性设置
        (findViewById(R.id.toolbar) as Toolbar).let {
            setSupportActionBar(it)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        //FAB属性设置
        (findViewById(R.id.fab) as FloatingActionButton).let {
            it.setOnClickListener(this@MenuManagerActivity)
            fab = it
        }

        //RecycleView属性设置
        (findViewById(R.id.recycleView) as RecyclerView).let {
            it.setOnTouchListener { view, motionEvent ->
                //滑动列表时隐藏FAB，抬起手指重新显示
                when (motionEvent.action) {
                    MotionEvent.ACTION_MOVE -> {
                        fab.hide()
                    }
                    MotionEvent.ACTION_UP -> {
                        fab.show()
                    }
                }
                false
            }
            it.layoutManager = LinearLayoutManager(this@MenuManagerActivity, LinearLayoutManager.VERTICAL, false)
            it.setHasFixedSize(true)
            it.adapter = adapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        //点击ActionBar上的返回按钮
            android.R.id.home -> {
                if (Build.VERSION.SDK_INT >= 21) {
                    finishAfterTransition()
                } else {
                    finish()
                }
            }
        }
        return true
    }

    override fun onClick(view: View) {
        when (view.id) {
        //点击浮动按钮
            R.id.fab -> {
                //启动菜单编辑页面进行菜单新建操作
                val animBundle: Bundle?
                if (Build.VERSION.SDK_INT >= 21) {
                    animBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this@MenuManagerActivity,
                            Pair<View, String>(findViewById(R.id.fab), "fab")
                    ).toBundle()
                } else {
                    animBundle = null
                }
                startActivity(Intent(this@MenuManagerActivity, MenuEditActivity::class.java).let {
                    it.action = MenuEditActivity.ACTIONS.ACTION_CREATE
                    it
                }, animBundle)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
        supportActionBar?.subtitle = String.format("%s%d", getString(R.string.menu_list_subTitle_header), DATA.menus.size)
    }

    override fun onClick(menu: Menu, position: Int, itemView: View) {
        //显示是否编辑菜单对话框
        AlertDialog.Builder(this@MenuManagerActivity)
                .setMessage(R.string.menu_list_dialog_edit_title)
                .setPositiveButton(R.string.menu_list_dialog_edit_edit, { p0, p1 ->
                    p0.dismiss()
                    //启动菜单编辑页面进行菜单编辑操作
                    val animBundle: Bundle?
                    if (Build.VERSION.SDK_INT >= 21) {
                        animBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                this@MenuManagerActivity,
                                Pair<View, String>(findViewById(R.id.fab), "fab"),
                                Pair<View, String>(itemView.findViewById(R.id.item_menu_title), "title")
                        ).toBundle()
                    } else {
                        animBundle = null
                    }

                    val intent: Intent = Intent(this@MenuManagerActivity, MenuEditActivity::class.java)
                    intent.action = MenuEditActivity.ACTIONS.ACTION_EDIT
                    intent.putExtra(MenuEditActivity.ACTIONS.EXTRAS_PARCELABLE_OBJECT, menu)
                    startActivity(intent, animBundle)
                })
                .setNegativeButton(R.string.menu_list_dialog_edit_option, { p0, p1 ->
                    p0.dismiss()
                    //显示属性修改对话框
                    OptionChangeDialog().show(position, menu)
                })
                .show()
    }

    override fun onLongClick(menu: Menu, position: Int, itemView: View): Boolean {
        //显示是否删除菜单对话框
        AlertDialog.Builder(this@MenuManagerActivity)
                .setMessage(R.string.menu_list_dialog_delete_title)
                .setPositiveButton(R.string.menu_list_dialog_delete_yes, { p0, p1 ->
                    p0.dismiss()
                    DATAHelper(this@MenuManagerActivity).removeMenu(menu)
                    adapter.notifyItemRemoved(position)
                })
                .setNegativeButton(R.string.menu_list_dialog_delete_no, { p0, p1 ->
                    p0.dismiss()
                })
                .show()
        return true
    }

    /**
     * 菜单属性修改对话框
     */
    private inner class OptionChangeDialog {

        private var randomCount: Int = 0
        private var isEnabled: Boolean = false

        private lateinit var menu: Menu
        private lateinit var totalCount: TextView
        private lateinit var randomCountInput: EditText
        private lateinit var isEnableBox: CheckBox

        fun show(listPosition: Int, menu: Menu) {
            //保存临时属性
            this.menu = menu
            this.randomCount = DATA.indexRandomCount(menu.title)
            this.isEnabled = DATA.indexEnable(menu.title)

            val dialogView: View = LayoutInflater.from(this@MenuManagerActivity).inflate(R.layout.dialog_menu_option, null)

            this.totalCount = dialogView.findViewById(R.id.dialog_menu_option_random_total)
            this.randomCountInput = dialogView.findViewById(R.id.dialog_menu_option_random_input)
            this.isEnableBox = dialogView.findViewById(R.id.dialog_menu_option_enable_random)

            //设置属性
            this.totalCount.text = String.format("/%d", menu.foods.size)
            this.randomCountInput.setText(randomCount.toString())
            this.isEnableBox.isChecked = isEnabled

            AlertDialog.Builder(this@MenuManagerActivity)
                    .setView(dialogView)
                    .setPositiveButton(R.string.simple_done, { p0, p1 ->
                        //获取用户输入的随机数量，发生异常则数量为 -1
                        var number: Int
                        try {
                            number = randomCountInput.text.toString().toInt()
                        } catch(e: Exception) {
                            number = -1
                        }

                        if (number !in 0..menu.foods.size) {
                            //如果用户输入的随机数量大于数组尺寸或小于0，则为异常
                            ToastMaker.show(this@MenuManagerActivity, R.string.menu_list_ERROR_dialog_option_number, ToastMaker.TOAST_COLOR_WARNING)
                            return@setPositiveButton
                        } else {
                            //应用随机数量
                            DATAHelper(this@MenuManagerActivity).saveCount(menu.title, number)
                        }

                        if (isEnableBox.isChecked != isEnabled) {
                            //如果用户修改了启用随机，则进行更改
                            DATAHelper(this@MenuManagerActivity).updateEnableList(menu.title, isEnableBox.isChecked)
                        }

                        //通知适配器数据变更
                        adapter.notifyItemChanged(listPosition)
                        p0.dismiss()
                    })
                    .show()
        }

    }

}