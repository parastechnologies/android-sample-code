package com.app.muselink.util

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import soup.neumorphism.NeumorphButton

@BindingAdapter("app:errorText")
fun setErrorMessage(view: EditText, errorMessage: String?) {
    if(errorMessage.isNullOrEmpty().not()) {
        Toast.makeText(view.context, errorMessage, Toast.LENGTH_SHORT).show()
    }
}
@BindingAdapter("app:errorText")
fun setErrorMessage(view: NeumorphButton, errorMessage: String?) {
    if(errorMessage.isNullOrEmpty().not()) {
        Toast.makeText(view.context, errorMessage, Toast.LENGTH_SHORT).show()
    }
}
@BindingAdapter("app:validation")
fun setErrorMessageValidation(view: EditText, errorMessage: String?) {
    if(errorMessage.isNullOrEmpty().not()) {
        Toast.makeText(view.context, errorMessage, Toast.LENGTH_SHORT).show()
    }
}
fun Activity.hideKeyboard() {
    currentFocus?.windowToken?.let {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?)
            ?.hideSoftInputFromWindow(it, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}
fun Activity.showKeyboard(view: View) {
    view.requestFocus()
    currentFocus?.windowToken?.let {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?)
            ?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}
fun showToast(context: Context,message:String?){
    if (message!=null){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
fun isPhoneValid(phone: String): Boolean {
    return phone.length > 8
}
fun isPasswordValid(password: String): Boolean {
    return password.length > 5
}

fun diconnectFromFb() {
    if (AccessToken.getCurrentAccessToken() == null) {
        Log.e("ifffffffffff", "AccessTokenNull")
        return
    } else {
        Log.e("ifffffffffff", "AccessTokenNotttNull")
        val graphRequest = GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/permissions/",
            null,
            HttpMethod.DELETE,
            GraphRequest.Callback { LoginManager.getInstance().logOut() })

        graphRequest.executeAsync()
    }

}
fun clearCookies(context: Context){
    CookieSyncManager.createInstance(context)
    val cookieManager: CookieManager = CookieManager.getInstance()
    cookieManager.removeAllCookie()
}