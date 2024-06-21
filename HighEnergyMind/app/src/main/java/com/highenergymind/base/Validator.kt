package com.highenergymind.base

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.util.PatternsCompat
import com.highenergymind.R
import com.highenergymind.databinding.ActivityChangePasswordBinding
import com.highenergymind.databinding.ActivityContactUsBinding
import com.highenergymind.databinding.ActivityEditProfileBinding
import com.highenergymind.databinding.ActivityEmailVarificationBinding
import com.highenergymind.databinding.ActivityForgotPasswordBinding
import com.highenergymind.databinding.ActivityLoginBinding
import com.highenergymind.databinding.ActivityResetPasswordBinding
import com.highenergymind.databinding.ActivitySuggestAffirmationBinding
import com.highenergymind.databinding.FragmentEmailVerificationBinding
import com.highenergymind.databinding.FragmentSignUpBinding
import com.highenergymind.utils.showSnackBar
import com.highenergymind.view.fragment.personaldetails.PersonalDetailsFragment
import java.util.regex.Pattern
import javax.inject.Inject


class Validator @Inject constructor() {

    private fun isValidPasswordFormat(password: String?): Boolean {
        val pattern: Pattern
        val passwordPattern = "^(?=.*[A-Z])(?=\\S+$).{8,}$"
        pattern = Pattern.compile(passwordPattern)
        val matcher = password?.let { pattern.matcher(it) }
        return matcher!!.matches()
    }

    /** Valid email address validation*/
    fun String?.isValidEmail() =
        !isNullOrEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()

    /** is password valid function*/
    private fun isPasswordValid(
        password: String?,
        context: Context,
        isSignUp: Boolean = false,
        parentView: View? = null
    ): Boolean {
        if (password?.trim().isNullOrEmpty()) {
            showToast(
                context.getString(R.string.please_enter_your_password),
                context
            )
            return false
        }
        if (isSignUp) {
            if (!isValidPasswordFormat(password)) {

                parentView?.let {
                    context.getString(R.string.password_should_be_validation).showSnackBar(
                        it
                    )
                }

                return false
            }
        } else {
            if ((password?.trim()?.length ?: 0) < 8) {
                showToast(
                    context.getString(R.string.password_should_not_less_eight),
                    context
                )
                return false
            }
        }
        return true
    }

    fun personalValidation(fragment: PersonalDetailsFragment): Boolean {
        fragment.mBinding.apply {
            if (etFirstName.text?.trim()?.toString().isNullOrEmpty()) {
                showToast(
                    root.context.getString(R.string.please_enter_your_first_name),
                    root.context
                )
                etFirstName.requestFocus()
                return false
            }
            if (fragment.gender == null) {
                showToast(
                    root.context.getString(R.string.please_select_gender),
                    root.context
                )
                return false
            }
            if (yourAgeTV.text?.trim()?.toString().isNullOrEmpty()) {
                showToast(
                    root.context.getString(R.string.please_enter_your_age),
                    root.context
                )
                return false
            }

        }

        return true
    }


    /**
     * this function is used for input type validation used in signUp Screen
     * @param binding
     * */
    fun loginValidation(binding: ActivityLoginBinding): Boolean {
        binding.apply {
            val context: Context = root.context
            if (!etEmail.text?.trim()?.toString().isValidEmail()) {
                showToast(
                    root.context.getString(R.string.please_enter_valid_email_address),
                    context
                )
                etEmail.requestFocus()
                return false
            }
            if (!isPasswordValid(etPassword.text?.trim()?.toString(), context)) {
                etPassword.requestFocus()
                return false
            }
        }
        return true
    }

    fun signUpValidation(binding: FragmentSignUpBinding): Boolean {
        binding.apply {
            val context: Context = root.context
            if (!emailEdt.text?.trim()?.toString().isValidEmail()) {
                showToast(
                    root.context.getString(R.string.please_enter_valid_email_address),
                    context
                )
                emailEdt.requestFocus()
                return false
            }
            if (!isPasswordValid(
                    passwordEdt.text?.trim()?.toString(),
                    context,
                    isSignUp = true,
                    parentView = binding.root
                )
            ) {
                passwordEdt.requestFocus()
                return false
            }
            if (passwordEdtCon.text?.trim()?.toString() != passwordEdt.text?.trim()?.toString()) {
                showToast(root.context.getString(R.string.password_does_not_match), context)
                passwordEdtCon.requestFocus()
                return false
            }
        }
        return true
    }


