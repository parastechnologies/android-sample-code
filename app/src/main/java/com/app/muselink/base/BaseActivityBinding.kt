package com.app.muselink.base
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.app.muselink.util.AutoClearedValue

abstract class BaseActivityBinding<B : ViewDataBinding> : AppCompatActivity() {
    protected var binding: B? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBinding: B = DataBindingUtil.setContentView(this,getLayout())
        binding = AutoClearedValue(this, dataBinding).get()
    }

    protected abstract fun getLayout(): Int

}