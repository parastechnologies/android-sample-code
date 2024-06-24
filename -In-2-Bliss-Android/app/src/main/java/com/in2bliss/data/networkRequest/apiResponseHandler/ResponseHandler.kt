package com.in2bliss.data.networkRequest.apiResponseHandler

import android.app.Activity
import androidx.core.os.bundleOf
import com.in2bliss.R
import com.in2bliss.ui.activity.ProgressDialog
import com.in2bliss.ui.activity.auth.stepTwo.StepTwoActivity
import com.in2bliss.ui.activity.home.profileManagement.manageSubscription.ManageSubscriptionActivity
import com.in2bliss.ui.activity.messageDialogBox
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import org.json.JSONObject
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Handling the api request if success , error and failure or getting the exceptions
 * @param apiRequest api request body passing as higher order function
 * @param apiName name of the api which going to execute
 * */
suspend fun <T> safeApiRequest(
    apiRequest: suspend () -> Response<T>,
    apiName: String
): Resource<T> {
    return try {
        val response = apiRequest.invoke()

        when (response.code()) {
            200 -> {
                Resource.Success(data = response.body())
            }
            400 -> {
                val errorMessage = try {
                    val error = response.errorBody()?.string()
                    val jsonObject = JSONObject(error ?: "")
                    jsonObject.get("message") as String
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    "Something went wrong"
                }
                Resource.Error(message = errorMessage)
            }

            401 -> {
                Resource.UnAuthorized(message = "Session Expired, Please Login Again")
            }

            402 -> {
                Resource.SubscriptionExpired(message = "Subscription expired")
            }
            403 -> {
                Resource.UserSuspend(message = "Your Profile Suspend By Admin")
            }

            404, 500 -> {
                Resource.Failure(message = "Server is down", apiName = apiName)
            }
            else -> {
                val errorMessage = try {
                    val error = response.errorBody()?.string()
                    val jsonObject = JSONObject(error ?: "")
                    jsonObject.get("message") as String
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    "Something went wrong"
                }
                Resource.Failure(message = errorMessage, apiName = apiName)
            }
        }
    } catch (exception: Exception) {
        when (exception) {
            is UnknownHostException -> {
                Resource.Failure(
                    message = "No internet connectivity detected.Please reconnect and try again.",
                    apiName = apiName
                )
            }

            is SocketTimeoutException -> {
                Resource.Failure(message = "Connection Time Out", apiName = apiName)
            }

            else -> Resource.Failure(message = "Something went wrong", apiName = apiName)
        }
    }
}

/**
 * Handling the parsed data from the [safeApiRequest]
 * @param response getting the response returned by [safeApiRequest]
 * @param context
 * @param success calling success lambda if success
 * @param error calling error lambda if getting any error or failure
 * */
fun <T> handleResponse(
    response: Resource<T>,
    context: Activity,
    success: (response: T) -> Unit,
    showToast: Boolean = true,
    errorBlock: ((() -> Unit))? = null,
    error: (message: String, apiName: String) -> Unit
) {


    when (response) {
        is Resource.Success -> {

            ProgressDialog.hideProgress()
            response.data?.let { success.invoke(it) }
        }

        is Resource.Error -> {
            ProgressDialog.hideProgress()
            errorBlock?.invoke()
            response.message?.let { message ->
                if (showToast) context.showToast(
                    message = message
                )
            }
        }

        is Resource.Failure -> {
            ProgressDialog.hideProgress()
            response.message?.let { message ->
                response.apiName?.let { apiName ->
                    error.invoke(message, apiName)
                }
            }
        }

        is Resource.UnAuthorized -> {
            ProgressDialog.hideProgress()
            val bundle = bundleOf(
                AppConstant.IS_LOGIN to true
            )
            context.intent(
                destination = StepTwoActivity::class.java,
                bundle = bundle
            )
            context.finishAffinity()
        }

        is Resource.SubscriptionExpired -> {
            context.messageDialogBox(
                message = context.getString(R.string.your_subscription_is_expired),
                cancelOutSide = false
            ) {
                val bundle =
                    bundleOf(AppConstant.SUBSCRIPTION to AppConstant.SubscriptionStatus.SUBSCRIPTION_EXPIRED.name)
                context.intent(
                    destination = ManageSubscriptionActivity::class.java,
                    bundle = bundle
                )
            }
        }

        is Resource.UserDeleteByAdmin -> {
            context.messageDialogBox(
                message = context.getString(R.string.user_delete_by_admin),
                cancelOutSide = false
            ) {
                val bundle = bundleOf(
                    AppConstant.IS_LOGIN to true
                )
                context.intent(
                    destination = StepTwoActivity::class.java,
                    bundle = bundle
                )
            }
        }

        is Resource.Loading -> {
            ProgressDialog.showProgress(activity = context)
        }

        is Resource.UserSuspend -> {

            ProgressDialog.hideProgress()
            val bundle = bundleOf(
                AppConstant.IS_LOGIN to true
            )
            response.message?.let { message ->
                if (showToast) context.showToast(
                    message = message
                )
            }
            context.intent(
                destination = StepTwoActivity::class.java,bundle
            )
            context.finishAffinity()
        }
    }
}