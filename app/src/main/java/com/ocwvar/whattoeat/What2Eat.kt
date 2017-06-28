package com.ocwvar.whattoeat

import android.app.Application
import com.ocwvar.whattoeat.Unit.DATAHelper

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-28 上午11:42
 * File Location com.ocwvar.whattoeat
 */
class What2Eat : Application() {

    override fun onCreate() {
        super.onCreate()
        //应用启动时加载数据
        DATAHelper(this@What2Eat).initData()
    }
}