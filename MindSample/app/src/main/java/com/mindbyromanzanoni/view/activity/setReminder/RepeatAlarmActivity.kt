package com.mindbyromanzanoni.view.activity.setReminder

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.databinding.ActivityRepeatAlarmBinding
import com.mindbyromanzanoni.databinding.ItemReminderBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.utils.ReminderClass
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.getWeekName
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepeatAlarmActivity : BaseActivity<ActivityRepeatAlarmBinding>() {
    private var weekIds:String=""
    override fun getLayoutRes() = R.layout.activity_repeat_alarm
    override fun initView() {
        setToolbar()
        getIntentData()
        onClickListener()
        binding.setAdapter()
    }
    override fun viewModel() {}
    private fun setToolbar() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
    private fun getIntentData() {
         weekIds = intent.getStringExtra("value")?:""
    }
    private fun onClickListener() {
        binding.btnSave.setOnClickListener {
            weekIds = reminderAdapter.currentList.filter(ReminderClass::isSelected).joinToString { it.id.toString() }
            val intent = Intent()
            intent.putExtra("value", weekIds)
            setResult(Activity.RESULT_OK, intent)
            finishActivity()
        }
    }
    private fun ActivityRepeatAlarmBinding.setAdapter() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@RepeatAlarmActivity)
            adapter = reminderAdapter
            reminderAdapter.submitList(markAsSelected())
        }
    }
    private fun markAsSelected(): ArrayList<ReminderClass> {
        val list= getWeekName()
         weekIds.split(",").forEach {ids->
             for(i in list.indices){
                 if (list[i].id.toString().trim()==ids.trim()){
                     list[i].isSelected=true
                 }
             }
       }
       return list
    }
    private val reminderAdapter = object : GenericAdapter<ItemReminderBinding, ReminderClass>() {
        override fun getResourceLayoutId(): Int {
            return R.layout.item_reminder
        }
        @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
        override fun onBindHolder(holder: ItemReminderBinding,dataClass: ReminderClass,position: Int) {
            holder.apply {
                tvTime.text = "Every " + dataClass.name
                val icon = if (dataClass.isSelected) R.drawable.tick_circle_green else 0
                selectedIcon.setImageResource(icon)
                holder.root.setOnClickListener {
                    val selected = !dataClass.isSelected
                    dataClass.isSelected = selected
                    notifyItemChanged(position)
                }
            }
        }
    }
}