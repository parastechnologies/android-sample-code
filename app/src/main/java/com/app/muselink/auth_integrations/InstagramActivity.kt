package com.app.muselink.auth_integrations

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.app.muselink.R
import com.app.muselink.base.BaseActivity
import com.app.muselink.constants.AppConstants
import com.app.muselink.constants.RequestCodeConstants
import kotlinx.android.synthetic.main.activity_webview_auth.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.jvm.Throws

@Suppress("DEPRECATION")
class InstagramActivity : BaseActivity() {


    lateinit var code: String

    override fun getLayout(): Int {
        return R.layout.activity_webview_auth
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initObservers()
    }

    private fun initObservers() {

    }

//    private fun connectInstagramObserver(): Observer<in ConnectInstagramResponse> {
//        return Observer {
//            if (it.isSuccess()) {
//                val userName = it.connectInstagram?.username
//                InstaTask(this, userName!!).execute()
//            } else {
//                ToastUtil.error(this,it.message)
//                val intent = Intent()
////                intent.putExtra(AppConstants.INSTAGRAM_RES, "false")
//                setResult(Activity.RESULT_OK, intent)
//                finish()
//            }
//        }
//    }

    private fun initViews() {
        webView?.isVerticalScrollBarEnabled = false
        webView?.isHorizontalScrollBarEnabled = false
        webView?.settings?.javaScriptEnabled = true
        webView?.loadUrl(AuthConstants.authURLFull)
        webView?.webViewClient = MyWVClient()
    }

