package com.mindbyromanzanoni.view.bottomsheet.journaltype

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.databinding.BottomsheetJournalTypeBinding
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.view.activity.editJournal.EditJournalActivity


class BottomSheetJournalType : BottomSheetDialogFragment() {
    private var binding: BottomsheetJournalTypeBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottomsheet_journal_type, container, false) as BottomsheetJournalTypeBinding
        setPeekHeight()
        return binding?.root!!
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClickListener()
    }
    private fun setPeekHeight() {
        dialog?.setOnShowListener {
            val dialogParent = binding?.layoutCoordinate?.parent as View
            BottomSheetBehavior.from(dialogParent).peekHeight =
                (binding?.layoutCoordinate?.height!! * 0.99).toInt()
            dialogParent.requestLayout()
        }
    }
    override fun getTheme(): Int {
        return R.style.CustomBottomSheetTheme
    }
    fun onClickListener() {
        binding?.apply {
            btnSubmit.setOnClickListener {
                 when(radioGroup.checkedRadioButtonId){
                     radioFreeForm.id->{
                         val bundle = bundleOf(AppConstants.SCREEN_TYPE to "ADD", AppConstants.ENTRY_TYPE to "1")
                         requireActivity().launchActivity<EditJournalActivity>(0, bundle) { }
                     }
                     radioJotMethod.id->{
                         val bundle = bundleOf(AppConstants.SCREEN_TYPE to "ADD", AppConstants.ENTRY_TYPE to "2")
                         requireActivity().launchActivity<EditJournalActivity>(0, bundle) { }
                     }
                     radioWeightMethod.id->{
                         val bundle = bundleOf(AppConstants.SCREEN_TYPE to "ADD", AppConstants.ENTRY_TYPE to "3")
                         requireActivity().launchActivity<EditJournalActivity>(0, bundle) { }
                     }
                     else->{
                         Toast.makeText(requireActivity(),"Please select entry type",Toast.LENGTH_SHORT).show()
                     }
                }
             }
        }
    }
}