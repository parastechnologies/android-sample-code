package com.mindbyromanzanoni.view.fragment.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseFragment
import com.mindbyromanzanoni.data.response.edification.EdificationTypeListResponse
import com.mindbyromanzanoni.data.response.home.EventListResponse
import com.mindbyromanzanoni.data.response.home.ScreenType
import com.mindbyromanzanoni.data.response.meditation.MeditationTypeListResponse
import com.mindbyromanzanoni.databinding.FragmentHomeBinding
import com.mindbyromanzanoni.databinding.ItemHomeBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.sharedPreference.SharedPrefs
import com.mindbyromanzanoni.utils.checkType
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.getTimeInAgo
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.startZoomMeeting
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.view.activity.edificationVideoPlayer.EdificationVideoPlayerActivity
import com.mindbyromanzanoni.view.activity.eventDetails.EventDetailActivity
import com.mindbyromanzanoni.view.activity.nowPlaying.NowPlayingActivity
import com.mindbyromanzanoni.view.activity.openPdfViewer.OpenPdfActivity
import com.mindbyromanzanoni.view.bottomsheet.comments.BottomSheetComments
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val viewModal: HomeViewModel by viewModels()
    lateinit var listCallBackSheet: (Boolean) -> Unit
    private var eventList: ArrayList<EventListResponse>? = arrayListOf()

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        setRecyclerViewAdapter()
        setViewModel()
        observeDataFromViewModal()
        apiHit()
        getCallbackFromEventList()
        setOnClickListener()
    }
    private fun setRecyclerViewAdapter(){
        binding.rvPosts.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvPosts.adapter = postListAdapter
    }

    private fun setOnClickListener() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener {
                apiHit()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getCallbackFromEventList() {
        EventDetailActivity.listCallBackSheet =
            { positions, status, commentCount, likesCount, isLiked ->
                if (status) {
                    eventList!![positions].totalComments = commentCount
                    eventList!![positions].totalFavourites = likesCount
                    if (isLiked == 1) {
                        eventList!![positions].isFavoritedbyUser = false
                    } else if (isLiked == 2) {
                        eventList!![positions].isFavoritedbyUser = true
                    }
                    postListAdapter.submitList(eventList?.toMutableList())
                    postListAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun setViewModel() {
        binding.viewModel = viewModal
    }

    private fun apiHit() {
        RunInScope.ioThread {
            viewModal.hitEventListApi()
        }
    }

    /** set recycler view Donation List */
    @SuppressLint("NotifyDataSetChanged")
    private fun initPostRecyclerView(data: ArrayList<EventListResponse>?) {
        binding.swipeRefreshLayout.isRefreshing = false

        postListAdapter.submitList(null)
        postListAdapter.submitList(data?.toMutableList())

    }

    /** Observer Response via View model*/
    @SuppressLint("NotifyDataSetChanged")
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.eventListSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            eventList = data.data
                            CoroutineScope(Dispatchers.Main).launch {
                                initPostRecyclerView(eventList)
                            }
                        } else {
                            showErrorSnack(requireActivity(), data?.message ?: "")
                        }
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg -> showErrorSnack(requireActivity(), msg) }
                    }
                }
                if (eventList.isNullOrEmpty()) {
                    binding.noDataFound.visible()
                } else {
                    binding.noDataFound.gone()
                }
            }
        }
        lifecycleScope.launch {
            viewModal.favouriteEventStatusSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        /*if (data?.success == true) {
                            //listCallBackSheet.invoke(true)
                        } else {
                           // showErrorSnack(requireActivity(), data?.message ?: "")
                        }*/
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg -> showErrorSnack(requireActivity(), msg) }
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
                            listCallBackSheet.invoke(true)
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                data?.message ?: "",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        viewModal.showLoading.observe(requireActivity()) {
            if (it) {
                binding.rvPosts.gone()
                binding.mainShimmerLayout.visible()
            } else {
                binding.rvPosts.visible()
                binding.mainShimmerLayout.gone()
            }
        }
    }

    private val postListAdapter = object : GenericAdapter<ItemHomeBinding, EventListResponse>() {
        override fun getResourceLayoutId(): Int {
            return R.layout.item_home
        }
        override fun submitList(list: MutableList<EventListResponse?>?) {
            super.submitList(if (list != null) ArrayList(list) else null)
        }
        override fun onBindHolder(
            holder: ItemHomeBinding,
            dataClass: EventListResponse,
            position: Int) {
            holder.apply {
                tvDesc.text = dataClass.eventDesc
                ivAddComment.setImageFromUrl(
                    R.drawable.no_image_placeholder,
                    sharedPrefs.getUserData()?.userImage ?: ""
                )
                ivHome.setImageFromUrl(R.drawable.placeholder_mind, dataClass.videoThumbImage)
                if (dataClass.totalComments<=0){
                    tvViewComments.gone()
                }else{
                    tvViewComments.visible()
                    tvViewComments.text = getString(R.string.view_all).plus("comments")
                }
                tvLikes.text = dataClass.totalFavourites.toString().plus(" ")
                    .plus(getString(R.string._100_likes))
                tvTime.text = getTimeInAgo(dataClass.createdOn)
                /*if (dataClass.videoHlsLink == null) {
                    ivPlayButton.gone()
                } else {
                    ivPlayButton.visible()
                }*/
                if (dataClass.zoomLink == null) {
                    layoutZoom.gone()
                } else {
                    layoutZoom.visible()
                }
                tvDesc.setOnClickListener {
                    layoutMain.performClick()
                }
                /*ivPlayButton.setOnClickListener {
                    layoutMain.performClick()
                }*/
                layoutMain.setOnClickListener {
                    when (dataClass.type) {
                        ScreenType.EVENTS.type -> {
                            val bundle = bundleOf(
                                AppConstants.EVENT_ID to dataClass.id,
                                AppConstants.POSITION to position
                            )
                            requireActivity().launchActivity<EventDetailActivity>(0, bundle) { }
                        }

                        ScreenType.MEDITATION.type -> {
                            val model = MeditationTypeListResponse(
                                title = dataClass.title,
                                videoName = dataClass.mediaPath ?: "",
                                videoThumbImage = dataClass.videoThumbImage
                            )
                            val dataJson = Gson().toJson(model)
                            val bundle = bundleOf(
                                AppConstants.SCREEN_TYPE to AppConstants.MEDITATION_SCREEN,
                                AppConstants.MEDIATION_DETAILS to dataJson)
                            requireActivity().launchActivity<NowPlayingActivity>(0, bundle) { }
                        }
                        ScreenType.EDIFICATION.type -> {
                            val model = EdificationTypeListResponse(
                                content = dataClass.eventDesc,
                                title = dataClass.title,
                                videoName = dataClass.mediaPath,
                                videoThumbImage = dataClass.videoThumbImage,
                                videoHlsLink = dataClass.videoHlsLink
                            )
                            val dataJson = Gson().toJson(model)
                            val bundle = bundleOf(AppConstants.EDIFICATION_DATA to dataJson)
                            requireActivity().launchActivity<EdificationVideoPlayerActivity>(
                                0,
                                bundle
                            ) {}
                        }

                        ScreenType.RESOURCE.type -> {
                         /*   val model = ResourceTypeList(
                                pdfFileName = dataClass.docFileName,
                                title = dataClass.title,
                                content = dataClass.eventDesc,
                                imageName = dataClass.videoThumbImage
                            )
                            val dataJson = Gson().toJson(model)
                            val bundle = bundleOf(AppConstants.RESOURCE_DETAILS to dataJson)
                            requireActivity().launchActivity<ResourceDetailActivity>(0, bundle) { }*/
                            val bundle = bundleOf(AppConstants.PDF_URL to  dataClass.docFileName)
                            requireActivity().launchActivity<OpenPdfActivity>(0, bundle) { }
                        }
                    }
                }
                tvViewComments.setOnClickListener {
                    viewModal.type.set(dataClass.type)
                    val bottomSheetComments =
                        BottomSheetComments(dataClass.id.toString(), dataClass.type)
                    bottomSheetComments.show(requireActivity().supportFragmentManager, "")
                    bottomSheetComments.dismissCallBackSheet = { status, count ->
                        if (status) {
                            dataClass.totalComments = count
                            notifyDataSetChanged()
                        }
                    }
                }
                ivShare.setOnClickListener {
                    shareEvent(dataClass.id, dataClass.type)
                }
                ivComment.setOnClickListener {
                    tvViewComments.performClick()
                }
                ivLike.setOnClickListener {
                    viewModal.eventId.set(dataClass.id.toString())
                    viewModal.type.set(dataClass.type)
                    if (dataClass.isFavoritedbyUser) {
                        viewModal.isFavourite.set(false)
                    } else {
                        viewModal.isFavourite.set(true)
                    }
                    if (dataClass.isFavoritedbyUser) {
                        dataClass.totalFavourites -= 1
                        dataClass.isFavoritedbyUser = false
                    } else {
                        dataClass.totalFavourites += 1
                        dataClass.isFavoritedbyUser = true
                    }
                    RunInScope.ioThread {
                        viewModal.hitUpdateFavouriteEventStatusApi()
                    }

                    notifyDataSetChanged()
                    /*listCallBackSheet = { status ->
                        if (status) {
                            if (dataClass.isFavoritedbyUser) {
                                dataClass.totalFavourites = dataClass.totalFavourites - 1
                                dataClass.isFavoritedbyUser = false
                            } else {
                                dataClass.totalFavourites = dataClass.totalFavourites + 1
                                dataClass.isFavoritedbyUser = true
                            }
                            notifyItemChanged(position)
                        }
                    }*/
                }
                holder.etAddComment.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        viewModal.commentDesc.set(holder.etAddComment.text?.trim().toString())
                        viewModal.eventId.set(dataClass.id.toString())
                        viewModal.type.set(dataClass.type)
                        RunInScope.ioThread {
                            viewModal.hitAddCommentApi()
                        }
                        listCallBackSheet = { status ->
                            if (status) {
                                dataClass.totalComments = dataClass.totalComments + 1
                                holder.etAddComment.text?.clear()
                                notifyDataSetChanged()
                            }
                        }
                        true
                    } else {
                        false
                    }
                }
                layoutZoom.setOnClickListener {
                    startZoomMeeting(requireContext(), "2324")
                }
                if (dataClass.isFavoritedbyUser) {
                    ivLike.setBackgroundResource(R.drawable.liked_ic)
                } else {
                    ivLike.setBackgroundResource(R.drawable.ic_heart)
                }
            }
        }
    }
    fun shareEvent(eventId: Int, eventType: Int) {
        val deepLinkUrl =
            Uri.parse("https://api.mindbyroman.com/share.html?EventId=$eventId&Type=$eventType")
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this ${checkType(eventType)}: $deepLinkUrl")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Share using"))
    }
}
