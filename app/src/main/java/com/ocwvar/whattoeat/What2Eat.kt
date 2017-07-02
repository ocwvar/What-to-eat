package com.ocwvar.whattoeat

import android.app.Application
import android.preference.PreferenceManager
import com.ocwvar.whattoeat.Unit.DATAHelper
import com.umeng.analytics.MobclickAgent

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

        if (PreferenceManager.getDefaultSharedPreferences(this@What2Eat).getBoolean("isEnableUmeng", true)) {
            //判断是否启用友盟是据统计
            setupUmeng()
        }
    }

    /**
     * 友盟属性设置
     */
    fun setupUmeng() {
        MobclickAgent.startWithConfigure(MobclickAgent.UMAnalyticsConfig(this@What2Eat, "595877d204e20551c8002069", "Default"))
        MobclickAgent.setDebugMode(true)
        MobclickAgent.setScenarioType(this@What2Eat, MobclickAgent.EScenarioType.E_UM_NORMAL)
        MobclickAgent.setCatchUncaughtExceptions(true)
        MobclickAgent.enableEncrypt(true)
    }

}