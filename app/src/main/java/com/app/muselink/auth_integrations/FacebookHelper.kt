package com.app.muselink.auth_integrations

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.app.muselink.data.modals.responses.UserDetailModel
import org.json.JSONException
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class FacebookHelper (
    val activity: Activity,
    private val faceBookInterface: FaceBookInterface
) {


    interface FaceBookInterface {
        fun onFailure(message: String?)
        fun showProgress()
        fun hideProgress()
        fun onSuccessFaceBook(userDetailModel: UserDetailModel?)
    }

    var callbackManager: CallbackManager? = null

    fun facebookLogin() {

        faceBookInterface.showProgress()
        //  if (!CommonUtil.isFaceBookLogin()) {

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(
            activity,
            Arrays.asList("public_profile", "email")
        )
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) { // App code
                    setFaceBookData(loginResult)
                }
                override fun onCancel() { // App code
                    faceBookInterface.hideProgress()
                    faceBookInterface.onFailure("Cancel")
                }

                override fun onError(exception: FacebookException) { // App code
                    faceBookInterface.hideProgress()
                    faceBookInterface.onFailure(exception.message)
                }
            })
    }



    private fun setFaceBookData(loginResult: LoginResult?) {
        val request = GraphRequest.newMeRequest(
            loginResult?.accessToken
        ) { data, response ->

            if (data != null) {
                try {

                    val userDetailModel = UserDetailModel()

                    if (data.has("id")) {
                        val fbId = data.getString("id")
                        userDetailModel.faceBookId = fbId
                    }

                    if (data.has("name")) {
                        val name = data.getString("name")
                        userDetailModel.userName = name
                    }
                    if (data.has("email")) {
                        val fbEmail = data.getString("email")
                        userDetailModel.email = fbEmail
                    }
                    if (data.has("gender")) {
                        val gender = data.getString("gender")
                        userDetailModel.gender = gender
                    }

                    if (data.has("birthday")) {
                        val birthday = data.getString("birthday")
                        userDetailModel.birthDate = birthday
                    }

                    if (data.has("first_name")) {
                        val fbFirstName = data.getString("first_name")
                        userDetailModel.firstName = fbFirstName
                    }

                    if (data.has("last_name")) {
                        val fbLastName = data.getString("last_name")
                        userDetailModel.lastName = fbLastName
                    }

                    val jsonObject = JSONObject(data.getString("picture"))
                    if (jsonObject != null) {
                        val dataObject = jsonObject.getJSONObject("data")
                        val fbProfilePicture = URL("https://graph.facebook.com/$%7BfbId%7D/picture?width=500&height=500")
                        userDetailModel.userImage = fbProfilePicture.toString()
                    }
                    faceBookInterface.hideProgress()
                    faceBookInterface.onSuccessFaceBook(userDetailModel)
                    /*if(LoginManager.getInstance()!=null){
                        LoginManager.getInstance().logOut()
                    }*/
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
            } else {
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,gender,birthday,first_name,last_name,picture")
        request.parameters = parameters
        request.executeAsync()

    }
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

}