package com.ocwvar.whattoeat.Unit

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.reflect.KProperty

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-25 下午11:09
 * File Location com.ocwvar.whattoeat.Unit
 * This file use to :   菜单数据控制器
 */
class DATAHelper(appContext: Context) {

    //数据处理器
    private val jsonDecoder: JsonDecoder = JsonDecoder()
    //Sp处理器
    private val spDecoder: SpDecoder = SpDecoder(appContext)

    /**
     * 加载所有数据：
     * 所有的菜单对象
     * 菜单对象的随机数量
     * 已启用的列表
     */
    fun initData() {
        DATA.menus.clear()
        DATA.menus.addAll(jsonDecoder.loadAllMenus())
        DATA.counts.clear()
        DATA.counts.putAll(spDecoder.loadAllCounts())
        DATA.enableList.clear()
        DATA.enableList.addAll(spDecoder.loadEnableList())
    }

    /**
     * 储存菜单数据对象为文件对象，此操作会替换现有相同名称的菜单对象
     * @return  执行结果
     */
    fun saveMenu(menu: Menu): Boolean {
        if (jsonDecoder.saveMenu(menu)) {
            //查找原本菜单位置
            val position: Int = DATA.indexMenuByTitle(menu.title)
            if (position != -1) {
                //如果有原有的菜单位置，则直接替换数据
                DATA.menus[position] = menu
            } else {
                //新数据直接添加
                DATA.menus.add(menu)
            }
            return true
        }
        return false
    }

    /**
     * 移除菜单对象
     * @param   menu    菜单对象
     * @return  执行结果
     */
    fun removeMenu(menu: Menu): Boolean {
        //获取菜单文件
        val menuFile: File = File(jsonDecoder.dataFolder + menu.title + ".menu")
        if (menuFile.exists() && menuFile.delete()) {
            //成功删除菜单文件后继续删除数据列表
            return DATA.menus.remove(menu)
        }
        return false
    }

    /**
     * 移除菜单对象
     * @param   menuTitle    菜单标题
     * @return  执行结果
     */
    fun removeMenu(menuTitle: String): Boolean {
        //获取菜单文件
        val menuFile: File = File(jsonDecoder.dataFolder + menuTitle + ".menu")
        if (menuFile.exists() && menuFile.delete()) {
            //成功删除菜单文件后继续删除数据列表
            val position: Int = DATA.menus.indexOfLast { it.title.equals(menuTitle) }
            if (position != -1) {
                return DATA.menus.removeAt(position) != null
            }
            return false
        }
        return false
    }

    /**
     * 储存菜单的随机数量
     * @param   menuTitle  菜单名称
     * @param   count   随机数量
     */
    fun saveCount(menuTitle: String, count: Int): Boolean = spDecoder.saveCount(menuTitle, count)

    /**
     * 更新已启用菜单列表数据到SP中
     */
    fun updateEnableList(): Boolean = spDecoder.saveEnableList()

    /**
     * 更新菜单的启动状态
     * @param   menuTitle   菜单名称
     * @param   enable   是否启用
     * @return  执行结果
     */
    fun updateEnableList(menuTitle: String, enable: Boolean): Boolean {
        if (enable && !DATA.indexEnable(menuTitle)) {
            //启用菜单
            DATA.enableList.add(menuTitle)
            return spDecoder.saveEnableList()
        } else if (!enable && DATA.indexEnable(menuTitle)) {
            //不启用菜单
            DATA.enableList.remove(menuTitle)
            return spDecoder.saveEnableList()
        } else {
            return false
        }
    }

    /**
     * Json数据解析类
     */
    private class JsonDecoder {

        //Json数据储存目录
        val dataFolder: String = Environment.getExternalStorageDirectory().path + "/What2Eat/"
        //目录的可用性标记
        private var isReadable: Boolean by object : Any() {
            operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
                val folder: File = File(dataFolder)
                return (folder.exists() && folder.canWrite()) || (!folder.exists() && folder.mkdirs())
            }

            operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {}
        }

