package com.in2bliss.ui.activity.auth.stepThree

import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.ActivityStepThreeBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.auth.stepFour.StepFourActivity
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class StepThreeActivity : BaseActivity<ActivityStepThreeBinding>(
    layout = R.layout.activity_step_three
) {

    @Inject
    lateinit var sharedPreference: SharedPreference
    private val viewModel: StepThreeViewModel by viewModels()
    private var countryItems: ArrayList<CountryName>? = null
    var adapter: CountryAdapter? = null


    override fun init() {
        binding.data = viewModel
        onClick()
        observer()
        initList()
        setSpinner()
    }

    private fun setSpinner() {
        adapter = CountryAdapter(
            this,
            countryItems
        )
        binding.spSpinner.adapter = adapter
        binding.spSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?, position: Int, id: Long
            ) {

                val clickedItem: CountryName =
                    parent.getItemAtPosition(position) as CountryName
                viewModel.countryName = clickedItem.name
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun observer() {
        lifecycleScope.launch {
            viewModel.addNameResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = this@StepThreeActivity,
                    success = { _ ->
                        val userData = sharedPreference.userData
                        userData?.data?.profileStatus = 2
                        sharedPreference.userData = userData

                        intent(
                            destination = StepFourActivity::class.java
                        )
                    },
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message,
                            retry = {
                                viewModel.retryApiRequest(
                                    apiName = apiName
                                )
                            }
                        )
                    }
                )
            }
        }
    }

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            if (isValidate()) {
                viewModel.retryApiRequest(
                    apiName = ApiConstant.ADD_NAME
                )
            }

        }

        binding.etName.doAfterTextChanged { text ->
            binding.ivNameCancel.visibility((text?.length ?: "".length) > 0)
        }

        binding.ivNameCancel.setOnClickListener {
            viewModel.name.set("")
        }
    }

    private fun isValidate(): Boolean {
        return when {
            (viewModel.name.get()?.trim().isNullOrEmpty()) -> {
                viewModel.name.set("")
                binding.etName.requestFocus()
                binding.etName.setSelection(0)
                showToast(getString(R.string.please_enter_your_full_name))
                false
            }

            (viewModel.countryName == "Select Country") -> {
                showToast(getString(R.string.please_select_country_name))
                false
            }

            else -> true
        }
    }

    private fun initList() {
        countryItems = ArrayList()
        countryItems?.add(CountryName("Select Country",R.drawable.ic_flag_1))
        countryItems?.add(CountryName("USA",R.drawable.ic_united_states_of_america))
        countryItems?.add(CountryName("Canada",R.drawable.ic_canada))
        countryItems?.add(CountryName("New Zealand",R.drawable.ic_new_zealand))
        countryItems?.add(CountryName("UK",R.drawable.ic_united_kingdom))
        countryItems?.add(CountryName("Europe",R.drawable.ic_european_union))
        countryItems?.add(CountryName("India",R.drawable.ic_india))
        countryItems?.add(CountryName("South America",R.drawable.ic_south_africa))
        countryItems?.add(CountryName("Australia",R.drawable.ic_australia))
        countryItems?.add(CountryName("Africa",R.drawable.ic_africa))
        countryItems?.add(CountryName("Other",R.drawable.ic_flag_1))
    }
}

data class CountryName(val name: String, val flagImage: Int)
