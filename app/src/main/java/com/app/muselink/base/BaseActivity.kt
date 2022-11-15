package com.app.muselink.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.muselink.R

abstract class BaseActivity : AppCompatActivity() {
    abstract fun getLayout(): Int
    var builder: AlertDialog.Builder? = null
    private var progressDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        progressDialog = getAlertDialog(this)
    }

    private fun getAlertDialog(
        context: Context
    ): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context,R.style.MyDialogTheme)
        val customLayout: View = layoutInflater.inflate(R.layout.progress_dialog, null)
        builder.setView(customLayout)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    protected fun showDialog() {
        if (progressDialog != null && !progressDialog!!.isShowing) {
            progressDialog?.show()
        }
    }

    protected fun hideDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog?.dismiss()
        }
    }


}
