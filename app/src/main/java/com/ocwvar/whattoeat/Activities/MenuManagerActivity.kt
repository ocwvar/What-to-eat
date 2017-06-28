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
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
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
                finish()
            }
        }
        return true
    }

    override fun onClick(view: View) {
        when (view.id) {
        //点击浮动按钮
            R.id.fab -> {
                //启动菜单编辑页面进行菜单新建操作
                startActivity(Intent(this@MenuManagerActivity, MenuEditActivity::class.java).let {
                    it.action = MenuEditActivity.ACTIONS.ACTION_CREATE
                    it
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
        supportActionBar?.subtitle = String.format("%s%d", getString(R.string.menu_list_subTitle_header), DATA.menus.size)
    }

    override fun onClick(menu: Menu, position: Int) {
        //显示是否编辑菜单对话框
        AlertDialog.Builder(this@MenuManagerActivity)
                .setMessage(R.string.menu_list_dialog_edit_title)
                .setPositiveButton(R.string.menu_list_dialog_edit_yes, { p0, p1 ->
                    p0.dismiss()
                    //启动菜单编辑页面进行菜单编辑操作
                    val animBundle: Bundle?
                    if (Build.VERSION.SDK_INT >= 21) {
                        animBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                this@MenuManagerActivity,
                                Pair<View, String>(findViewById(R.id.fab), "fab")
                        ).toBundle()
                    } else {
                        animBundle = null
                    }

                    val intent: Intent = Intent(this@MenuManagerActivity, MenuEditActivity::class.java)
                    intent.action = MenuEditActivity.ACTIONS.ACTION_EDIT
                    intent.putExtra(MenuEditActivity.ACTIONS.EXTRAS_PARCELABLE_OBJECT, menu.copy())
                    startActivity(intent, animBundle)
                })
                .setNegativeButton(R.string.menu_list_dialog_edit_no, { p0, p1 ->
                    p0.dismiss()
                })
                .show()
    }

    override fun onLongClick(menu: Menu, position: Int): Boolean {
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

}