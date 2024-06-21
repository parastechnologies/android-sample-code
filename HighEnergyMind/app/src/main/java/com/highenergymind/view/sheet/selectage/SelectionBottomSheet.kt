package com.highenergymind.view.sheet.selectage

import com.highenergymind.R
import com.highenergymind.adapter.SelectionAdapter
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.databinding.SheetSelectAgeLayoutBinding

class SelectionBottomSheet(

    var dataList: List<String>, var title: String? = null,
    var desc: String? = null,
    var callBack: ((String) -> Unit)? = null
) :
    BaseBottomSheet<SheetSelectAgeLayoutBinding>() {
    private lateinit var selectionAdapter: SelectionAdapter

    override fun getLayoutRes(): Int {
        return R.layout.sheet_select_age_layout
    }

    override fun init() {
        setUpData()
        recyclerViewSetUP()
        onClick()
    }

    private fun setUpData() {
        mBinding.apply {
            title?.let {
                tvTitle.text = it
            }
            desc?.let {
                tvDescription.text = it
            }
        }

    }

    private fun onClick() {
        mBinding.apply {
            continueBtn.setOnClickListener {
                if (selectionAdapter.isSelected != -1) {
                    callBack?.invoke(selectionAdapter.dataList[selectionAdapter.isSelected])
                }
                dialog!!.dismiss()
            }
            crossIV.setOnClickListener {
                dialog!!.dismiss()
            }
        }
    }

    private fun recyclerViewSetUP() {


        selectionAdapter = SelectionAdapter(dataList, requireActivity())
        mBinding.selectAgeRV.adapter = selectionAdapter

    }
}