package com.app.muselink.ui.bottomsheets.description

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.app.muselink.R
import com.app.muselink.data.modals.responses.getDescription.GetDescriptionData
import com.app.muselink.model.ui.DescriptionModel
import com.app.muselink.databinding.DescriptionBottomsheetBinding
import com.app.muselink.ui.base.dialogfragment.BaseViewModelDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DescriptionBottomSheet(var data: GetDescriptionData?) :
    BaseViewModelDialogFragment<DescriptionBottomsheetBinding, DescriptionViewModel>() {
    /**
     * ViewModel Class
     * */
    override fun getViewModelClass(): Class<DescriptionViewModel> {
        return DescriptionViewModel::class.java
    }

   /**
    * [getLayout]
    * */
    override fun getLayout(): Int {
        return R.layout.description_bottomsheet
    }

    /**
     * [onCreateDialog]
     * */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from(bottomSheet!!).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    /**
     * [onViewCreated]
     * */
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.vm = viewModel
        binding?.edtDescription?.text = data?.description
        val descriptionArrayList = ArrayList<DescriptionModel>()
        if (data?.projectRoles != null && data?.projectRoles!!.isNotEmpty()) {
            descriptionArrayList.add(
                DescriptionModel(
                    "Project Roles",
                    "", 1
                )
            )
            for (model in data?.projectRoles!!) {
                descriptionArrayList.add(
                    DescriptionModel(
                        model.roleName!!,
                        model.projectRoleIcon!!
                    )
                )
            }
        }
        if (data?.projectGoals != null && data?.projectGoals!!.isNotEmpty()) {
            descriptionArrayList.add(
                DescriptionModel(
                    "Milestones",
                    "", 1
                )
            )
            for (model in data?.projectGoals!!) {
                descriptionArrayList.add(
                    DescriptionModel(
                        model.goalName!!,
                        model.goalIcon!!
                    )
                )
            }
        }
        val imgClose = view.findViewById<ImageView>(R.id.imgClose)
        imgClose.setOnClickListener {
            dismiss()
        }
        val descriptionAdapter = DescriptionAdapter(requireActivity())
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding?.recyclerView?.layoutManager = layoutManager
        binding?.recyclerView?.adapter = descriptionAdapter
        descriptionAdapter.setData(descriptionArrayList)
        binding?.recyclerView?.isNestedScrollingEnabled = false
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (descriptionAdapter.getItemViewType(position) == 1) 2 else 1
            }
        }
        binding?.recyclerView?.setOnTouchListener { v, motionEvent ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            v.onTouchEvent(motionEvent)
            true
        }
    }



}