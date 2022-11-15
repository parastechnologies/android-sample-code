package com.app.muselink.ui.bottomsheets

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.app.muselink.R
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.util.showToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import soup.neumorphism.NeumorphButton
import soup.neumorphism.NeumorphCardView
import javax.inject.Inject

@AndroidEntryPoint
class MoreInfoChatBottomSheet : BottomSheetDialogFragment() {
    private val requestApi = MutableLiveData<HashMap<String, String>>()
    @Inject
    lateinit var repository: ApiRepository

    private var fromId:String?=""
    private var toId:String?=""
    private var matchingType:String?=""

    private val search = requestApi.switchMap { requestApi ->
        repository.removeMatches(requestApi)
    }
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)

        fromId=arguments?.getString("fromId","")
        toId=arguments?.getString("toId","")
        matchingType=arguments?.getString("matchingType","")

    }
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        getDialog()?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val contentView = View.inflate(context, R.layout.more_info_chat_bottomsheet, null)
        val btnClose = contentView.findViewById<NeumorphButton>(R.id.btnClose)
        val removeMatches = contentView.findViewById<NeumorphCardView>(R.id.removeMatches)
        val report = contentView.findViewById<NeumorphCardView>(R.id.report)
        val block = contentView.findViewById<NeumorphCardView>(R.id.block)
        btnClose.setOnClickListener {
            this.dismiss()
        }
        removeMatches.setOnClickListener {
            val hashmap = hashMapOf("fromId" to fromId.toString(), "toId" to toId.toString(), "matchingType" to matchingType.toString())
            requestApi.value = hashmap
        }
        report.setOnClickListener {}
        block.setOnClickListener{

        }
        dialog.setContentView(contentView)
        setupObservers()
    }
    private fun setupObservers() {
        search.observe(requireActivity(), {
            when (it.status) {
                Resource.Status.SUCCESS -> {

                }
                Resource.Status.ERROR -> {

                }
                Resource.Status.LOADING -> {

                }
            }
        })
    }
    /*removeMatches*/
}