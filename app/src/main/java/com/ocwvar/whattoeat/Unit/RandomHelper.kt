package com.ocwvar.whattoeat.Unit

import java.util.*

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-26 下午12:46
 * File Location com.ocwvar.whattoeat.Unit
 * This file use to :   随机生成器
 */
class RandomHelper {

    private val random: Random = Random(System.currentTimeMillis())

    /**
     * 生成随机获取的菜单
     * @param   count   生成的数量
     * @param   menus   要随机的菜单容器
     * @return  随机的结果，输入数据不可用返回NULL
     */
    fun getRandomMenus(count: Int, menus: ArrayList<Menu>): ArrayList<Menu>? {
        if (menus.size > 1 && count in 1..menus.size - 1) {
            //在有效范围内才进行获取数据
            val result: ArrayList<Menu> = ArrayList()
            for (i in 0..count) {
                //将数据放入容器内
                result.add(menus[random.nextInt(menus.size - 1)])
            }
            return result
        }
        return null
    }

    /**
     * 生成随机获取的食物
     * @param   count   生成的数量
     * @param   menu    要随机的菜单
     * @return  随机的结果，输入数据不可用返回NULL
     */
    fun getRandomFood(count: Int, menu: Menu): ArrayList<Food>? {
        if (menu.foods.size > 1 && count in 1..menu.foods.size - 1) {
            //在有效范围内才进行获取数据
            val result: ArrayList<Food> = ArrayList()
            for (i in 0..count - 1) {
                //将数据放入容器内
                result.add(menu.foods[random.nextInt(menu.foods.size - 1)])
            }
            return result
        }
        return null
    }

}