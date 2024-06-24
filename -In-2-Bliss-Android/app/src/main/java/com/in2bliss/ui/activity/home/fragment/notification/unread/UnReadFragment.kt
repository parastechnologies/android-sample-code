package com.in2bliss.ui.activity.home.fragment.notification.unread

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.in2bliss.base.BaseFragment
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.FragmentUnReadBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.notification.NotificationVM
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UnReadFragment : BaseFragment<FragmentUnReadBinding>(
    layoutInflater = FragmentUnReadBinding::inflate
) {

    private val viewModel: NotificationVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView()
        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.getNotificationList().collectLatest {
                lifecycleScope.launch {
                    viewModel.adapter.submitData(it)
                }
            }
        }
        viewModel.adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Error -> {
                    binding.pbPopular.gone()
                    binding.pbSeeAllNewData.gone()
                }

                is LoadState.Loading -> {
                    val isEmptyList = viewModel.adapter.snapshot().isEmpty()
                    binding.pbPopular.visibility(isVisible = isEmptyList)
                    binding.pbSeeAllNewData.visibility(isVisible = isEmptyList.not())
                }

                is LoadState.NotLoading -> {
                    val isListEmpty = viewModel.adapter.snapshot().items.isEmpty()
                    binding.tvNoDatFound.visibility(
                        isVisible = isListEmpty
                    )
                    binding.pbPopular.gone()
                    binding.pbSeeAllNewData.gone()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.readResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = requireActivity(),
                    success = {
                        lifecycleScope.launch(Dispatchers.Main) {
                            val currentList =
                                viewModel.adapter.snapshot().items.toMutableList()
                            currentList.removeAt(viewModel.position)
                            viewModel.adapter.submitData(
                                pagingData = PagingData.from(
                                    currentList
                                )
                            )
                            binding.tvNoDatFound.visibility(isVisible = currentList.isEmpty())

                        }
                    }, error = { message, apiName ->
                        requireActivity().alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    }
                )

//                val currentList=viewModel.adapter.snapshot().items.toMutableList()
//                currentList.removeAt(viewModel.position)
//                viewModel.adapter.submitData(pagingData = PagingData.from(currentList))
//                binding.tvNoUnRead.visibility(isVisible = currentList.isEmpty())
            }
        }
    }

    private fun recyclerView() {
        binding.rvNotification.adapter = viewModel.adapter
        viewModel.adapter.listener = { notificationId, position ->
            viewModel.notificationId = notificationId.toString()
            viewModel.position = position
            viewModel.retryApiRequest(
                apiName = ApiConstant.NOTIFICATION_READ
            )
            viewModel.isDelete = true
        }
    }
}

