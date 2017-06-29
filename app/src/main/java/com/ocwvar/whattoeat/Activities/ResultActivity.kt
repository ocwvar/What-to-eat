package com.ocwvar.whattoeat.Activities

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ocwvar.whattoeat.Adapter.ResultAdapter
import com.ocwvar.whattoeat.R
import com.ocwvar.whattoeat.Unit.DATA
import com.ocwvar.whattoeat.Unit.Menu
import com.ocwvar.whattoeat.Unit.RandomHelper
import java.util.*

@Suppress("DEPRECATION")
/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-29 下午9:00
 * File Location com.ocwvar.whattoeat.Activities
 * This file use to :   结果显示页面
 */
class ResultActivity : AppCompatActivity() {

    private val adapter: ResultAdapter = ResultAdapter()
    private var isWaitingForResult: Boolean = false

    private lateinit var waitingText: View
    private lateinit var recycleView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= 21) {
            //只有SDK>=21才能使用状态栏导航栏颜色
            window.statusBarColor = resources.getColor(R.color.colorAccent)
            window.navigationBarColor = resources.getColor(R.color.colorAccent)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        waitingText = findViewById(R.id.result_waiting)

        //RecycleView属性设置
        (findViewById(R.id.recycleView) as RecyclerView).let {
            recycleView = it
            recycleView.layoutManager = LinearLayoutManager(this@ResultActivity, LinearLayoutManager.VERTICAL, false)
            recycleView.setHasFixedSize(false)
            recycleView.adapter = adapter
            recycleView.visibility = View.INVISIBLE
        }

        findViewById(R.id.fab).setOnClickListener {
            if (!isWaitingForResult) {
                finish()
            }
        }

        showResult()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (!isWaitingForResult) {
            return super.onKeyDown(keyCode, event)
        } else {
            return true
        }
    }

    /**
     * 计算结果
     */
    private fun showResult() {

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

}