    internal inner class MyWVClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            if (request.url.toString().startsWith(AuthConstants.REDIRECT_URI)) {
                handleUrl(request.url.toString())
                return true
            }
            return false
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith(AuthConstants.REDIRECT_URI)) {
                handleUrl(url)
                return true
            }
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
        }
    }

    fun handleUrl(url: String) {
        if (url.contains("code")) {
            val temp = url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            code = temp[1]
            Log.d("asasasas", code)
            MyAsyncTask(code, this).execute()
        } else if (url.contains("error")) {
            val temp = url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class MyAsyncTask(private var code: String, private var context: Context) :
        AsyncTask<URL, Int, String>() {
        var accessTokenString = ""
        var userId = ""

        override fun onPreExecute() {

        }

        override fun doInBackground(vararg urls: URL): String? {
            try {
                val url = URL(AuthConstants.tokenURLFull)
                val httpsURLConnection =
                    url.openConnection() as HttpsURLConnection
                httpsURLConnection.requestMethod = "POST"
                httpsURLConnection.doInput = true
                //httpsURLConnection.doOutput = true
                val outputStreamWriter =
                    OutputStreamWriter(httpsURLConnection.outputStream)
                outputStreamWriter.write(
                    "client_id=" + AuthConstants.CLIENT_ID +
                            "&client_secret=" + AuthConstants.CLIENT_SECRET +
                            "&grant_type=authorization_code" +
                            "&redirect_uri=" + AuthConstants.REDIRECT_URI +
                            "&code=" + code.replace("#_", "")
                )
                outputStreamWriter.flush()
                val response: String? = streamToString(httpsURLConnection.inputStream)
                val jsonObject =
                    JSONTokener(response).nextValue() as JSONObject
                 accessTokenString = jsonObject.getString("access_token")
                userId = jsonObject.getString("user_id")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //getLongLivedToken(accessTokenString)
            return accessTokenString + "userId" + userId
        }

        override fun onPostExecute(result: String?) {
            val accessToken = result?.substringBefore("userId")!!
            val userId = result.substringAfter("userId")
            if(userId.isNullOrEmpty().not()){
                val intent = Intent()
                intent.putExtra(AppConstants.INSTAGRAM_SOCIAL_ID,userId)
                intent.putExtra(AppConstants.INSTAGRAM_ACCESS_TOKEN,accessToken)
                setResult(RequestCodeConstants.REQUEST_CODE_INSTAGRAM_LOGIN,intent)
                finish()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class InstaTask(private var context: Context, private var userName: String) :
        AsyncTask<URL, Int, String>() {

        var businessEmail = ""
        var profile_pic_url_hd = ""
        var followers_count = ""
//        var biography = ""
        var isPrivateString = "0"
        var isBusinessString = "0"
        var is_private = false
        var is_business_account = false
        var businessCategory = ""
        override fun onPreExecute() {

        }

        override fun doInBackground(vararg urls: URL): String? {
            try {
                val url = URL("https://www.instagram.com/$userName/?__a=1")
                val httpsURLConnection =
                    url.openConnection() as HttpsURLConnection
                httpsURLConnection.requestMethod = "GET"
                httpsURLConnection.doInput = true
                httpsURLConnection.connect()
                val response: String? = streamToString(httpsURLConnection.inputStream)
                val jsonObject =
                    JSONTokener(response).nextValue() as JSONObject
                Log.d("asdasdasdas", jsonObject.toString())
                val resultObject = jsonObject.getJSONObject("graphql").getJSONObject("user")
                profile_pic_url_hd = resultObject.getString("profile_pic_url_hd")
                followers_count = resultObject.getJSONObject("edge_followed_by").getString("count")
                //biography = resultObject.getString("biography")
                is_private = resultObject.getBoolean("is_private")
                is_business_account = resultObject.getBoolean("is_business_account")
                businessCategory = resultObject.getString("business_category_name")

                if(businessCategory.isNullOrEmpty())
                    businessCategory = ""
                if (is_private)
                    isPrivateString = "1"
                if (is_business_account)
                    isBusinessString = "1"
                if (businessEmail == "null")
                    businessEmail = ""
                businessEmail = resultObject.getString("business_email")
//                ToastUtil.success(
//                    context,
//                    " $businessEmail $profile_pic_url_hd $followers_count $is_private $is_business_account"
//                )
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("error1", e.message.toString())
            }
            return "businessEmail" + businessEmail + "profilePicUrl" + profile_pic_url_hd + "count" + followers_count + "isPrivateString" + isPrivateString + "isBusinessString" + isBusinessString+ "businessCategory" + businessCategory
        }

        override fun onPostExecute(result: String?) {

            val businessEmail =
                result?.substringAfter("businessEmail")?.substringBefore("profilePicUrl")
            val profilePicUrl = result?.substringAfter("profilePicUrl")?.substringBefore("count")
            val followersCount = result?.substringAfter("count")?.substringBefore("isPrivateString")
            //val biography = result?.substringAfter("biography")?.substringBefore("isPrivateString")
            val isPrivateString =
                result?.substringAfter("isPrivateString")?.substringBefore("isBusinessString")
            val isBusinessString = result?.substringAfter("isBusinessString")?.substringBefore("businessCategory")
            val categories = result?.substringAfter("businessCategory")

        }
    }

    private fun getLongLivedToken(result: String?) {
//        try {
//            val url = URL(InstagramConstants.LONG_TOKEN_URL)
//            val httpsURLConnection =
//                url.openConnection() as HttpsURLConnection
//            httpsURLConnection.requestMethod = "GET"
//            httpsURLConnection.doInput = true
//            httpsURLConnection.doOutput = true
//            val outputStreamWriter =
//                OutputStreamWriter(httpsURLConnection.outputStream)
//            outputStreamWriter.write(
//                "client_secret=" + AppConstants.CLIENT_SECRET +
//                        "&access_token" + result +
//                        "&grant_type=ig_exchange_token"
//            )
//            outputStreamWriter.flush()
//            val response: String? = streamToString(httpsURLConnection.inputStream)
//            val jsonObject =
//                JSONTokener(response).nextValue() as JSONObject
//            Log.d("asdasdasdas", jsonObject.toString())
//            accessTokenString = jsonObject.getString("access_token")
//            Log.d("result2", accessTokenString)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("error1", e.message.toString())
//        }
    }

    /*****  Converting stream to string  */
    @Throws(IOException::class)
    fun streamToString(`is`: InputStream?): String? {
        var str = ""
        if (`is` != null) {
            val sb = StringBuilder()
            var line: String?
            try {
                val reader =
                    BufferedReader(InputStreamReader(`is`))
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                reader.close()
            } finally {
                `is`.close()
            }
            str = sb.toString()
        }
        return str
    }
}