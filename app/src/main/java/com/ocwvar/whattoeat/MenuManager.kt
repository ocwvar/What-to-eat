package com.ocwvar.whattoeat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ocwvar.whattoeat.Adapter.MenuListAdapter
import com.ocwvar.whattoeat.Unit.Menu

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-26 下午3:12
 * File Location com.ocwvar.whattoeat
 * This file use to :   菜单管理页面
 */
class MenuManager : AppCompatActivity(), MenuListAdapter.Callback, View.OnClickListener {

    private val adapter: MenuListAdapter = MenuListAdapter(this@MenuManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_manager)
        val recycleView: RecyclerView = findViewById(R.id.recycleView) as RecyclerView
        recycleView.layoutManager = LinearLayoutManager(this@MenuManager, LinearLayoutManager.VERTICAL, false)
        recycleView.setHasFixedSize(true)
        recycleView.adapter = adapter
        findViewById(R.id.fab).setOnClickListener(this@MenuManager)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fab -> {

            }
        }
    }

    override fun onClick(menu: Menu) {

    }

    override fun onLongClick(menu: Menu): Boolean {
        return false
    }

}