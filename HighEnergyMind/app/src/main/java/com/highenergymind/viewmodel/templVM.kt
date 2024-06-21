//package com.highenergymind.viewmodel
//
//package com.example.plazapalm.views.signup
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.os.Bundle
//import android.provider.Settings
//import android.util.Log
//import android.view.View
//import android.widget.RadioGroup
//import androidx.databinding.ObservableBoolean
//import androidx.databinding.ObservableField
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.plazapalm.MainActivity
//import com.example.plazapalm.R
//import com.example.plazapalm.datastore.DataStoreUtil
//import com.example.plazapalm.datastore.LOGIN_DATA
//import com.example.plazapalm.models.SignupResponseModel
//import com.example.plazapalm.networkcalls.ApiEnums
//import com.example.plazapalm.networkcalls.ApiProcessor
//import com.example.plazapalm.networkcalls.Repository
//import com.example.plazapalm.networkcalls.RetrofitApi
//import com.example.plazapalm.pref.PreferenceFile
//import com.example.plazapalm.utils.*
//import com.example.plazapalm.utils.Constants.CHECK_INTERNET
//import com.example.plazapalm.utils.Constants.ConfirmPasswordCantEmpty
//import com.example.plazapalm.utils.Constants.DEVICE_TOKEN
//import com.example.plazapalm.utils.Constants.DEVICE_TYPE
//import com.example.plazapalm.utils.Constants.DeviceType
//import com.example.plazapalm.utils.Constants.EmailCantEmpty
//import com.example.plazapalm.utils.Constants.FirstNameCantEmpty
//import com.example.plazapalm.utils.Constants.LastNameCantEmpty
//import com.example.plazapalm.utils.Constants.PasswordCantEmpty
//import com.example.plazapalm.validation.ValidatorUtils.isEmailValid
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.SetOptions
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import org.json.JSONObject
//import retrofit2.Response
//import javax.inject.Inject
//
//@HiltViewModel
//class SignupVM @Inject constructor(
//    private var repository : Repository,
//    private val dataStore: DataStoreUtil,
//    private val pref: PreferenceFile,
//) : ViewModel() {
//    //get Android (Device Id)..
//    @SuppressLint("HardwareIds")
//    private val androidId = Settings.Secure.getString(
//        MainActivity.context.get()!!.contentResolver,
//        Settings.Secure.ANDROID_ID
//    )
//    var firstName = ObservableField("")
//    var lastName = ObservableField("")
//    var email = ObservableField("")
//    var password = ObservableField("")
//    var ischecked = ObservableField("")
//    var confirmPassword = ObservableField("")
//    var sendFirebaseSignUpToken = ObservableField("")
//    var businessStatus = ObservableBoolean(false)
//    val firestore = FirebaseFirestore.getInstance()
//
//    fun selectOption(radioGroup: RadioGroup, radioButton: View) {
//        radioGroup.check(radioButton.id)
//
//        if (radioButton.id.equals(R.id.rb_yes)) {
//            businessStatus.set(true)
//            ischecked.set("YES")
//            Log.e("DSDSQQEE", "TRUEE")
//        } else {
//            businessStatus.set(false)
//            ischecked.set("NO")
//            Log.e("DSDSQQEE", "FALSE")
//        }
//
//    }
//
//    fun onClicks(view: View) {
//
//        when (view.id) {
//            R.id.clsignupMain -> {
//                (MainActivity.context.get() as Activity).hideKeyboard()
//            }
//            R.id.tvSignupWantLogin -> {
//                view.navigateWithId(R.id.action_signUpFragment_to_loginFragment)
//            }
//            R.id.btnSignup -> {
//                if (CommonMethods.context.isNetworkAvailable()) {
//                    if (validation()) {
//                        callSignUp(view)
//                    }
//                } else {
//                    CommonMethods.showToast(CommonMethods.context, CHECK_INTERNET)
//                }
//            }
//
//        }
//    }
//
//    fun validation(): Boolean {
//        when {
//            firstName.get()?.trim().toString().isEmpty() -> {
//                CommonMethods.showToast(CommonMethods.context, FirstNameCantEmpty)
//                return false
//            }
//            lastName.get()?.trim().toString().isEmpty() -> {
//                CommonMethods.showToast(CommonMethods.context, LastNameCantEmpty)
//                return false
//            }
//            email.get()?.trim().toString().isEmpty() -> {
//                CommonMethods.showToast(CommonMethods.context, EmailCantEmpty)
//                return false
//            }
//            !isEmailValid(email.get()!!) -> {
//                CommonMethods.showToast(CommonMethods.context, "Please Enter Valid Email ")
//                return false
//            }
//            password.get()?.trim().toString().isEmpty() -> {
//                CommonMethods.showToast(CommonMethods.context, PasswordCantEmpty)
//                return false
//            }
//            confirmPassword.get()?.trim().toString().isEmpty() -> {
//                CommonMethods.showToast(CommonMethods.context, ConfirmPasswordCantEmpty)
//                return false
//            }
//            confirmPassword.get() != password.get() -> {
//                CommonMethods.showToast(CommonMethods.context, "Password does not match")
//                return false
//            }
//            ischecked.get().toString().isEmpty() -> {
//                CommonMethods.showToast(CommonMethods.context, "Please slecet account type ")
//                return false
//            }
//            else -> {
//                return true
//            }
//        }
//    }
//
//    /**Call Signup Api here..**/
//    private fun callSignUp(view: View) = viewModelScope.launch {
//
//
//        val body = JSONObject()
//        body.put(Constants.FIRST_NAME, firstName.get())
//        body.put(Constants.LAST_NAME, lastName.get())
//        body.put(Constants.EMAIL, email.get())
//        body.put(Constants.PASSWORD, password.get())
//        body.put(DEVICE_TOKEN, pref.retrieveFirebaseToken())
//        body.put(DEVICE_TYPE, DeviceType)
//
//        Log.e("ASASZZZ", businessStatus.get().toString())
//        Log.e(
//            "LOGIN--REQUEST--",
//            email.get() + "-- " + password.get() + "--Local--" + sendFirebaseSignUpToken.get()
//                .toString() +
//                    " --->>>> " + pref.retrieveFirebaseToken() + "--" + Constants.DeviceType + "--" +
//                    firstName.get() + "--" + lastName.get()
//        )
//
//        repository.makeCall(
//            ApiEnums.SIGNUP,
//            loader = true,
//            saveInCache = false,
//            getFromCache = false,
//            requestProcessor = object : ApiProcessor<Response<SignupResponseModel>> {
//                override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<SignupResponseModel> {
//
//                    return retrofitApi.signUp(
//                        FirstName = firstName.get()?.trim().toString(),
//                        LastName = lastName.get()?.trim().toString(),
//                        Email = email.get()?.trim().toString(),
//                        Password = password.get()?.trim().toString(),
//                        /*DeviceToken*/
//                        sendFirebaseSignUpToken.get(),
//                        DeviceType,
//                        businessStatus.get()
//                    )
//                }
//
//                override fun onResponse(res: Response<SignupResponseModel>) {
//                    /**Save Register Data...**/
//                    if (res.isSuccessful) {
//                        if (res.code() == 200) {
//                            if (res.body()?.status == 200) {
//
//                                dataStore.saveObject(LOGIN_DATA, res.body())
//
//                                val bundle = Bundle()
//                                bundle.putString("comingFrom", "signup")
//                                bundle.putString("email", email.get())
//                                CommonMethods.showToast(
//                                    CommonMethods.context,
//                                    res.body()?.message.toString()
//                                )
//                                view.navigateWithId(
//                                    R.id.action_signUpFragment_to_verifyEmailFragment,
//                                    bundle
//                                )
//                                Log.e("RESSPONSEE", res.body().toString())
//
//                                /** Add user on firestore **/
//                                FireStorechatVM().firestoreLogin(pref,res.body()!!.data!!.userId!!)
//
//                            } else {
//                                CommonMethods.showToast(CommonMethods.context, res.body()?.message.toString())
//                                view.navigateWithId(R.id.action_signUpFragment_to_loginFragment)
//
//                            }
//                        }
//
//                    } else {
//
//                        CommonMethods.showToast(
//                            CommonMethods.context,
//                            "Oops! Something went wrong"
//                        )
//                    }
//
//                }
//
//                override fun onError(message: String) {
//                    super.onError(message)
//                    CommonMethods.showToast(CommonMethods.context, message)
//                }
//            })
//    }
//
//}