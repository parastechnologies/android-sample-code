package com.app.muselink.util

import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.app.muselink.R
import soup.neumorphism.NeumorphCardView

class ToggleOnOff(val toggleOnOffNavigator : ToggleOnOffNavigator,val toggleCard:  NeumorphCardView) : CompoundButton.OnCheckedChangeListener {

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if(isChecked){
            toggleCard.setBackgroundColor(ContextCompat.getColor(buttonView!!.context, R.color.colorGreenSwitch))
            toggleOnOffNavigator.onChecked()
        }else{
            toggleCard.setBackgroundColor(ContextCompat.getColor(buttonView!!.context, R.color.transparent))
            toggleOnOffNavigator.onUnChecked()
        }
    }

    interface ToggleOnOffNavigator{
        fun onChecked()
        fun onUnChecked()
    }

}