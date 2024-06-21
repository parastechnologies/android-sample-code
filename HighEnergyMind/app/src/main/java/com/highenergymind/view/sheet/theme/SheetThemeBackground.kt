package com.highenergymind.view.sheet.theme

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.highenergymind.R
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.data.BackgroundImage
import com.highenergymind.data.ThemeData
import com.highenergymind.databinding.SheetThemeBackLayoutBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.intentComponent
import com.highenergymind.view.activity.unlockFeature.UnlockFeatureActivity

class SheetThemeBackground(val data: List<ThemeData>) :
    BaseBottomSheet<SheetThemeBackLayoutBinding>() {
    lateinit var themeAdapter: ThemeTitleAdapter
    lateinit var themeimageAdapter: ThemeImageAdapter
    var callBack: ((BackgroundImage) -> Unit)? = null
    override fun getLayoutRes(): Int {
        return R.layout.sheet_theme_back_layout
    }

    override fun init() {
        clicks()
        setRecyclerView()
        setImageRecyclerView()
    }

    private fun clicks() {
        mBinding.apply {
            crossIV.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun setRecyclerView() {


        themeAdapter = ThemeTitleAdapter(data)
        mBinding.themeTitleRV.adapter = themeAdapter.also {
            it.callBack = { inList ->
                themeimageAdapter = ThemeImageAdapter(inList, requireActivity())
                mBinding.themeImageRV.adapter = themeimageAdapter
                setImageCallback()
            }
        }

    }

    private fun setImageRecyclerView() {
        themeimageAdapter = ThemeImageAdapter(data[0].backgroundImages, requireActivity())
        mBinding.themeImageRV.adapter = themeimageAdapter
        setImageCallback()
    }

    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                themeimageAdapter.notifyDataSetChanged()
            }
        }

    private fun setImageCallback() {
        themeimageAdapter.callBack = {item,isPremium->
            if (isPremium){
                val intent = Intent(requireContext(), UnlockFeatureActivity::class.java)
                intent.putExtras(Bundle().also { bnd ->
                    bnd.putInt(AppConstant.SCREEN_FROM, R.id.musicCV)
                })
                activityResult.launch(intent)
            }else {
                callBack?.invoke(item)
            }
        }

    }
}