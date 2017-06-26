package com.ocwvar.whattoeat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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

    override fun onClick(p0: View) {

    }

}