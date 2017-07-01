package com.ocwvar.whattoeat.Activities

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import com.ocwvar.darkpurple.Units.ToastMaker
import com.ocwvar.whattoeat.Adapter.ResultAdapter
import com.ocwvar.whattoeat.R
import com.ocwvar.whattoeat.Unit.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-29 下午9:00
 * File Location com.ocwvar.whattoeat.Activities
 * This file use to :   结果显示页面
 */
class ResultActivity : AppCompatActivity(), View.OnClickListener {

    /**
     * 页面启动参数
     */
    object ACTIONS {
        /**
         * 正常启动模式
         */
        val ACTION_NORMAL: String = "an"
        /**
         * 加载记录启动模式，需要搭配使用 ACTIONS.EXTRA_RECORD_OBJECT 字段传递记录数据对象
         */
        val ACTION_LOAD_RECORD: String = "alr"
        /**
         * 加载记录启动模式下附带的记录数据字段
         */
        val EXTRA_RECORD_OBJECT: String = "ero"
    }

    /**
     * 错误状态标记
     */
    private val ACTION_ERROR: String = "error"

    /**
     * 当前是否正在播放动画标记
     */
    private var isWaitingForResult: Boolean = false

    private val adapter: ResultAdapter = ResultAdapter()
    private var currentAction: String = ACTION_ERROR

