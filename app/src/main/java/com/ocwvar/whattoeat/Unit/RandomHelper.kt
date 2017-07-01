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
            val container: ArrayList<Menu> = menus
            for (i in 0..count) {
                //将数据放入容器内
                val menu: Menu? = container.removeAt(random.nextInt(container.size - 1))
                menu ?: break
                result.add(menu)
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
        if (menu.foods.size > 1 && count in 1..menu.foods.size) {
            //在有效范围内才进行获取数据
            val result: ArrayList<Food> = ArrayList()
            val container: ArrayList<Food> = menu.foods.clone() as ArrayList<Food>
            for (i in 0..count - 1) {
                //将数据放入容器内
                if (container.size == 1) {
                    //在只有一个对象的时候，则直接放入列表，不经过随机
                    result.add(container[0])
                    break
                } else {
                    //进行随机抽选
                    val food: Food? = container.removeAt(random.nextInt(container.size - 1))
                    food ?: break
                    result.add(food)
                }
            }
            return result
        }
        return null
    }

}