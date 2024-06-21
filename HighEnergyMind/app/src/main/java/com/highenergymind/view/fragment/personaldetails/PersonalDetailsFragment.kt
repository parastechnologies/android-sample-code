package com.highenergymind.view.fragment.personaldetails

import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseFragment
import com.highenergymind.databinding.FragmentPersonalDetailsBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.view.activity.signUpProcess.SignUpProcessActivity
import com.highenergymind.view.activity.signUpProcess.SignUpProcessViewModel
import com.highenergymind.view.sheet.selectage.SelectionBottomSheet


class PersonalDetailsFragment : BaseFragment<FragmentPersonalDetailsBinding>() {
    var gender: String? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_personal_details
    }

    override fun initViewWithData() {
        requireActivity().fullScreenStatusBar()
        setGenderView()
        onClick()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as SignUpProcessActivity).apply {
            setProgressMeter(com.intuit.sdp.R.dimen._64sdp)
        }
    }

    private fun onClick() {
        mBinding.apply {
            contiguousBtn.setOnClickListener {

                if (validator.personalValidation(this@PersonalDetailsFragment)) {
                    (activityViewModels<SignUpProcessViewModel>().value).apply {
                        map[ApiConstant.DEVICE_TYPE] = AppConstant.ANDROID
                        map[ApiConstant.NAME] = etFirstName.text?.trim()?.toString()!!
                        map[ApiConstant.GENDER] = gender!!
                        yourAgeTV.text?.trim()?.let { age ->
                            val ageArray = if (age.contains("+")) {
                                age.split("+")
                            } else {
                                age.split("-")
                            }
                            map[ApiConstant.MIN] = ageArray[0]
                            map[ApiConstant.MAX] = ageArray[1].ifEmpty { "+" }
                        }
                    }
                    navigateToCategoryScreen()

                }


            }


            dropUpTV.setOnClickListener {
                openAgeSheet()
            }

            yourAgeTV.setOnClickListener {
                openAgeSheet()
            }
        }

    }

    private fun navigateToCategoryScreen() {
        view?.let { view ->
            Navigation.findNavController(view)
                .navigate(R.id.action_personal_to_empoweringAffirmation)
        }
    }

    private fun openAgeSheet() {
        val dataList = ArrayList<String>()
        dataList.add(("6-17"))
        dataList.add(("18-29"))
        dataList.add(("30-49"))
        dataList.add(("50-59"))
        dataList.add(("70+"))
        val selectAgeBottom = SelectionBottomSheet(dataList)
        selectAgeBottom.callBack = { age ->
            mBinding.yourAgeTV.setText(age)
        }

        selectAgeBottom.show(
            childFragmentManager,
            "ModalBottomSheetDialog.TAG"
        )

    }

    private fun setGenderView() {
        when (gender) {
            AppConstant.GENDER.MALE.value -> {
                setmalebackground()

            }

            AppConstant.GENDER.FEMALE.value -> {
                setfemaleIVbackground(mBinding.femaleIV)
            }

            AppConstant.GENDER.OTHERS.value -> {
                setthirdGenderbackground()

            }

            AppConstant.GENDER.NO_MALE_NOR_FEMALE.value -> {
                setcancelbackground()

            }
        }
        mBinding.femaleIV.setOnClickListener {
            gender = AppConstant.GENDER.FEMALE.value
            setfemaleIVbackground(mBinding.femaleIV)
            hideKeyboard(it)
        }
        mBinding.maleIV.setOnClickListener {
            gender = AppConstant.GENDER.MALE.value
            setmalebackground()
            hideKeyboard(it)

        }
        mBinding.thirdGenderIV.setOnClickListener {
            gender = AppConstant.GENDER.OTHERS.value
            setthirdGenderbackground()
            hideKeyboard(it)

        }
        mBinding.cancelIV.setOnClickListener {
            gender = AppConstant.GENDER.NO_MALE_NOR_FEMALE.value
            setcancelbackground()
            hideKeyboard(it)

        }
    }

    private fun setthirdGenderbackground() {

        mBinding.femaleIV.setBackgroundResource(R.drawable.edt_text_back)
        mBinding.femaleIV.setColorFilter(mBinding.femaleIV.context.resources.getColor(R.color.gender_img_color))

        // female
        mBinding.maleIV.setBackgroundResource(R.drawable.edt_text_back)
        mBinding.maleIV.setColorFilter(mBinding.maleIV.context.resources.getColor(R.color.gender_img_color))

        // third
        mBinding.thirdGenderIV.setBackgroundResource(R.drawable.drawable_checked_gradient_content)
        mBinding.thirdGenderIV.setColorFilter(mBinding.thirdGenderIV.context.resources.getColor(R.color.bg_color_1))

        /// cancel
        mBinding.cancelIV.setBackgroundResource(R.drawable.edt_text_back)
        mBinding.cancelIV.setColorFilter(mBinding.thirdGenderIV.context.resources.getColor(R.color.gender_img_color))

    }

    private fun setmalebackground() {

        mBinding.femaleIV.setBackgroundResource(R.drawable.edt_text_back)
        mBinding.femaleIV.setColorFilter(mBinding.femaleIV.context.resources.getColor(R.color.gender_img_color))

        // female
        mBinding.maleIV.setBackgroundResource(R.drawable.drawable_checked_gradient_content)
        mBinding.maleIV.setColorFilter(mBinding.maleIV.context.resources.getColor(R.color.bg_color_1))

        // third
        mBinding.thirdGenderIV.setBackgroundResource(R.drawable.edt_text_back)
        mBinding.thirdGenderIV.setColorFilter(mBinding.thirdGenderIV.context.resources.getColor(R.color.gender_img_color))

        /// cancel
        mBinding.cancelIV.setBackgroundResource(R.drawable.edt_text_back)
        mBinding.cancelIV.setColorFilter(mBinding.thirdGenderIV.context.resources.getColor(R.color.gender_img_color))

    }

    private fun setcancelbackground() {

        mBinding.femaleIV.setBackgroundResource(R.drawable.edt_text_back)
        mBinding.femaleIV.setColorFilter(mBinding.femaleIV.context.resources.getColor(R.color.gender_img_color))

        // female
        mBinding.maleIV.setBackgroundResource(R.drawable.edt_text_back)
        mBinding.maleIV.setColorFilter(mBinding.maleIV.context.resources.getColor(R.color.gender_img_color))

        // third
        mBinding.thirdGenderIV.setBackgroundResource(R.drawable.edt_text_back)
        mBinding.thirdGenderIV.setColorFilter(mBinding.thirdGenderIV.context.resources.getColor(R.color.gender_img_color))

        /// cancel
        mBinding.cancelIV.setBackgroundResource(R.drawable.drawable_checked_gradient_content)
        mBinding.cancelIV.setColorFilter(mBinding.thirdGenderIV.context.resources.getColor(R.color.bg_color_1))

    }

    private fun setfemaleIVbackground(femaleIV: ImageView) {
        // male
        femaleIV.setBackgroundResource(R.drawable.drawable_checked_gradient_content)
        femaleIV.setColorFilter(femaleIV.context.resources.getColor(R.color.bg_color_1))

        // female
        mBinding.maleIV.setBackgroundResource(R.drawable.edt_text_back)
        mBinding.maleIV.setColorFilter(mBinding.maleIV.context.resources.getColor(R.color.gender_img_color))

        // third
        mBinding.thirdGenderIV.setBackgroundResource(R.drawable.edt_text_back)
        mBinding.thirdGenderIV.setColorFilter(mBinding.thirdGenderIV.context.resources.getColor(R.color.gender_img_color))

        /// cancel
        mBinding.cancelIV.setBackgroundResource(R.drawable.edt_text_back)
        mBinding.cancelIV.setColorFilter(mBinding.thirdGenderIV.context.resources.getColor(R.color.gender_img_color))

    }


}