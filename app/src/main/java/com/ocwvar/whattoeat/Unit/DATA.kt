package com.ocwvar.whattoeat.Unit
import java.util.*

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-26 下午2:32
 * File Location com.ocwvar.whattoeat.Unit
 * This file use to :   数据储存，数据查询获取。非数据修改编辑类操作
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
    fun indexRandomCount(menuTitle: String): Int = counts[menuTitle] ?: 0

    /**
     * 获取菜单是否已经启用
     * @param   menuTitle   菜单名称
     * @return  是否启用
     */
    fun indexEnable(menuTitle: String): Boolean = enableList.indexOf(menuTitle) != -1

    /**
     * 通过菜单名称查询菜单的位置
     * @param menuTitle 菜单名称
     * @return  菜单位置
     */
    fun indexMenuByTitle(menuTitle: String): Int = (0..menus.size - 1).firstOrNull { menus[it].title.equals(menuTitle) } ?: -1

    /**
     * @return  已启用的菜单列表
     */
    fun enabledMenus(): ArrayList<Menu> {
        val result: ArrayList<Menu> = ArrayList()
        menus.forEach {
            if (indexEnable(it.title)) {
                result.add(it)
            }
        }
        return result
    }

}