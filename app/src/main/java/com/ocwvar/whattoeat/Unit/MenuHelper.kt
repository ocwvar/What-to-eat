package com.ocwvar.whattoeat.Unit

import android.os.Environment
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
class MenuHelper {

    //数据处理器
    private val decoder: JsonDecoder = JsonDecoder()

    //菜单数据容器
    private val menus: ArrayList<Menu> = ArrayList()

    init {
        //初始化时加载所有菜单对象
        menus.addAll(decoder.loadAllMenus())
    }

    /**
     * 重新读取所有菜单对象
     */
    fun reload() {
        menus.clear()
        menus.addAll(decoder.loadAllMenus())
    }

    /**
     * @return  所有菜单对象
     */
    fun menus(): ArrayList<Menu> {
        return this.menus
    }

    /**
     * @see JsonDecoder.saveMenu
     * @param   menu    要储存的菜单对象
     * @return  执行结果
     */
    fun saveMenu(menu: Menu): Boolean {
        return decoder.saveMenu(menu)
    }

    /**
     * Json数据解析类
     */
    private class JsonDecoder {

        //Json数据储存目录
        private val dataFolder: String = Environment.getExternalStorageDirectory().path + "/What2Eat/"
        //目录的可用性标记
        private var isReadable: Boolean by object : Any() {
            operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
                return File(dataFolder).canWrite()
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
                var length: Int = 0
                while (length != -1) {
                    length = inputStream.read(buffer)
                    outputStream.write(buffer, 0, length)
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

}