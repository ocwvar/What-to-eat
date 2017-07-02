package com.ocwvar.whattoeat.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.ocwvar.darkpurple.Units.ToastMaker
import com.ocwvar.whattoeat.Adapter.RecordAdapter
import com.ocwvar.whattoeat.R
import com.ocwvar.whattoeat.Unit.DATAHelper
import com.ocwvar.whattoeat.Unit.Menu
import com.umeng.analytics.MobclickAgent

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-7-1 下午7:36
 * File Location com.ocwvar.whattoeat.Activities
 * This file use to :   记录列表界面
 */
class RecordManagerActivity : AppCompatActivity(), RecordAdapter.CallBack {

    private val adapter: RecordAdapter = RecordAdapter(this@RecordManagerActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_manager)
        title = getString(R.string.record_title)

        (findViewById(R.id.toolbar) as Toolbar).let {
            setSupportActionBar(it)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        (findViewById(R.id.recycleView) as RecyclerView).let {
            it.layoutManager = LinearLayoutManager(this@RecordManagerActivity, LinearLayoutManager.VERTICAL, false)
            it.setHasFixedSize(false)
            it.adapter = adapter
        }

    }

    override fun onClick(record: Menu, position: Int) {
        startActivity(Intent(this@RecordManagerActivity, ResultActivity::class.java).let {
            it.action = ResultActivity.ACTIONS.ACTION_LOAD_RECORD
            it.putExtra(ResultActivity.ACTIONS.EXTRA_RECORD_OBJECT, record)
            it
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this@RecordManagerActivity)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this@RecordManagerActivity)
    }

    override fun onLongClick(record: Menu, position: Int) {
        //长按记录项目，显示是否删除记录对话框
        AlertDialog.Builder(this@RecordManagerActivity)
                .setMessage(R.string.record_dialog_deleting_title)
                .setPositiveButton(R.string.record_dialog_deleting_yes, { p0, p1 ->
                    if (DATAHelper(this@RecordManagerActivity).removeRecord(record.title)) {
                        ToastMaker.show(this@RecordManagerActivity, R.string.record_deleting_success, ToastMaker.TOAST_COLOR_NORMAL)
                        adapter.notifyItemRemoved(position)
                    } else {
                        ToastMaker.show(this@RecordManagerActivity, R.string.record_deleting_fail, ToastMaker.TOAST_COLOR_WARNING)
                    }
                    p0.dismiss()
                })
                .setNegativeButton(R.string.menu_list_dialog_delete_no, { p0, p1 -> p0.dismiss() })
                .show()
    }

}