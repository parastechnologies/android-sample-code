package com.in2bliss.ui.activity.home.fragment.myAffirmation

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.in2bliss.base.BaseFragment
import com.in2bliss.data.model.recordings.RecordingDataModel
import com.in2bliss.databinding.FragmentWrittenBinding
import com.in2bliss.ui.activity.home.affirmation.myRecordings.MyRecordingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WrittenFragment : BaseFragment<FragmentWrittenBinding>(
    layoutInflater = FragmentWrittenBinding::inflate
) {
    private val viewModel: MyRecordingsViewModel by viewModels()
    val list =
        arrayListOf(
            RecordingDataModel(mediaUri = "https://in2bliss.com.au/in2bliss/storage/app/public/uploads/music/1698917163_QDEHwmFoW3nqXBYZieLawW9poKTRVz7XWgeFMeWd.mp3".toUri()),
            RecordingDataModel(mediaUri = "https://in2bliss.com.au/in2bliss/storage/app/public/uploads/music/1698917163_QDEHwmFoW3nqXBYZieLawW9poKTRVz7XWgeFMeWd.mp3".toUri()),
            RecordingDataModel(mediaUri = "https://in2bliss.com.au/in2bliss/storage/app/public/uploads/music/1698917163_QDEHwmFoW3nqXBYZieLawW9poKTRVz7XWgeFMeWd.mp3".toUri()),
            RecordingDataModel(mediaUri = "https://in2bliss.com.au/in2bliss/storage/app/public/uploads/music/1698917163_QDEHwmFoW3nqXBYZieLawW9poKTRVz7XWgeFMeWd.mp3".toUri())
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}