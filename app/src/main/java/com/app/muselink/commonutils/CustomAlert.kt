package com.app.muselink.commonutils

import android.app.Activity
import android.content.Context
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.app.muselink.R

class CustomAlert(context: Context) : AlertDialog.Builder(context), View.OnClickListener {

    var mTitle: String? = null

    var mMessage: String? = null

    var mPositiveBtnText: String? = null

    var mNegativeBtnText: String? = null

    var mDialogView: View? = null

    var mPositiveBtn: TextView? = null

    var mNegativeBtn: TextView? = null

    var mTitleTV: TextView? = null

    var mMessageTV: TextView? = null

    var mDialog: AlertDialog? = null

    var mCustomAlertListener: CustomAlertListener? = null

    fun setDialogCancelable(value: Boolean) {
        this.setCancelable(value)
    }

    fun setCustomAlertListener(customAlertListener: CustomAlertListener?) {
        this.mCustomAlertListener = customAlertListener
    }

    fun setCustomMessage(message: String?) {
        this.mMessage = message
    }

    fun setCustomMessage(spanned: Spanned?) {
        if (mDialogView != null) {
            val view =
                mDialogView!!.findViewById<TextView>(R.id.message)
            if (view != null) {
                view.text = spanned
            }
        }
    }

    fun setTitle(title: String?) {
        this.mTitle = title
    }

    fun setPositiveBtnCaption(caption: String?) {
        mPositiveBtnText = caption
    }

    fun setNegativeBtnCaption(caption: String?) {
        mNegativeBtnText = caption
    }

    fun setPositiveBtnColor(color: Int) {
        mPositiveBtn!!.setTextColor(color)
    }

    fun setNegativeBtnColor(color: Int) {
        mNegativeBtn!!.setTextColor(color)
    }

    fun setCustomAlert(activity: Activity) {
        val inflater = activity.layoutInflater
        mDialogView = inflater.inflate(R.layout.custom_alert, null)
        setView(mDialogView)
        mDialog = this.create()
        mTitleTV = mDialogView!!.findViewById(R.id.title)
        mMessageTV = mDialogView!!.findViewById(R.id.message)
        mPositiveBtn = mDialogView!!.findViewById(R.id.positive_btn)
        mNegativeBtn = mDialogView!!.findViewById(R.id.negative_btn)
        mDialog!!.setOnDismissListener {
            if (mCustomAlertListener != null) {
                mCustomAlertListener!!.onDismiss()
            }
        }
    }

    fun hideNegativeButton() {
        if (mNegativeBtn != null) {
            mNegativeBtn!!.visibility = View.GONE
        }
    }

    fun hideTitle() {
        if (mTitleTV != null) {
            mTitleTV!!.visibility = View.GONE
        }
    }

    fun getMessageTV(): TextView? {
        return mMessageTV
    }

    fun showAlertDialog(context: Context?) {
        this.setIcon(0)
        mPositiveBtn!!.setOnClickListener(this)
        mNegativeBtn!!.setOnClickListener(this)
        if (!TextUtils.isEmpty(mMessage)) {
            mMessageTV!!.text = mMessage
        }
        if (!TextUtils.isEmpty(mPositiveBtnText)) {
            mPositiveBtn!!.text = mPositiveBtnText
        } /*else {
            mPositiveBtn.setVisibility(View.GONE);
        }*/
        if (!TextUtils.isEmpty(mNegativeBtnText)) {
            mNegativeBtn!!.text = mNegativeBtnText
        } /*else {
            mNegativeBtn.setVisibility(View.GONE);
        }*/
        if (!TextUtils.isEmpty(mTitle)) {
            mTitleTV!!.text = mTitle
        } else {
            mTitleTV!!.visibility = View.GONE
        }
        if (context is Activity) {
//
//            Window window = mDialog.getWindow();
//
//            if (window != null) {
//
//                window.getAttributes().windowAnimations = R.style.dialog_animation;
//            }
            mDialog!!.show()
        }
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.positive_btn) {
            if (mCustomAlertListener != null) {
                mCustomAlertListener?.onPositiveBtnClicked()
            }
        } else if (i == R.id.negative_btn) {
            if (mCustomAlertListener != null) {
                mCustomAlertListener?.onNegativeBtnClicked()
            }
        }
        cancelDialog()
    }

    open fun cancelDialog() {
        if (mDialog != null && mDialog!!.isShowing) {
            mDialog!!.cancel()
        }
    }

    fun setCancelOnTouchOutside(dismissONTouchOutside: Boolean) {
        if (mDialog != null) {
            mDialog!!.setCanceledOnTouchOutside(dismissONTouchOutside)
        }
    }

    interface CustomAlertListener {
        fun onPositiveBtnClicked()
        fun onNegativeBtnClicked()
        fun onDismiss()
    }

}
