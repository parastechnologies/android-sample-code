package com.in2bliss.data.model

data class EditProfileResponse(
    val `data`: Data?,
    val message: String?,
    val status: Int?
) {
    data class Data(
        val profilePicture: String?,
        val updated_at: String?
    )
}