    private lateinit var fab: FloatingActionButton
    private lateinit var waitingText: View
    private lateinit var recycleView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= 21) {
            //只有SDK>=21才能使用状态栏导航栏颜色
            window.statusBarColor = resources.getColor(R.color.colorAccent)
            window.navigationBarColor = resources.getColor(R.color.colorAccent)
        }

        super.onCreate(savedInstanceState)

        val record: Menu?
        //读取当前界面启动ACTION
        when (intent?.action ?: ACTION_ERROR) {
        //读取记录模式
            ACTIONS.ACTION_LOAD_RECORD -> {
                this.currentAction = ACTIONS.ACTION_LOAD_RECORD
                record = intent.extras?.getParcelable(ACTIONS.EXTRA_RECORD_OBJECT) ?: null
                record ?: finish()
            }

        //正常启动模式
            ACTIONS.ACTION_NORMAL -> {
                this.currentAction = ACTIONS.ACTION_NORMAL
                record = null
            }

        //异常状态
            else -> {
                finish()
                return
            }
        }

        setContentView(R.layout.activity_result)
        waitingText = findViewById(R.id.result_waiting)

        //RecycleView属性设置
        (findViewById(R.id.recycleView) as RecyclerView).let {
            //RecycleView触摸事件，动态隐藏FAB
            it.setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_MOVE -> {
                        fab.hide()
                    }
                    MotionEvent.ACTION_UP -> {
                        fab.show()
                    }
                }
                false
            }
            //其他属性设置
            it.layoutManager = LinearLayoutManager(this@ResultActivity, LinearLayoutManager.VERTICAL, false)
            it.setHasFixedSize(false)
            it.adapter = adapter
            it.visibility = View.INVISIBLE
            recycleView = it
        }

        //FAB点击事件
        (findViewById(R.id.fab) as FloatingActionButton).let {
            it.setOnClickListener(this@ResultActivity)
            fab = it
        }

        //加载数据
        showResult(record)
    }

    override fun onClick(view: View) {
        //当前正在执行动画，不予执行点击事件
        if (isWaitingForResult) return

        when (view.id) {
            R.id.fab -> {
                if (currentAction == ACTIONS.ACTION_NORMAL) {
                    //如果是普通模式，则需要提示用户是否保存数据
                    val editText: EditText = EditText(this@ResultActivity).let {
                        it.hint = getString(R.string.result_dialog_input_hit)
                        it.setHintTextColor(Color.LTGRAY)
                        it.background = null
                        it
                    }
                    //创建并显示询问对话框
                    AlertDialog.Builder(this@ResultActivity)
                            .setView(editText)
                            .setPositiveButton(R.string.result_dialog_save, { p0, p1 ->
                                //确认储存
                                var recordName: String = editText.text.toString()
                                if (TextUtils.isEmpty(recordName) || !isNameValid(recordName)) {
                                    //如果用户没有输入标题，则默认给一个标题
                                    ToastMaker.show(this@ResultActivity, R.string.result_ERROR_record_name, ToastMaker.TOAST_COLOR_NORMAL)
                                    recordName = getString(R.string.result_record_default_name_header) + System.nanoTime().toString()
                                }
                                //开始储存记录数据
                                DATAHelper(this@ResultActivity).saveRecord(result2Record(recordName))
                                //退出页面
                                p0.dismiss()
                                if (Build.VERSION.SDK_INT >= 21) {
                                    finishAfterTransition()
                                } else {
                                    finish()
                                }
                            })
                            .setNegativeButton(R.string.result_dialog_not_save, { p0, p1 ->
                                //不储存数据
                                if (!isWaitingForResult) {
                                    if (Build.VERSION.SDK_INT >= 21) {
                                        finishAfterTransition()
                                    } else {
                                        finish()
                                    }
                                }
                            })
                            .show()
                } else {
                    //其他模式下则可以直接退出页面
                    if (Build.VERSION.SDK_INT >= 21) {
                        finishAfterTransition()
                    } else {
                        finish()
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (!isWaitingForResult) {
            return super.onKeyDown(keyCode, event)
        } else {
            return true
        }
    }

    /**
     * 文件名称是否合法
     * @param   name    文件名称
     * @return  是否合法
     */
    fun isNameValid(name: String): Boolean = !name.contains("|\\?*<\":>+[]/'")

    /**
     * 计算和显示结果
     * @param   record  需要显示的记录数据，如果传入NULL，则计算随机数据
     */
    private fun showResult(record: Menu?) {

        //等待文字淡出动画，周期包括：等待文字淡出 -> 结果列表淡入显示
        val fadeOut: Animation = AnimationUtils.loadAnimation(this@ResultActivity, R.anim.fade_out).let {
            it.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    waitingText.visibility = View.INVISIBLE
                    recycleView.visibility = View.VISIBLE
                    recycleView.startAnimation(AnimationUtils.loadAnimation(this@ResultActivity, R.anim.fade_in))
                    adapter.notifyDataSetChanged()
                    isWaitingForResult = false
                }

                override fun onAnimationStart(p0: Animation?) {
                }
            })
            it.duration = 500L
            it
        }

        //等待文字淡入动画，周期包括：等待文字淡入 -> 计算随机结果 -> 执行淡出动画 fadeOut
        val fadeIn: Animation = AnimationUtils.loadAnimation(this@ResultActivity, R.anim.fade_in).let {
            it.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    //根据启动模式不同而加载不同的显示计算逻辑
                    when (currentAction) {
                    //普通启动模式，计算随机结果
                        ACTIONS.ACTION_NORMAL -> {
                            //显示完全等待信息后，开始执行结果计算
                            adapter.addSource(ResultAdapter.ResultMessage(getString(R.string.result_message), 50.0f))
                            adapter.addSource(ResultAdapter.ResultMessage(getString(R.string.result_message_1), 35.0f))

                            val enabledMenus: ArrayList<Menu> = DATA.enabledMenus()
                            val randomHelper: RandomHelper = RandomHelper()

                            for (menuObject in enabledMenus) {
                                randomHelper.getRandomFood(DATA.indexRandomCount(menuObject.title), menuObject)?.forEach {
                                    adapter.addSource(ResultAdapter.ResultItem(it, menuObject.title))
                                }
                            }

                            //添加当前生成时间戳
                            adapter.addSource(ResultAdapter.ResultMessage(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(System.currentTimeMillis())), 12.0f))
                        }
                    //显示记录模式，开始加载数据
                        ACTIONS.ACTION_LOAD_RECORD -> {
                            if (record != null) {
                                adapter.addSource(ResultAdapter.ResultMessage(record.title, 20.0f))
                                adapter.addSource(ResultAdapter.ResultMessage(record.message, 15.0f))
                                record.foods.forEach {
                                    adapter.addSource(ResultAdapter.ResultItem(Food(it.title, it.message), it.extraMessage ?: ""))
                                }
                            }
                        }
                    }

                    //计算完全后执行等待文字消失动画
                    waitingText.startAnimation(fadeOut)
                }

                override fun onAnimationStart(p0: Animation?) {
                    isWaitingForResult = true
                }
            })
            it.duration = 1500L
            it
        }

        waitingText.visibility = View.VISIBLE
        waitingText.startAnimation(fadeIn)
    }

    /**
     * 将结果储存为记录
     * @param   name    记录标题
     * @return  记录
     */
    private fun result2Record(name: String): Menu {
        //结果食品列表容器
        val recordObjects: ArrayList<Food> = ArrayList()
        //生成记录数据，其中最后一项为记录的生成时间
        val recordObject: Menu = Menu(recordObjects, name, (adapter.source()[adapter.itemCount - 1] as ResultAdapter.ResultMessage).message)
        adapter.source().forEach {
            if (it is ResultAdapter.ResultItem) {
                //添加食品记录
                recordObjects.add(Food(it.food.title, it.food.message, null, it.ownerTitle))
            }
        }
        return recordObject
    }

}