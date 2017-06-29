package com.ocwvar.whattoeat.Activities

import android.animation.Animator
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ocwvar.darkpurple.Units.ToastMaker
import com.ocwvar.whattoeat.R
import com.ocwvar.whattoeat.Unit.DATA

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-25 下午9:54
 * File Location com.ocwvar.whattoeat
 * This file use to :   主界面
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    //动画背景
    private lateinit var mainPanel: View
    //动画执行标记，标记为True时，不接受控件点击事件和按键点击事件
    private var isShowingAnimation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= 21) {
            //只有SDK>=21才能使用全屏布局
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainPanel = findViewById(R.id.mainPanel)
        findViewById(R.id.fab).setOnClickListener(this@MainActivity)
        findViewById(R.id.main_menu_manager).setOnClickListener(this@MainActivity)
    }

    override fun onResume() {
        super.onResume()
        if (mainPanel.visibility == View.VISIBLE) {
            //如果页面恢复的时候，背景已经展开，则要使其淡去
            val fadeAnimation: Animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.fade_out).let {
                it.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation?) {
                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        mainPanel.visibility = View.INVISIBLE
                        isShowingAnimation = false
                    }

                    override fun onAnimationStart(p0: Animation?) {
                        isShowingAnimation = true
                    }
                })
                it
            }
            mainPanel.startAnimation(fadeAnimation)
        }
    }

    override fun onClick(view: View) {
        if (isShowingAnimation) {
            //当前正在执行动画，不接受控件点击事件反馈
            return
        }

        when (view.id) {
            R.id.fab -> {
                //点击中央按钮
                //先判断是否有已启用的菜单
                if (DATA.enabledMenus().size <= 0) {
                    //没有启用菜单
                    ToastMaker.show(this@MainActivity, R.string.main_ERROR_no_enabled_menus, ToastMaker.TOAST_COLOR_WARNING)
                    return
                }

                //开始执行动画并转跳页面
                if (Build.VERSION.SDK_INT >= 21) {
                    show21Animation()
                } else {
                    showCompatAnimation()
                }
            }

            R.id.main_menu_manager -> {
                //启动菜单编辑页面进行菜单编辑操作
                val animBundle: Bundle?
                if (Build.VERSION.SDK_INT >= 21) {
                    animBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this@MainActivity,
                            Pair<View, String>(findViewById(R.id.fab), "fab")
                    ).toBundle()
                } else {
                    animBundle = null
                }

                startActivity(Intent(this@MainActivity, MenuManagerActivity::class.java), animBundle)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        //当前正在执行动画，不接受按键点击事件反馈
        if (!isShowingAnimation) {
            return super.onKeyDown(keyCode, event)
        } else {
            return true
        }
    }

    /**
     * 显示 SDK>=21 的动画效果，动画完成后将自动转跳到结果页面：ResultActivity
     */
    @RequiresApi(21)
    private fun show21Animation() {
        val fab: View = findViewById(R.id.fab)

        //动画显示在中央FAB的中心背后
        val centerX: Int = fab.left + (fab.right - fab.left) / 2
        val centerY: Int = fab.top + (fab.bottom - fab.top) / 2

        //圆形动画对象
        val animator: Animator = ViewAnimationUtils.createCircularReveal(mainPanel, centerX, centerY, 0f, (mainPanel.bottom).toFloat()).let {
            //动画监听
            it.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    //动画执行结束后转跳至目标页面
                    val animBundle: Bundle?
                    if (Build.VERSION.SDK_INT >= 21) {
                        animBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                this@MainActivity,
                                Pair<View, String>(findViewById(R.id.fab), "fab")
                        ).toBundle()
                    } else {
                        animBundle = null
                    }

                    startActivity(Intent(this@MainActivity, ResultActivity::class.java), animBundle)
                    isShowingAnimation = false
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                    isShowingAnimation = true
                }
            })
            //动画时长
            it.duration = 600L
            it
        }

        mainPanel.visibility = View.VISIBLE
        animator.start()
    }

    /**
     * 显示兼容动画效果，动画完成后将自动转跳到结果页面：ResultActivity
     */
    private fun showCompatAnimation() {
        val fadeAnimation: Animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.fade_in).let {
            it.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    startActivity(Intent(this@MainActivity, ResultActivity::class.java))
                    isShowingAnimation = false
                }

                override fun onAnimationStart(p0: Animation?) {
                    isShowingAnimation = true
                }
            })
            it
        }

        mainPanel.visibility = View.VISIBLE
        mainPanel.startAnimation(fadeAnimation)
    }

}