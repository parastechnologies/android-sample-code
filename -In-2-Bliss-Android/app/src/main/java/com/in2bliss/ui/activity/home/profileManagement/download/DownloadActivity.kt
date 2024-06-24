package com.in2bliss.ui.activity.home.profileManagement.download

import android.app.Activity
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.musicDetails.MusicCustomizeDetails
import com.in2bliss.data.model.musicDetails.MusicDetails
import com.in2bliss.data.roomDataBase.OfflineAudioEntity
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.ActivityDownloadBinding
import com.in2bliss.domain.DownloadFIleInInternalStorageInterface
import com.in2bliss.domain.DownloadStatusListenerInterface
import com.in2bliss.domain.RoomDataBaseInterface
import com.in2bliss.ui.activity.home.player.PlayerActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class DownloadActivity : BaseActivity<ActivityDownloadBinding>(
    layout = R.layout.activity_download
) {

    @Inject
    lateinit var sharedPreference: SharedPreference

    @Inject
    lateinit var downloadStatusListener: DownloadStatusListenerInterface

    @Inject
    lateinit var roomDataBaseInterface: RoomDataBaseInterface

    @Inject
    lateinit var downloadFIleInInternalStorageInterface: DownloadFIleInInternalStorageInterface

    private val viewModel: DownloadVM by viewModels()

    override fun init() {
        binding.toolBar.tvTitle.text = getString(R.string.downloads)
        binding.toolBar.ivBack.setOnClickListener { finish() }
        backPressed()
        recyclerView()
        onClick()
        observer()
    }

    private fun onClick() {
        binding.toolBar.ivBack.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun backPressed() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(Activity.RESULT_OK)
                finish()
            }
        })
    }

    private fun observer() {
        lifecycleScope.launch {
            downloadStatusListener.getDownloadStatus().collectLatest { downloadStatus ->
                if (downloadStatus.downloadStatus == AppConstant.DownloadStatus.DOWNLOAD_COMPLETE) {
                    addDownloadedData()
                }
            }
        }
    }

    private fun recyclerView() {
        binding.rvDownload.adapter = viewModel.downloadAdapter
        viewModel.downloadAdapter.listener = { musicDetails ->
            navigate(
                data = musicDetails
            )
        }
        addDownloadedData()
        deleteDownloadedMusic()

        viewModel.downloadAdapter.deleteListener = { data, position ->
            lifecycleScope.launch {
                /** Deleting from the room data base */
                withContext(Dispatchers.IO) {
                    roomDataBaseInterface.delete(data)
                }

                /** Deleting from the internal storage */
                downloadFIleInInternalStorageInterface.deleteFile(
                    fileName = data.musicFileName,
                    context = this@DownloadActivity,
                    isMusicDirectory = false,
                    isComplete = {}
                )
                downloadFIleInInternalStorageInterface.deleteFile(
                    fileName = data.musicImageFileName,
                    context = this@DownloadActivity,
                    isMusicDirectory = false,
                    isComplete = {}
                )

                /** Deleting from the adapter*/
                val currentList = viewModel.downloadAdapter.currentList.toMutableList()
                currentList.removeAt(position)
                viewModel.downloadAdapter.submitList(currentList) {
                    binding.tvNoDatFound.visibility(isVisible = viewModel.downloadAdapter.currentList.isEmpty())
                }
            }
        }
    }

    private fun addDownloadedData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val currentUserOffline: ArrayList<OfflineAudioEntity> = arrayListOf()


            roomDataBaseInterface.getList().forEach { offlineData ->
                if (sharedPreference.userData?.data?.id == offlineData.userId) {
                    currentUserOffline.add(offlineData)
                }
            }
            val list=currentUserOffline.reversed()
            withContext(Dispatchers.Main) {
                viewModel.downloadAdapter.submitList(
                    list.toMutableList()
                ) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.tvNoDatFound.visibility(isVisible = viewModel.downloadAdapter.currentList.isEmpty())
                    }
                }
            }
        }
    }

    private fun deleteDownloadedMusic() {
        val swipeCallBack = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    val isDelete = viewModel.downloadAdapter.currentList[position].isDelete
                    viewModel.downloadAdapter.currentList[position].isDelete = isDelete.not()
                }
                if (direction == ItemTouchHelper.RIGHT) {
                    viewModel.downloadAdapter.currentList[position].isDelete = false
                }
                viewModel.downloadAdapter.notifyItemChanged(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rvDownload)
    }

    private fun navigate(data: OfflineAudioEntity) {

        val musicDetail = MusicDetails(
            musicTitle = data.title,
            musicDescription = null,
            musicId = null,
            musicUrl = data.musicFilePath,
            musicViews = null,
            musicFavouriteStatus = null,
            musicThumbnail = data.backMusicImageFilePath,
            backgroundMusicUrl = data.backMusicFilePath,
            musicBackground = data.musicImageFilePath,
            backgroundMusicTitle = data.backgroundMusicTitle,
            downloadCategoryName = data.downloadCategoryName,
        )

        Log.d("sacsacsacsasac", "navigate: $musicDetail")
        val bundle = bundleOf(
            AppConstant.MUSIC_DETAILS to Gson().toJson(musicDetail),
            AppConstant.CATEGORY_NAME to AppConstant.HomeCategory.DOWNLOAD.name,
        )

        intent(
            destination = PlayerActivity::class.java,
            bundle = bundle
        )
    }
}

