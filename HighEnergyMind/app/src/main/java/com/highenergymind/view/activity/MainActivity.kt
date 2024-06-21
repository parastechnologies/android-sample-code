package com.highenergymind.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.highenergymind.R
import com.highenergymind.utils.fullScreenStatusBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fullScreenStatusBar()

    }
}