package com.in2bliss.ui.activity.home.fragment.favourites

import android.text.method.ScrollingMovementMethod
import android.util.TypedValue
import android.view.Gravity
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.journalStreak.JournalDetail
import com.in2bliss.databinding.ActivityFavouritesTextAffirmationBinding
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavouritesTextAffirmationActivity :
    BaseActivity<ActivityFavouritesTextAffirmationBinding>(R.layout.activity_favourites_text_affirmation) {

    @Inject
    lateinit var requestManager: RequestManager
    var categoryType: AppConstant.HomeCategory? = null

    override fun init() {
        initView()
        getIntentData()
        onClick()
    }

    private fun initView() {
        binding.apply {
            tvAffirmation.movementMethod = ScrollingMovementMethod()
        }
    }

    private fun onClick() {
        binding.ivBack2.setOnClickListener {
            finish()
        }
    }

    fun getIntentData() {

        intent.getStringExtra(AppConstant.CATEGORY_NAME)?.let { categoryName ->
            categoryType = when (categoryName) {
                AppConstant.HomeCategory.TEXT_AFFIRMATION.name -> AppConstant.HomeCategory.TEXT_AFFIRMATION
                AppConstant.HomeCategory.JOURNAL.name -> AppConstant.HomeCategory.JOURNAL
                else -> null
            }
        }


        intent.getStringExtra(AppConstant.JOURNAL_DATA)?.let { journalData ->
            val data = Gson().fromJson(
                journalData,
                JournalDetail::class.java
            )
            setData(
                data, isTextAffirmation = true
            )
        }
        if (intent.hasExtra(AppConstant.CHANGE_GRAVITY)) {
            binding.apply {
                /**
                 * changing text size and gravity to start
                 * **/
                tvAffirmation.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(com.intuit.ssp.R.dimen._18ssp)
                )
                tvAffirmation.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            }
        }

    }

    private fun setData(
        data: JournalDetail?, isTextAffirmation: Boolean = false
    ) {


        if (categoryType == AppConstant.HomeCategory.JOURNAL)
            binding.cvCategory.gone()
        else
            binding.cvCategory.visible()


        binding.tvAffirmation.text = "${data?.description}"
        binding.tvCategory.text = if (isTextAffirmation) {
            data?.categoryName
        } else {
            formatDate(
                date = data?.date.orEmpty(),
                inputFormat = "yyyy-MM-dd",
                outPutFormat = "MMMM dd, yyyy"
            )
        }

        if (isTextAffirmation) {
            data?.categoryIcon.let { image ->
                binding.ivCategory.glide(
                    requestManager = requestManager,
                    image = BuildConfig.IMAGE_BASE_URL.plus(image),
                    error = R.color.prime_purple_5F46F4,
                    placeholder = R.color.prime_purple_5F46F4
                )
            }
        }

        val backgroundImage = when (categoryType) {
            AppConstant.HomeCategory.JOURNAL -> {
                BuildConfig.JOURNAL_BASE_URL.plus(data?.backgroundImage ?: "")
            }

            AppConstant.HomeCategory.TEXT_AFFIRMATION -> {
                BuildConfig.AFFIRMATION_BASE_URL.plus(data?.backgroundImage ?: "")
            }

            else -> data?.backgroundImage ?: ""
        }

        binding.ivAffirmationBg.glide(
            requestManager = requestManager,
            image = backgroundImage,
            error = R.color.black,
            placeholder = R.color.black
        )
    }


}