        /**
         * 文件转换为文字
         * @return  读取的结果，如果读取失败，则返回NULL
         */
        private fun file2String(file: File): String? {
            try {
                val inputStream: FileInputStream = FileInputStream(file)
                val outputStream: ByteArrayOutputStream = ByteArrayOutputStream()
                var buffer: ByteArray = ByteArray(128)
                var length: Int
                while (true) {
                    length = inputStream.read(buffer)
                    if (length != -1) {
                        outputStream.write(buffer, 0, length)
                    } else {
                        break
                    }
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
                return String(outputStream.toByteArray(), Charsets.UTF_8)
            } catch (e: Exception) {
                return null
            }
        }

        /**
         * 读取所有现有菜单数据
         * @return 本地储存的菜单数据，读取失败返回空的数据列表
         */
        fun loadAllMenus(): ArrayList<Menu> {
            //数据容器
            val result: ArrayList<Menu> = ArrayList()

            //如果目录无法读取，则直接返回空列表
            if (!isReadable) return result

            //菜单文件列表
            val menuFiles: Array<File>? = File(dataFolder).listFiles { file, name ->
                (name != null) && (name!!.endsWith(".menu", false))
            }

            //如果没办法获取到数据，则直接返回空列表
            if (menuFiles == null || menuFiles.isEmpty()) return result
            menuFiles
                    .map {
                        //读取文件的内容
                        file2String(it)
                    }
                    .filterNot {
                        //过滤掉读取文字失败的文件对象
                        TextUtils.isEmpty(it)
                    }
                    .forEach {
                        try {
                            //将数据转换为Menu对象，并储存进列表中
                            result.add(Gson().fromJson(it, Menu::class.java))
                        } catch(e: Exception) {
                            Log.e(JsonDecoder::class.java.simpleName, "菜单文件转换错误！JSON数据为：\n" + it)
                        }
                    }
            return result
        }

        /**
         * 储存菜单数据对象为文件对象，此操作会替换现有相同名称的菜单对象
         * @return  执行结果
         */
        fun saveMenu(menu: Menu): Boolean {
            //目录不可用，不执行操作
            if (!isReadable) return false

            //菜单文件对象
            val menuFile: File = File(dataFolder + menu.title + ".menu")
            //Json文字字节对象
            val bytes: ByteArray = Gson().toJson(menu).toString().toByteArray(Charsets.UTF_8)
            //先删除旧的对象
            menuFile.delete()
            try {
                //创建输出流
                val outputStream: FileOutputStream = FileOutputStream(menuFile, false)
                //写入数据
                outputStream.write(bytes)
                outputStream.flush()
                outputStream.close()
                return true
            } catch(e: Exception) {
                return false
            }
        }
    }

    /**
     * sharePreference数据解析类
     */
    private inner class SpDecoder(val appContext: Context) {

        //启动列表SP储存键值名
        private val ENABLED_LIST_KEY: String = "enabled_list"

        /**
         * 获取所有菜单的随机数量,此方法必须调用在 JsonDecoder.loadAllMenus 方法之后，不然无法获取数据
         * @see JsonDecoder.loadAllMenus
         */
        fun loadAllCounts(): HashMap<String, Int> {
            val result: HashMap<String, Int> = HashMap()
            val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
            for (menu in DATA.menus) {
                result.put(menu.title, sp.getInt(menu.title, 0))
            }
            return result
        }

        /**
         * 获取启动菜单信息
         */
        fun loadEnableList(): ArrayList<String> {
            val result: ArrayList<String> = ArrayList()
            val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
            val stringSet: Iterator<String>? = sp.getStringSet(ENABLED_LIST_KEY, null)?.iterator()
            //如果获取不到启动列表，则直接返回空列表
            stringSet ?: return result
            //将数据存入结果列表
            stringSet.forEach { result.add(it) }
            return result
        }

        /**
         * 储存启动菜单信息
         */
        fun saveEnableList(): Boolean {
            val edit: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(appContext).edit()
            val stringSet: Set<String> = DATA.enableList.toSet()
            return edit.remove(ENABLED_LIST_KEY).putStringSet(ENABLED_LIST_KEY, stringSet).commit()
        }

        /**
         * 储存菜单的随机数量
         * @param   menuTitle  菜单名称
         * @param   count   随机数量
         */
        fun saveCount(menuTitle: String, count: Int): Boolean {
            val edit: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(appContext).edit()
            return edit.putInt(menuTitle, count).commit()
        }

    }

}