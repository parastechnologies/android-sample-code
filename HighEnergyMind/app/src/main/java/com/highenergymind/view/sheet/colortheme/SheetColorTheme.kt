package com.highenergymind.view.sheet.colortheme

import androidx.recyclerview.widget.LinearLayoutManager
import com.highenergymind.R
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.data.ThemeDataModel
import com.highenergymind.databinding.SheetThemeBackLayoutBinding


class SheetColorTheme : BaseBottomSheet<SheetThemeBackLayoutBinding>() {

    lateinit var colorThemeTitleAdapter: ColorThemeTitleAdapter
    lateinit var colorThemeAdapter: ColorThemeAdapter
    lateinit var dataList: ArrayList<ThemeDataModel>
    override fun getLayoutRes(): Int {
        return R.layout.sheet_theme_back_layout
    }

    override fun init() {
        dataList = ArrayList()
        setRecyclerView()
        setImageRecyclerView()
    }

    private fun setRecyclerView() {
        dataList.add(ThemeDataModel("Nature", R.drawable.color_theme1_ic))
        dataList.add(ThemeDataModel("Color", R.drawable.color_theme1_ic))
        dataList.add(ThemeDataModel("Minimalistic", R.drawable.color_theme1_ic))
        dataList.add(ThemeDataModel("Nature", R.drawable.color_theme1_ic))

        val horizontalLayout =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.themeTitleRV.layoutManager = horizontalLayout
        colorThemeTitleAdapter = ColorThemeTitleAdapter(dataList, requireActivity())
        mBinding.themeTitleRV.adapter = colorThemeTitleAdapter

    }

    private fun setImageRecyclerView() {

        val horizontalLayout = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.themeImageRV.layoutManager = horizontalLayout
        colorThemeAdapter = ColorThemeAdapter(dataList, requireActivity())
        mBinding.themeImageRV.adapter = colorThemeAdapter

    }

}