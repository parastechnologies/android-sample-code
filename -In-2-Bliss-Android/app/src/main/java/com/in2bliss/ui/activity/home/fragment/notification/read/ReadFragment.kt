package com.in2bliss.ui.activity.home.fragment.notification.read

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.in2bliss.base.BaseFragment
import com.in2bliss.databinding.FragmentReadBinding
import com.in2bliss.ui.activity.home.notification.NotificationVM
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReadFragment : BaseFragment<FragmentReadBinding>(
    layoutInflater = FragmentReadBinding::inflate
) {
    private val viewModel: NotificationVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView()
        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.getNotificationListRead().collectLatest {
                lifecycleScope.launch {
                    viewModel.adapterRead.submitData(it)
                }
            }
        }

        viewModel.adapterRead.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Error -> {
                    binding.pbPopular.gone()
                    binding.pbSeeAllNewData.gone()
                }
                is LoadState.Loading -> {
                    val isEmptyList = viewModel.adapterRead.snapshot().isEmpty()
                    binding.pbPopular.visibility(isVisible = isEmptyList)
                    binding.pbSeeAllNewData.visibility(isVisible = isEmptyList.not())
                }
                is LoadState.NotLoading -> {
                    val isListEmpty = viewModel.adapterRead.snapshot().items.isEmpty()
                    binding.tvNoDatFound.visibility(
                        isVisible = isListEmpty
                    )
                    binding.pbPopular.gone()
                    binding.pbSeeAllNewData.gone()
                }
            }
        }
    }

    private fun recyclerView() {
        binding.rvNotification.adapter = viewModel.adapterRead
    }

}

