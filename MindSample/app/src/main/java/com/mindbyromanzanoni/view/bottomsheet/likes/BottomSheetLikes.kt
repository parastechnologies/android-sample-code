package com.mindbyromanzanoni.view.bottomsheet.likes

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.data.response.eventDetails.LikesListResponse
import com.mindbyromanzanoni.databinding.BottomsheetLikesBinding
import com.mindbyromanzanoni.databinding.ItemLikesBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BottomSheetLikes(private var eventId: String) : BottomSheetDialogFragment(), View.OnClickListener {
    private var binding: BottomsheetLikesBinding? = null
    private val viewModal: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottomsheet_likes, container, false) as BottomsheetLikesBinding
        setPeekHeight()
        return binding?.root!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClickListener()
        observeDataFromViewModal()
        hitApi()
    }

    private fun setPeekHeight() {
        dialog?.setOnShowListener {
            val dialogParent = binding?.layoutCoordinate?.parent as View
            BottomSheetBehavior.from(dialogParent).peekHeight = (binding?.layoutCoordinate?.height!! * 100).toInt()
            dialogParent.requestLayout()
        }
    }

    private fun hitApi() {
        viewModal.eventId.set(eventId)
        RunInScope.ioThread {
            viewModal.hitLikeListApi()
        }
    }


    override fun getTheme(): Int {
        return R.style.CustomBottomSheetTheme
    }


    fun onClickListener() {
//        binding?.ivCancel?.setOnClickListener(this)
    }


    /** set recycler view Likes List */
    private fun initLikesRecyclerView(data: ArrayList<LikesListResponse>) {
        binding?.rvLikes?.adapter = likesAdapter
        likesAdapter.submitList(data)
    }

    private val likesAdapter = object : GenericAdapter<ItemLikesBinding, LikesListResponse>() {
        override fun getResourceLayoutId(): Int {
            return R.layout.item_likes
        }

        override fun onBindHolder(holder: ItemLikesBinding, dataClass: LikesListResponse, position: Int) {
            holder.apply {
                tvUserName.text = dataClass.userName
                circleImageView.setImageFromUrl(R.drawable.no_image_placeholder,dataClass.userImage)

                // Check if it's the last item in the list
                if (position == itemCount - 1) {
                    // Hide the view in the last item
                    view.gone()
                } else {
                    // Show the view for other items
                    view.visible()
                }
            }

        }
    }


    override fun onClick(p0: View?) {
    }

    /** Observer Response via View model*/
    @SuppressLint("NotifyDataSetChanged")
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.likeListSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            initLikesRecyclerView(data.data)
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
                binding?.rvLikes?.gone()
                binding?.shimmerCommentList?.apply {
                    visible()
                    startShimmer()
                }
            } else {
                binding?.shimmerCommentList?.apply {
                    RunInScope.mainThread {
                        stopShimmer()
                        gone()
                        binding!!.rvLikes.visible()
                    }
                }
            }
        }
    }

}