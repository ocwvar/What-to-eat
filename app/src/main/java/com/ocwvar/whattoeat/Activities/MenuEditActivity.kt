package com.ocwvar.whattoeat.Activities

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.ocwvar.darkpurple.Units.ToastMaker
import com.ocwvar.whattoeat.Adapter.FoodListAdapter
import com.ocwvar.whattoeat.R
import com.ocwvar.whattoeat.Unit.DATA
import com.ocwvar.whattoeat.Unit.DATAHelper
import com.ocwvar.whattoeat.Unit.Food
import com.ocwvar.whattoeat.Unit.Menu
import java.util.*

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-26 下午3:34
 * File Location com.ocwvar.whattoeat
 * This file use to :   菜单编辑页面
 */
class MenuEditActivity : AppCompatActivity(), FoodListAdapter.Callback, View.OnClickListener {

    /**
     * 创建此页面需要使用到的数据
     */
    object ACTIONS {
        /**
         * 创建菜单 ACTION
         */
        val ACTION_CREATE: String = "AC_C"

        /**
         *编辑菜单 ACTION
         */
        val ACTION_EDIT: String = "AC_E"

        /**
         * 编辑菜单时传递的菜单数据字段
         */
        val EXTRAS_PARCELABLE_OBJECT: String = "AC_O"
    }

    /**
     * 退出页面 ACTION：数据错误
     */
    private val ACTION_EXIT_ERROR: String = "PAC_E"

    /**
     * 退出页面 ACTION：储存数据
     */
    private val ACTION_EXIT_SAVE: String = "PAC_S"

    /**
     * 退出页面 ACTION：储存新建的数据
     */
    private val ACTION_EXIT_SAVE_CREATE: String = "PAC_SC"

    lateinit var exitAction: String
    lateinit var adapter: FoodListAdapter
    lateinit var foods: ArrayList<Food>

    //储存原有的菜单数据，主要用于原有菜单编辑操作
    var oldTitle: String = ""
    var oldMessage: String = ""

    lateinit var menuTitleInput: EditText
    lateinit var menuMessageInput: TextView

    private var foodEditDialog: FoodEditDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //检查传递的数据正确性
        val intentAction: String = intent?.action ?: "EMPTY"

        when (intentAction) {

        //创建菜单
            ACTIONS.ACTION_CREATE -> {
                //创建空的食物列表
                foods = ArrayList()
                //创建基于空列表的适配器
                adapter = FoodListAdapter(this@MenuEditActivity.foods, this@MenuEditActivity)
                //设定这次的Action
                exitAction = ACTION_EXIT_SAVE_CREATE
            }

        //编辑菜单
            ACTIONS.ACTION_EDIT -> {
                //将要编辑的数据设置到本地
                val menuObject: Menu? = intent.extras?.getParcelable<Menu>(ACTIONS.EXTRAS_PARCELABLE_OBJECT)
                if (menuObject == null) {
                    //无法获取到要编辑的数据，直接退出页面
                    exitPage(ACTION_EXIT_ERROR)
                    return
                }
                //创建基于空列表的适配器
                adapter = FoodListAdapter(menuObject.foods, this@MenuEditActivity)
                //设定这次的Action
                exitAction = ACTION_EXIT_SAVE
                //设置数据到本地
                this.foods = menuObject.foods
                this.oldTitle = menuObject.title
                this.oldMessage = menuObject.message
            }

        //ACTION数据错误或无ACTION
            else -> {
                //无效ACTION，直接退出页面
                exitPage(ACTION_EXIT_ERROR)
                return
            }

        }

        setContentView(R.layout.activity_menu_edit)

        findViewById(R.id.fab).setOnClickListener(this@MenuEditActivity)

        //标题输入框属性设置
        (findViewById(R.id.menu_edit_title) as EditText).let {
            it.setText(oldTitle)
            menuTitleInput = it
        }

        //消息输入框属性设置
        (findViewById(R.id.menu_edit_message) as TextView).let {
            it.setOnClickListener(this@MenuEditActivity)
            it.text = oldMessage
            menuMessageInput = it
        }

        //ToolBar属性设置
        (findViewById(R.id.toolbar) as Toolbar).let {
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
            when (exitAction) {
                ACTION_EXIT_SAVE -> {
                    supportActionBar?.title = getString(R.string.menu_edit_mode_title_edit)
                }
                ACTION_EXIT_SAVE_CREATE -> {
                    supportActionBar?.title = getString(R.string.menu_edit_mode_title_create)
                }
            }
        }

