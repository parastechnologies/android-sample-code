package com.in2bliss.ui.activity.home.profileManagement.termAndPrivacy

import com.in2bliss.R
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.NotificationList
import com.in2bliss.data.model.TermsAndPrivacyList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TermAndPrivacyVM @Inject constructor() : BaseViewModel() {


    val termAndPrivacyAdapter by lazy {
        TermAndPrivacyAdapter()
    }

    val termAndPrivacyList = arrayListOf(
        TermsAndPrivacyList(
            "Nunc safittis mattis sollicitudin. Pallentesque",
            "Nunc sagittis mattis sollicitudin. Pellentesque eu fringilla leo. Aliquam congue lectus at sapien sodales fringilla. Maecenas mi dui, egestas sed erat quis, placerat auctor ligula.".plus("\nNunc sagittis mattis sollicitudin. Pellentesque eu fringilla leo. Aliquam congue lectus at sapien sodales fringilla. Maecenas mi dui, egestas sed erat quis, placerat auctor ligula.")
        ),
        TermsAndPrivacyList(
            "Nunc safittis mattis sollicitudin. Pallentesque",
            "Nunc sagittis mattis sollicitudin. Pellentesque eu fringilla leo. Aliquam congue lectus at sapien sodales fringilla. Maecenas mi dui, egestas sed erat quis, placerat auctor ligula."
        ),
        TermsAndPrivacyList(
            "Nunc safittis mattis sollicitudin. Pallentesque",
            "Nunc sagittis mattis sollicitudin. Pellentesque eu fringilla leo. Aliquam congue lectus at sapien sodales fringilla. Maecenas mi dui, egestas sed erat quis, placerat auctor ligula."
        )
    )

    override fun retryApiRequest(apiName: String) {
    }
}

