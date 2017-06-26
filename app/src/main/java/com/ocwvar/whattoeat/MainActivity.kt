package com.ocwvar.whattoeat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View

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
        findViewById(R.id.eat).setOnClickListener(this@MainActivity)
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

            }

            R.id.menu_main_random -> {

            }
        }
        return true
    }

    override fun onClick(p0: View) {

    }

}