    /**
     * this function user for show long toast
     * @param message
     * @param context
     * */
    private fun showToast(message: String, context: Context) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.BOTTOM, 0, 50)
        toast.show()
    }

    fun otpValidator(mBinding: FragmentEmailVerificationBinding): Boolean {

        mBinding.apply {
            val context = root.context
            if (et1.text?.trim().isNullOrEmpty() || et2.text?.trim()
                    .isNullOrEmpty() || et3.text?.trim().isNullOrEmpty() || et4.text?.trim()
                    .isNullOrEmpty()
            ) {
                showToast(context.getString(R.string.please_enter_correct_otp), context)
                return false
            }
        }
        return true
    }

    fun otpValidator(mBinding: ActivityEmailVarificationBinding): Boolean {

        mBinding.apply {
            val context = root.context
            if (et1.text?.trim().isNullOrEmpty() || et2.text?.trim()
                    .isNullOrEmpty() || et3.text?.trim().isNullOrEmpty() || et4.text?.trim()
                    .isNullOrEmpty()
            ) {
                showToast(context.getString(R.string.please_enter_correct_otp), context)
                return false
            }
        }
        return true
    }

    fun editProfileValidator(binding: ActivityEditProfileBinding): Boolean {
        binding.apply {
            val context = root.context
            if (etFirstName.text?.trim().isNullOrEmpty()) {
                showToast(context.getString(R.string.please_enter_your_first_name), context)
                etFirstName.requestFocus()
                return false
            }
        }
        return true
    }

    fun forgotValidation(binding: ActivityForgotPasswordBinding): Boolean {

        binding.apply {
            if (!emailEdt.text?.trim()?.toString().isValidEmail()) {
                showToast(
                    root.context.getString(R.string.please_enter_valid_email_address),
                    root.context
                )
                emailEdt.requestFocus()
                return false
            }
        }
        return true
    }

    fun resetPasValidator(binding: ActivityResetPasswordBinding): Boolean {
        binding.apply {
            if (!isPasswordValid(
                    etNewPas.text?.trim()?.toString(),
                    root.context,
                    true,
                    parentView = binding.root
                )
            ) {
                etNewPas.requestFocus()
                return false
            }
            if (etConfirmPas.text?.trim()?.toString() != etNewPas.text?.trim()?.toString()) {
                showToast(root.context.getString(R.string.password_does_not_match), root.context)
                etConfirmPas.requestFocus()
                return false
            }
        }
        return true
    }

    fun changePasswordValidator(binding: ActivityChangePasswordBinding): Boolean {
        binding.apply {
            if (etCurrentPas.text?.trim()?.toString().isNullOrEmpty()) {
                showToast(root.context.getString(R.string.please_enter_current_pas), root.context)
                etCurrentPas.requestFocus()
                return false
            }
            if (!isPasswordValid(
                    etNewPas.text?.trim()?.toString(),
                    root.context,
                    true,
                    parentView = binding.root
                )
            ) {
                etNewPas.requestFocus()
                return false
            }
            if (etConfirmPas.text?.trim()?.toString() != etNewPas.text?.trim()?.toString()) {
                showToast(root.context.getString(R.string.password_does_not_match), root.context)
                etConfirmPas.requestFocus()
                return false
            }
        }
        return true
    }

    fun contactUsValidator(binding: ActivityContactUsBinding): Boolean {

        binding.apply {
            if (etName.text?.trim()?.toString().isNullOrEmpty()) {
                showToast(root.context.getString(R.string.please_enter_your_name), root.context)
                etName.requestFocus()
                return false
            }
            if (!etEmail.text?.trim()?.toString().isValidEmail()) {
                showToast(
                    root.context.getString(R.string.please_enter_valid_email_address),
                    root.context
                )
                etEmail.requestFocus()
                return false
            }
            if (etMessage.text?.trim()?.toString().isNullOrEmpty()) {
                showToast(root.context.getString(R.string.please_enter_your_message), root.context)
                etMessage.requestFocus()
                return false
            }
        }
        return true
    }

    fun suggestUsValidator(binding: ActivitySuggestAffirmationBinding): Boolean {

        binding.apply {
            if (etName.text?.trim()?.toString().isNullOrEmpty()) {
                showToast(root.context.getString(R.string.please_enter_your_name), root.context)
                etName.requestFocus()
                return false
            }
            if (!etEmail.text?.trim()?.toString().isValidEmail()) {
                showToast(
                    root.context.getString(R.string.please_enter_valid_email_address),
                    root.context
                )
                etEmail.requestFocus()
                return false
            }
            if (etMessage.text?.trim()?.toString().isNullOrEmpty()) {
                showToast(root.context.getString(R.string.please_enter_your_suggestion), root.context)
                etMessage.requestFocus()
                return false
            }
        }
        return true
    }


}