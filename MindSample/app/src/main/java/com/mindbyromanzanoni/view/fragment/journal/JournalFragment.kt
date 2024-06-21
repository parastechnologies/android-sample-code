package com.mindbyromanzanoni.view.fragment.journal

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseFragment
import com.mindbyromanzanoni.data.response.journal.JournalListResponse
import com.mindbyromanzanoni.databinding.FragmentJournalBinding
import com.mindbyromanzanoni.databinding.ItemJournalTableBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.constant.AppConstants.JOURNAL_ID
import com.mindbyromanzanoni.utils.constant.AppConstants.POSITION
import com.mindbyromanzanoni.utils.formatDate
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.shimmerAnimationEffect
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.view.activity.editJournal.EditJournalActivity
import com.mindbyromanzanoni.view.activity.editJournal.EditJournalActivity.Companion.callBackAddJournal
import com.mindbyromanzanoni.view.activity.editJournal.EditJournalActivity.Companion.callBackUpdateJournal
import com.mindbyromanzanoni.view.activity.viewJouranl.ViewJournalActivity
import com.mindbyromanzanoni.view.activity.viewJouranl.ViewJournalActivity.Companion.callBackDeleteJournal
import com.mindbyromanzanoni.view.bottomsheet.journaltype.BottomSheetJournalType
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JournalFragment : BaseFragment<FragmentJournalBinding>(FragmentJournalBinding::inflate) {
    private val viewModal: HomeViewModel by viewModels()
    private var bottomSheetJournalType: BottomSheetJournalType? = null
    private var filteredList: ArrayList<JournalListResponse> = arrayListOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun setViewModel() {
        binding.viewModel = viewModal
    }

    private fun initData() {
        setViewModel()
        clickListeners()
        apiHit()
        observeDataFromViewModal()
        getWatcherSearchMeditation()
        callbackHandlingJournal()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun callbackHandlingJournal() {
        callBackAddJournal ={
            if (it){
                apiHit()
            }
        }
        callBackUpdateJournal ={
            if (it){
                apiHit()
            }
        }
        callBackDeleteJournal ={position,status ->
            if (status){
                filteredList.removeAt(position)
                postListAdapter.notifyDataSetChanged()
                if (filteredList.isEmpty()){
                    binding.noDataFound.visible()
                }
            }
        }
    }
    private fun apiHit() {
        RunInScope.ioThread {
            viewModal.hitJournalListApi()
        }
    }
    /** Text watcher when user search Journal list*/
    private fun getWatcherSearchMeditation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                viewModal.meditationSearchText.set(p0.toString())
                filter(p0.toString())
            }
        }
        binding.etsearch.addTextChangedListener(watcher)
    }
    /** Function to filter data based on search query*/
    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        val list: ArrayList<JournalListResponse>
        if (query.isEmpty()) {
            list = filteredList
            binding.ivCross.gone()
        } else {
            list = filteredList.filter { item ->
                item.subject.contains(query, ignoreCase = true)
            } as ArrayList<JournalListResponse>
            binding.ivCross.visible()
        }
        postListAdapter.submitList(list)
    }
    private fun clickListeners() {
        binding.apply {
            btnNewEntry.setOnClickListener {
                bottomSheetJournalType = BottomSheetJournalType()
                bottomSheetJournalType?.show(requireActivity().supportFragmentManager, "")
            }
            ivCross.setOnClickListener {
                etsearch.text?.clear()
            }
        }
    }
    /** set recycler view Donation List */
    private fun initPostRecyclerView() {
        binding.rvTable.adapter = postListAdapter
        postListAdapter.submitList(filteredList)
        if (filteredList.isEmpty()){
            binding.noDataFound.visible()
        }else{
            binding.noDataFound.gone()
        }
    }
    private val postListAdapter = object : GenericAdapter<ItemJournalTableBinding, JournalListResponse>() {
            override fun getResourceLayoutId(): Int {
                return R.layout.item_journal_table
            }
            override fun onBindHolder(holder: ItemJournalTableBinding, dataClass: JournalListResponse, position: Int) {
                holder.apply {
                    tvSubject.text = dataClass.subject
                    tvType.text = dataClass.type
                    tvDate.text = formatDate(dataClass.journalDate)

                    holder.root.setOnClickListener {
                        val bundle = bundleOf(
                            JOURNAL_ID to dataClass.journalId.toString(),
                            POSITION to position.toString(),
                            AppConstants.ENTRY_TYPE to dataClass.typeId.toString())
                        requireActivity().launchActivity<ViewJournalActivity>(0, bundle) { }
                    }
                    tvEdit.setOnClickListener {
                     /*   val bundle = bundleOf(
                            AppConstants.SCREEN_TYPE to "EDIT",
                            JOURNAL_ID to dataClass.journalId.toString(),
                            AppConstants.ENTRY_TYPE to dataClass.typeId.toString()
                        )
                        requireActivity().launchActivity<EditJournalActivity>(0, bundle) { }*/
                        val bundle = bundleOf(
                            JOURNAL_ID to dataClass.journalId.toString(),
                            POSITION to position.toString(),
                            AppConstants.ENTRY_TYPE to dataClass.typeId.toString())
                        requireActivity().launchActivity<ViewJournalActivity>(0, bundle) { }
                    }
                }
            }
        }


    override fun onResume() {
        super.onResume()
        bottomSheetJournalType?.dismiss()
    }


    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.journalListSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            filteredList = data.data
                            initPostRecyclerView()
                        } else {
                            showErrorSnack(requireActivity(), data?.message ?: "")
                        }
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg -> showErrorSnack(requireActivity(), msg) }
                    }
                }
            }
        }

        viewModal.showLoading.observe(requireActivity()) {
            if (it) {
                binding.apply {
                    rvTable.gone()
                    mainLayoutShimmer.shimmerAnimationEffect(it)
                }
            } else {
                binding.apply {
                    rvTable.visible()
                    mainLayoutShimmer.shimmerAnimationEffect(it)
                }
            }
        }
    }
}