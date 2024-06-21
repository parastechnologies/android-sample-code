package com.mindbyromanzanoni.view.bottomsheet.comments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.data.response.eventDetails.CommentListResponse
import com.mindbyromanzanoni.databinding.BottomsheetCommentsBinding
import com.mindbyromanzanoni.databinding.ItemCommentsBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.getTimeInAgo
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.hideKeyboard
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.validators.Validator
import com.mindbyromanzanoni.viewModel.HomeViewModel
import com.mindbyromanzanoni.widget.KeyboardUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class BottomSheetComments(private var eventId: String, private var type: Int = 1) :
    BottomSheetDialogFragment(), View.OnClickListener {
    private val viewModal: HomeViewModel by viewModels()
    private var commentList: ArrayList<CommentListResponse>? = arrayListOf()
    private var binding: BottomsheetCommentsBinding? = null
    private var sheetBehavior: BottomSheetBehavior<View>? = null
    lateinit var dismissCallBackSheet: (Boolean, Int) -> Unit

    @Inject
    lateinit var validator: Validator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set the window no floating style
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogThemeNoFloating)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottomsheet_comments,
            container,
            false
        ) as BottomsheetCommentsBinding
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setPeekHeight()
        return binding?.root!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.viewModel = viewModal
        viewModal.type.set(type)
        onClickListener()
        observeDataFromViewModal()
        hitApi()
        setSpinner()
    }

    private fun hitApi() {
        viewModal.eventId.set(eventId)
        RunInScope.ioThread {
            viewModal.hitCommentListApi()
        }
    }

    /** set Spinner Top Comment */
    private fun setSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.comment_list,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.spinnerComment?.adapter = adapter
    }

    private fun setPeekHeight() {
        dialog?.setOnShowListener {
            val dialogParent = binding?.layoutCoordinate?.parent as View
            sheetBehavior = BottomSheetBehavior.from(dialogParent)
            sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            sheetBehavior?.peekHeight = (binding?.layoutCoordinate?.height!! * 100)
            dialogParent.requestLayout()
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetTheme
    }

    @SuppressLint("ClickableViewAccessibility")
    fun onClickListener() {
        binding?.apply {
            btnSend.setOnClickListener {
                if (validator.validateComment(requireActivity(), binding)) {
                    RunInScope.ioThread {
                        viewModal.hitAddCommentApi()
                        Handler(Looper.getMainLooper()).post {
                            requireActivity().hideKeyboard(etComment)
                            etComment.clearFocus()
                        }
                    }
                } else {
                    requireActivity().hideKeyboard(etComment)
                    etComment.clearFocus()
                }
            }
            rvComments.setOnTouchListener { view, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    sheetBehavior?.setDraggable(false)
                } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                    sheetBehavior?.isDraggable = true
                }
                view.onTouchEvent(motionEvent)
                true
            }
            etComment.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Handler(Looper.getMainLooper()).post {
                        requireActivity().hideKeyboard(etComment)
                        etComment.clearFocus()
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

    /** set recycler view Comment List */
    private fun initLikesRecyclerView() {
        binding?.rvComments?.adapter = commentsAdapter
        commentsAdapter.submitList(commentList)
    }

    private val commentsAdapter =
        object : GenericAdapter<ItemCommentsBinding, CommentListResponse>() {
            override fun getResourceLayoutId(): Int {
                return R.layout.item_comments
            }

            override fun onBindHolder(
                holder: ItemCommentsBinding,
                dataClass: CommentListResponse,
                position: Int
            ) {
                holder.apply {
                    tvUserName.text = dataClass.commentedByName
                    tvComment.text = dataClass.commentDesc.toString()
                    circleImageView.setImageFromUrl(
                        R.drawable.no_image_placeholder,
                        dataClass.commentedByImage
                    )
                    tvOneDayAgo.text = getTimeInAgo(dataClass.commentedOn)

                    // Check if it's the last item in the list
                    if (position == itemCount - 1) {
                        view.gone()
                    } else {
                        // Show the view for other items
                        view.visible()
                    }
                }
            }
        }

    /** Observer Response via View model*/
    @SuppressLint("NotifyDataSetChanged")
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.commentListSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            commentList = data.data ?: arrayListOf()
                            initLikesRecyclerView()
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                data?.message ?: "",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(
                            requireActivity(),
                            isResponse.message ?: "",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModal.addCommentSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            commentList?.add(0, data.data)
                            commentsAdapter.submitList(commentList)
                            commentsAdapter.notifyDataSetChanged()
                            dismissCallBackSheet.invoke(true, commentList?.size ?: 0)
                            viewModal.commentDesc.set("")
                        } else {
                            Toast.makeText(requireActivity(), data?.message ?: "", Toast.LENGTH_SHORT).show()
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(
                            requireActivity(),
                            isResponse.message ?: "",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        viewModal.showLoading.observe(requireActivity()) {
            if (it) {
                binding?.rvComments?.gone()
                binding?.shimmerCommentList?.apply {
                    visible()
                    startShimmer()
                }
            } else {
                binding?.shimmerCommentList?.apply {
                    RunInScope.mainThread {
                        stopShimmer()
                        gone()
                        binding!!.rvComments.visible()
                    }
                }
            }
        }
    }

    override fun onClick(p0: View?) {}
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissCallBackSheet.invoke(true, commentList?.size ?: 0)
    }

}