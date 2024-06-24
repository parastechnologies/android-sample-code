package com.in2bliss.ui.activity.home.searchTODOREMOVE

import androidx.databinding.ObservableField
import com.in2bliss.R
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.HomeData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : BaseViewModel() {

    val search = ObservableField("")
    val showResult = ObservableField("")

    var isSearchFilterResult = false

    val recentSearchedAdapter by lazy {
        SearchRecentAdapter()
    }

    val searchKeywordAdapter by lazy {
        SearchKeywordAdapter()
    }

    val searchFilerResultAdapter by lazy {
        SearchFilterResultAdapter()
    }

    val recentChatList = arrayListOf(
        "Anxiety", "Affirmations for anxiety", "Self-love"
    )

    val searchKeyword = arrayListOf(
        "Sleep", "Anxiety", "Stress", "Anger", "Work"
    )

    val searchFilterList = arrayListOf(
        HomeData.MusicDescription(
            "I am deserving of happiness and fulfillment.",
            image = R.drawable.ic_music_temp_bg,
            isFav = false
        ),
        HomeData.MusicDescription(
            "I am grateful for all the abundance in my life.",
            image = R.drawable.ic_temp_music_2,
            isFav = true
        ),
        HomeData.MusicDescription(
            "I appreciate the people and opportunities that come into my life.",
            image = R.drawable.ic_music_temp_bg_3,
            isFav = false
        ),
        HomeData.MusicDescription(
            "I am grateful for all the abundance in my life.",
            image = R.drawable.ic_temp_music_1,
            isFav = true
        )
    )

    override fun retryApiRequest(apiName: String) {

    }
}