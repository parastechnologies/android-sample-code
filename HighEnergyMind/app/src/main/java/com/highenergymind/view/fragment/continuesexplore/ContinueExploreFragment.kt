package com.highenergymind.view.fragment.continuesexplore

import android.annotation.SuppressLint
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.highenergymind.R
import com.highenergymind.adapter.ExploreAdapter
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseFragment
import com.highenergymind.data.ChoiceData
import com.highenergymind.data.GetChoiceResponse
import com.highenergymind.databinding.FragmentContinueExploreBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.view.activity.signUpProcess.SignUpProcessActivity
import com.highenergymind.view.activity.signUpProcess.SignUpProcessViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContinueExploreFragment : BaseFragment<FragmentContinueExploreBinding>() {
    val viewModel by viewModels<ContinueExploreViewModel>()
    private lateinit var exploreAdapter: ExploreAdapter
    private lateinit var categoryList: List<ChoiceData>

    override fun getLayoutRes(): Int {
        return R.layout.fragment_continue_explore
    }

    override fun initViewWithData() {

        setCollectors()
        requireActivity().fullScreenStatusBar()
        onClick()
        if (::exploreAdapter.isInitialized) {
            setupRecyclerView(categoryList)
        } else {
            viewModel.getChoiceApi()
        }

    }

    private fun setCollectors() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                isLoading.collectLatest {
                    progressDialog(it)
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                getChoiceResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as GetChoiceResponse
                        setupRecyclerView(response.data)

                    })
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as SignUpProcessActivity).apply {
            setProgressMeter(com.intuit.sdp.R.dimen._32sdp)
        }
    }

    private fun onClick() {

        mBinding.contiBtn.setOnClickListener {
            if (!exploreAdapter.categoryList.none { it.isChecked == true }) {
                (activityViewModels<SignUpProcessViewModel>().value).apply {
                    map[ApiConstant.CHOICE] =
                        exploreAdapter.categoryList.filter { it.isChecked == true }.map { it.id }
                            .joinToString(separator = ",")
                }
            } else {
                (activityViewModels<SignUpProcessViewModel>().value).apply {
                    map.remove(ApiConstant.CHOICE)
                }
            }
            view?.let { it1 ->
                Navigation.findNavController(it1)
                    .navigate(R.id.action_continueExploreFragment_to_personalDetailsFragment)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView(data: List<ChoiceData>) {

        categoryList = data

        exploreAdapter = ExploreAdapter(categoryList)
        mBinding.categoryRV.adapter = exploreAdapter
    }

}