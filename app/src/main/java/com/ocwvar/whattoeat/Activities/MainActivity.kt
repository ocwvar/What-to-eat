package com.ocwvar.whattoeat.Activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ocwvar.whattoeat.R

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-25 下午9:54
 * File Location com.ocwvar.whattoeat
 * This file use to :   主界面
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById(R.id.fab).setOnClickListener(this@MainActivity)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            menuInflater.inflate(R.menu.menu_main, it)
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false
        when (item.itemId) {
            R.id.menu_main_menus -> {
                //启动菜单编辑页面进行菜单编辑操作
                val animBundle: Bundle?
                if (Build.VERSION.SDK_INT >= 21) {
                    animBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this@MainActivity,
                            Pair<View, String>(findViewById(R.id.fab), "fab")
                    ).toBundle()
                } else {
                    animBundle = null
                }

                startActivity(Intent(this@MainActivity, MenuManagerActivity::class.java), animBundle)
            }
        }
        return true
    }

    override fun onClick(p0: View) {

    }

}