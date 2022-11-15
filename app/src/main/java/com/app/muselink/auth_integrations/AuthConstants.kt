package com.app.muselink.auth_integrations

object AuthConstants{
    /*todo Instagram Constants*/
    const val AUTHURL = "https://api.instagram.com/oauth/authorize/?"
    const val TOKENURL = "https://api.instagram.com/oauth/access_token"

    const val REDIRECT_URI="https://www.hotcocoasoftware.com/"
    const val CLIENT_SECRET="39fec41438f325e527b2a3811f595d14"
    const val CLIENT_ID="770317030554444"
    const val authURLFull = AUTHURL + "client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&scope=user_profile,user_media&response_type=code"
    const val tokenURLFull = TOKENURL + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&redirect_uri=" + REDIRECT_URI + "&grant_type=authorization_code"

    /*todo Sound Cloud Constants*/
    const val AUTHURL_SOUND_CLOUD = "https://api.soundcloud.com/connect?"
    const val TOKENURL_SOUND_CLOUD  = "https://api.instagram.com/oauth/access_token"

    const val REDIRECT_URI_SOUND_CLOUD  ="https://www.hotcocoasoftware.com/"
    const val CLIENT_SECRET_SOUND_CLOUD ="39fec41438f325e527b2a3811f595d14"
    const val CLIENT_ID_SOUND_CLOUD ="770317030554444"
    const val authURLFull_SOUND_CLOUD  = AUTHURL_SOUND_CLOUD + "client_id=" + CLIENT_ID_SOUND_CLOUD + "&redirect_uri=" + REDIRECT_URI_SOUND_CLOUD + "&scope=default&state=encrypted_session_info"
    const val tokenURLFull_SOUND_CLOUD  = TOKENURL + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&redirect_uri=" + REDIRECT_URI + "&grant_type=authorization_code"


}