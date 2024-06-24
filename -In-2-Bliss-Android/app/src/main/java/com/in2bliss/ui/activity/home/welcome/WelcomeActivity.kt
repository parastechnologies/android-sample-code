package com.in2bliss.ui.activity.home.welcome

import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.content.Content
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.ActivityWelcomeBinding
import com.in2bliss.ui.activity.home.affirmation.affirmationCategories.AffirmationCategoriesActivity
import com.in2bliss.ui.activity.home.affirmationExplore.AffirmationExploreActivity
import com.in2bliss.ui.activity.home.journal.journalStreak.JournalStreakActivity
import com.in2bliss.ui.activity.home.reminder.ReminderActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>(
    layout = R.layout.activity_welcome
) {

    @Inject
    lateinit var sharedPreference: SharedPreference
    private var categoryType: String? = null

    private val adapter by lazy {
        ContentAdapter()
    }

    override fun init() {
        gettingBundle()
        onClick()
        settingRecyclerView()
    }

    private fun settingRecyclerView() {
        binding.viewPager.adapter = adapter
        val contentList = when (categoryType) {
            AppConstant.HomeCategory.GUIDED_AFFIRMATION.name -> guidedAffirmation
            AppConstant.HomeCategory.GUIDED_MEDITATION.name -> guidedMeditation
            AppConstant.HomeCategory.CREATE_AFFIRMATION.name -> createAffirmation
            AppConstant.HomeCategory.WISDOM_INSPIRATION.name -> arrayListOf()
            AppConstant.HomeCategory.JOURNAL.name -> gratitudeJournal
            else -> startUpList
        }


        adapter.submitList(contentList) {
            binding.tabLayout.visibility(
                isVisible = adapter.currentList.isNotEmpty()
            )
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ ->
        }.attach()
    }

    private fun gettingBundle() {
        intent.getStringExtra(AppConstant.CATEGORY_NAME)?.let { category ->
            categoryType = category
        }
    }

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnNext.setOnClickListener {
            val bundle = Bundle()
            val destination = when (categoryType) {
                AppConstant.HomeCategory.GUIDED_AFFIRMATION.name -> {
                    bundle.putString(
                        AppConstant.CATEGORY_NAME, AppConstant.HomeCategory.GUIDED_AFFIRMATION.name
                    )
                    AffirmationCategoriesActivity::class.java
                }

                AppConstant.HomeCategory.GUIDED_MEDITATION.name -> {
                    bundle.putString(
                        AppConstant.CATEGORY_NAME, AppConstant.HomeCategory.GUIDED_MEDITATION.name
                    )
                    AffirmationCategoriesActivity::class.java
                }

                AppConstant.HomeCategory.CREATE_AFFIRMATION.name -> {
                    bundle.putString(
                        AppConstant.CATEGORY_NAME, AppConstant.HomeCategory.CREATE_AFFIRMATION.name
                    )
                    AffirmationExploreActivity::class.java
                }

                AppConstant.HomeCategory.JOURNAL.name -> {
                    bundle.putString(
                        AppConstant.CATEGORY_NAME, AppConstant.HomeCategory.JOURNAL.name
                    )
                    if (sharedPreference.userData?.data?.journalStatus == 1) {
                        JournalStreakActivity::class.java
                    } else ReminderActivity::class.java
                }

                else -> ReminderActivity::class.java
            }

            changingStatus(
                category = categoryType ?: ""
            )

            intent(
                destination = destination,
                bundle = bundle
            )
            finish()
        }
    }

    /**
     * Changing welcome screen open status
     * @param category
     * */
    private fun changingStatus(
        category: String
    ) {
        val userData = sharedPreference.userData
        when (category) {
            AppConstant.HomeCategory.GUIDED_AFFIRMATION.name -> {
                userData?.data?.welcomeScreen?.guidedAffirmation = true
            }

            AppConstant.HomeCategory.GUIDED_MEDITATION.name -> {
                userData?.data?.welcomeScreen?.guidedMeditation = true
            }

            AppConstant.HomeCategory.CREATE_AFFIRMATION.name -> {
                userData?.data?.welcomeScreen?.createAffirmation = true
            }

            AppConstant.HomeCategory.JOURNAL.name -> {
                userData?.data?.welcomeScreen?.gratitudeJournal = true
            }

            else -> {}
        }
        sharedPreference.userData = userData
    }

    private val startUpList by lazy {
        arrayListOf(
            Content(
                title = getString(R.string.startUp_title_1),
                description = getString(R.string.startUp_description_1),
                image = R.drawable.yoga3,
                screenType = 2

            ),
            Content(
                description = getString(R.string.startUp_description_2),
                image = R.drawable.mind2,
                screenType = 2

            )
        )
    }

    private val guidedMeditation by lazy {
        arrayListOf(
            Content(
                title = getString(R.string.guided_meditation_title_1),
                description = getString(R.string.guided_meditation_description_1),
                image = R.drawable.ic_guided_meditation
            ),
            Content(
                description = getString(R.string.guided_meditation_description_2),
                image = R.drawable.ic_guided_meditation,
                screenType = 2
            ),
            Content(
                title = getString(R.string.guided_meditation_title_2),
                description = getString(R.string.guided_meditation_description_3),
                image = R.drawable.ic_guided_meditation
            )
        )
    }

    private val guidedAffirmation by lazy {
        arrayListOf(
            Content(
                title = getString(R.string.guided_affirmation_title_1),
                description = getString(R.string.guided_affirmation_description_1),
                image = R.drawable.ic_guided_affirmations
            ),
            Content(
                description = getString(R.string.guided_affirmation_description_2),
                image = R.drawable.ic_guided_affirmations
            ),
            Content(
                title = getString(R.string.guided_affirmation_title_2),
                description = getString(R.string.guided_affirmation_description_3),
                image = R.drawable.ic_guided_affirmations
            )
        )
    }

    private val guidedSleep by lazy {
        arrayListOf(
            Content(
                description = getString(R.string.guided_affirmation_description_1)
            ),
            Content(
                title = getString(R.string.guided_sleep_title_1),
                description = getString(R.string.guided_sleep_title_description_2)
            ),
            Content(
                title = getString(R.string.guided_sleep_title_2),
                description = getString(R.string.guided_sleep_title_description_3)
            )
        )
    }

    private val gratitudeJournal by lazy {
        arrayListOf(
            Content(
                description = getString(R.string.gratitude_journal_description_1),
                image = R.drawable.ic_journal1,
                screenType = 1
            ),
            Content(
                title = getString(R.string.gratitude_journal_title_1),
                description = getString(R.string.gratitude_journal_description_2),
                image = R.drawable.ic_journal1
            ),
            Content(
                title = getString(R.string.gratitude_journal_title_2),
                description = getString(R.string.gratitude_journal_description_3),
                image = R.drawable.ic_journal1
            )
        )
    }



    private val createAffirmation by lazy {
        arrayListOf(
            Content(
                title = getString(R.string.create_affirmation_title_1),
                description = getString(R.string.create_affirmation_description_1),
                image = R.drawable.ic_create_your_own_affirmations
            ),
            Content(
                title = getString(R.string.create_affirmation_title_2),
                description = getString(R.string.create_affirmation_description_2),
                image = R.drawable.ic_create_your_own_affirmations
            ),
            Content(
                title = getString(R.string.create_affirmation_title_3),
                description = getString(R.string.create_affirmation_description_3),
                image = R.drawable.ic_create_your_own_affirmations
            )
        )
    }
}

