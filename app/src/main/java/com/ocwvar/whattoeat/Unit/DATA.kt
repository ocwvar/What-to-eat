package com.ocwvar.whattoeat.Unit

import java.util.*

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-26 下午2:32
 * File Location com.ocwvar.whattoeat.Unit
 * This file use to :   数据储存容器
 */
object DATA {

    //菜单数据容器
    val menus: ArrayList<Menu> = ArrayList()
    //随机数量容器
    val counts: HashMap<String, Int> = HashMap()
    //激活列表标记
    val enableList: ArrayList<String> = ArrayList()

    /**
     * 获取菜单的随机数量
     * @param   menuTitle   菜单名称
     * @return  随机数量，如果获取失败则返回 0
     */
    fun indexCount(menuTitle: String): Int = counts[menuTitle] ?: 0

    /**
     * 获取菜单是否已经启用
     * @param   menuTitle   菜单名称
     * @return  是否启用
     */
    fun indexEnable(menuTitle: String): Boolean = enableList.indexOf(menuTitle) != -1

}