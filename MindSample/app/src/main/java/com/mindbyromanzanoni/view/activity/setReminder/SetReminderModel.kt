package com.mindbyromanzanoni.view.activity.setReminder

data class SetReminderModel(var time:String,var repeat:String,var label:String){
    var isTimeValid=time.isEmpty()
    var isLabelEmpty=label.isEmpty()
}