        //RecycleView属性设置
        (findViewById(R.id.recycleView) as RecyclerView).let {
            it.layoutManager = LinearLayoutManager(this@MenuEditActivity, LinearLayoutManager.VERTICAL, false)
            it.setHasFixedSize(true)
            it.adapter = adapter
        }

    }

    /**
     * 退出页面操作
     * @param   exitAction  退出动作
     */
    fun exitPage(exitAction: String) {
        when (exitAction) {

        //原有菜单储存操作
            ACTION_EXIT_SAVE -> {
                //获取菜单名称,给以下的步骤进行检查
                val title: String = menuTitleInput.text.toString()

                if (!title.equals(oldTitle) && DATA.indexMenuByTitle(title) != -1) {
                    //更改了原有的名称，同时现有列表中存在相同名称的菜单
                    ToastMaker.show(this@MenuEditActivity, R.string.menu_edit_ERROR_menu_existed, ToastMaker.TOAST_COLOR_WARNING)
                } else if (TextUtils.isEmpty(title) || !isNameValid(title)) {
                    //菜单标题为空或者名称不合法
                    ToastMaker.show(this@MenuEditActivity, R.string.menu_edit_ERROR_menu_name, ToastMaker.TOAST_COLOR_WARNING)
                } else if (foods.size <= 0) {
                    //菜单食物列表为空，直接删除菜单数据，并退出页面
                    if (DATAHelper(this@MenuEditActivity).removeMenu(title)) {
                        ToastMaker.show(this@MenuEditActivity, R.string.menu_edit_ERROR_menu_create_no_foods, ToastMaker.TOAST_COLOR_NORMAL)
                        finish()
                    } else {
                        //删除失败
                        ToastMaker.show(this@MenuEditActivity, R.string.menu_edit_ERROR_menu_save, ToastMaker.TOAST_COLOR_NORMAL)
                    }
                } else {
                    //菜单可以进行储存
                    //获取菜单的信息数据
                    val message: String = oldMessage
                    val helper: DATAHelper = DATAHelper(this@MenuEditActivity)

                    //名称检查与对应操作的结果标记变量
                    val canBeSave: Boolean
                    if (oldTitle.equals(title)) {
                        canBeSave = true
                    } else {
                        canBeSave = helper.removeMenu(oldTitle)
                    }

                    if (canBeSave && helper.saveMenu(Menu(foods, title, message))) {
                        //储存数据成功
                        ToastMaker.show(this@MenuEditActivity, R.string.menu_edit_INFO_menu_saved, ToastMaker.TOAST_COLOR_NORMAL)
                        finish()
                    } else {
                        //储存数据失败
                        ToastMaker.show(this@MenuEditActivity, R.string.menu_edit_ERROR_menu_save, ToastMaker.TOAST_COLOR_WARNING)
                    }
                }
            }

        //创建菜单对象并退出
            ACTION_EXIT_SAVE_CREATE -> {
                //获取菜单名称,给以下的步骤进行检查
                val title: String = menuTitleInput.text.toString()

                if (DATA.indexMenuByTitle(title) != -1) {
                    //如果成功查找到了菜单对象，则说明存在相同的，不能进行储存
                    ToastMaker.show(this@MenuEditActivity, R.string.menu_edit_ERROR_menu_existed, ToastMaker.TOAST_COLOR_WARNING)
                } else if (TextUtils.isEmpty(title) || !isNameValid(title)) {
                    //菜单标题为空或者名称不合法
                    ToastMaker.show(this@MenuEditActivity, R.string.menu_edit_ERROR_menu_name, ToastMaker.TOAST_COLOR_WARNING)
                } else if (foods.size <= 0) {
                    //菜单食物列表为空，直接退出页面，不进行列表的储存
                    ToastMaker.show(this@MenuEditActivity, R.string.menu_edit_INFO_menu_save_no_foods, ToastMaker.TOAST_COLOR_NORMAL)
                    finish()
                } else {
                    //菜单可以进行储存
                    //获取菜单的信息数据
                    val message: String = oldMessage

                    if (DATAHelper(this@MenuEditActivity).saveMenu(Menu(foods, title, message))) {
                        //储存数据成功
                        ToastMaker.show(this@MenuEditActivity, R.string.menu_edit_INFO_menu_saved, ToastMaker.TOAST_COLOR_NORMAL)
                        finish()
                    } else {
                        //储存数据失败
                        ToastMaker.show(this@MenuEditActivity, R.string.menu_edit_ERROR_menu_save, ToastMaker.TOAST_COLOR_WARNING)
                    }
                }
            }

        //发生错误退出页面
            ACTION_EXIT_ERROR -> {
                ToastMaker.show(this@MenuEditActivity, R.string.menu_edit_ERROR_data, ToastMaker.TOAST_COLOR_WARNING)
                finish()
            }

        }
    }

    /**
     * 文件名称是否合法
     * @param   name    文件名称
     * @return  是否合法
     */
    fun isNameValid(name: String): Boolean = !name.contains("|\\?*<\":>+[]/'")

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_menu_edit, menu)
        return true
    }

    /**
     * ToolBar按钮点击事件
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        //点击返回按钮事件
            android.R.id.home -> {
                //点击返回时，显示保存确认对话框
                AlertDialog.Builder(this@MenuEditActivity)
                        .setMessage(R.string.menu_edit_dialog_exit_title)
                        .setNegativeButton(R.string.menu_edit_dialog_exit_unSave, { p0, p1 ->
                            p0.dismiss()
                            finish()
                        })
                        .setPositiveButton(R.string.menu_edit_dialog_exit_save, { p0, p1 ->
                            p0.dismiss()
                            exitPage(exitAction)
                        })
                        .show()
            }

        //点击保存按钮事件
            R.id.menu_menu_edit_done -> {
                //保存数据退出
                exitPage(exitAction)
            }

        }
        return true
    }

    /**
     * 其他界面上的点击事件
     * FAB
     * 菜单信息文字
     */
    override fun onClick(view: View) {
        when (view.id) {

        //FAB点击事件
            R.id.fab -> {
                if (foodEditDialog == null) {
                    foodEditDialog = FoodEditDialog()
                }
                foodEditDialog?.show(null, -1)
            }

        //菜单信息文字,显示一个输入对话框让用户输入
            R.id.menu_edit_message -> {
                val editText: EditText = EditText(this@MenuEditActivity).let {
                    it.hint = getString(R.string.menu_edit_hint_message)
                    it.background = null
                    it.setEms(5)
                    it.setText(oldMessage)
                    it
                }
                AlertDialog.Builder(this@MenuEditActivity)
                        .setView(editText)
                        .setPositiveButton(R.string.simple_done, { p0, p1 ->
                            menuMessageInput.text = editText.text.toString()
                            oldMessage = editText.text.toString()
                            p0.cancel()
                        })
                        .show()
            }
        }
    }

    override fun onClick(food: Food, position: Int) {
        if (foodEditDialog == null) {
            foodEditDialog = FoodEditDialog()
        }
        foodEditDialog?.show(food, position)
    }

    override fun onLongClick(food: Food, position: Int): Boolean {
        foods.removeAt(position)
        adapter.notifyItemRemoved(position)
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //点击返回时，显示保存确认对话框
            AlertDialog.Builder(this@MenuEditActivity)
                    .setMessage(R.string.menu_edit_dialog_exit_title)
                    .setNegativeButton(R.string.menu_edit_dialog_exit_unSave, { p0, p1 ->
                        p0.dismiss()
                        finish()
                    })
                    .setPositiveButton(R.string.menu_edit_dialog_exit_save, { p0, p1 ->
                        p0.dismiss()
                        exitPage(exitAction)
                    })
                    .show()
        }
        return true
    }

    /**
     * 食品编辑创建对话框载体
     */
    private inner class FoodEditDialog {

        private lateinit var titleInput: EditText
        private lateinit var messageInput: EditText

        /**
         * 显示对话框
         * @param   food    要进行编辑的食品对象，新建则传入NULL即可
         * @param   position    编辑的位置，新建则传入 -1
         */
        fun show(food: Food?, position: Int) {
            //生成或者获取原有布局
            var dialogView: View = LayoutInflater.from(this@MenuEditActivity).inflate(R.layout.dialog_food_edit, null, false)

            //控件绑定
            titleInput = dialogView.findViewById<EditText>(R.id.food_edit_title).let {
                it.setText(food?.title)
                it
            }
            messageInput = dialogView.findViewById<EditText>(R.id.food_edit_message).let {
                it.setText(food?.message)
                it
            }

            //显示对话框
            AlertDialog.Builder(this@MenuEditActivity)
                    .setView(dialogView)
                    .setPositiveButton(R.string.simple_done) { p0, p1 ->
                        //确定按钮，进行食品添加
                        val title: String? = titleInput.text.toString()
                        val message: String? = messageInput.text.toString()
                        if (TextUtils.isEmpty(title)) {
                            //名称为空，不能进行添加
                            ToastMaker.show(this@MenuEditActivity, R.string.menu_edit_ERROR_food_create_no_data, ToastMaker.TOAST_COLOR_WARNING)
                        } else {
                            //添加数据
                            if (position < 0) {
                                //当前是创建对象操作
                                foods.add(Food(title!!, message, null))
                                adapter.notifyItemInserted(foods.size - 1)
                            } else {
                                //当前是编辑对象操作
                                foods.removeAt(position)
                                foods.add(position, Food(title!!, message, null))
                                adapter.notifyItemChanged(position)
                            }
                            p0?.dismiss()
                        }
                    }.show()
        }
    }

}