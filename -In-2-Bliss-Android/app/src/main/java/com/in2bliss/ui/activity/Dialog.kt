package com.in2bliss.ui.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.databinding.CongratulationsDialogBinding
import com.in2bliss.databinding.DeleteAccountPopupBinding
import com.in2bliss.databinding.DeleteHistoryPopupBinding
import com.in2bliss.databinding.FragmentMediaPickerSheetBinding
import com.in2bliss.databinding.ItemErrorDialogBinding
import com.in2bliss.databinding.ItemRecordingDialogBinding
import com.in2bliss.databinding.LayoutAffirmationBottomSheetEditBinding
import com.in2bliss.databinding.ProfileDetailPopupBinding
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.visibility

/**
 * Affirmation edit bottom sheet
 * @param context
 * @param fav
 * @param edit
 * @param share
 * @param delete
 * */
fun editAffirmationBottomSheet(
    context: Context,
    fav: () -> Unit,
    edit: () -> Unit,
    share: () -> Unit,
    delete: () -> Unit,
    isDelete: Boolean = true,
    isEdit: Boolean = true,
    isFav: Boolean = true,
    isShare: Boolean = true,
    favStatus: Boolean = false
) {
    val bottomSheet = BottomSheetDialog(context)
    val view = LayoutAffirmationBottomSheetEditBinding.inflate(LayoutInflater.from(context))
    bottomSheet.setContentView(view.root)

    view.clDelete.visibility(isDelete)
    view.clEdit.visibility(isEdit)
    view.clFav.visibility(isFav)
    view.clShare.visibility(isShare)

    view.tvFavourite.setText(
        if (favStatus) R.string.un_favourite else R.string.favourite
    )

    view.clFav.setOnClickListener {
        fav.invoke()
        bottomSheet.dismiss()
    }
    view.clEdit.setOnClickListener {
        edit.invoke()
        bottomSheet.dismiss()

    }
    view.clShare.setOnClickListener {
        share.invoke()
        bottomSheet.dismiss()
    }
    view.clDelete.setOnClickListener {
        delete.invoke()
        bottomSheet.dismiss()
    }
    bottomSheet.show()
}

/**
 * Delete account
 * @param activity
 * */
fun deleteAccount(
    activity: Activity,
    delete: () -> Unit
) {
    val dialog = Dialog(activity)
    val view = DeleteAccountPopupBinding.inflate(LayoutInflater.from(activity))
    dialog.setContentView(view.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val width = (activity.resources.displayMetrics.widthPixels / 1.1).toInt()
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    dialog.window?.setLayout(width, height)

    view.btnCancel.setOnClickListener {
        dialog.dismiss()
    }
    view.btnDelete.setOnClickListener {
        delete.invoke()
        dialog.dismiss()
    }
    dialog.show()
}

/**
 * Delete account
 * @param activity
 * */
fun deleteHistory(
    activity: Activity,
    delete: () -> Unit
) {
    val dialog = Dialog(activity)
    val view = DeleteHistoryPopupBinding.inflate(LayoutInflater.from(activity))
    dialog.setContentView(view.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val width = (activity.resources.displayMetrics.widthPixels / 1.1).toInt()
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    dialog.window?.setLayout(width, height)

    view.btnCancel.setOnClickListener {
        dialog.dismiss()
    }
    view.btnDelete.setOnClickListener {
        delete.invoke()
        dialog.dismiss()
    }
    dialog.show()
}


/**
 * Profile details
 * @param activity
 * */
fun profileDetails(
    activity: Activity,
    name: String,
    email: String,
    image: String,
    requestManager: RequestManager
) {
    val dialog = Dialog(activity)
    val view = ProfileDetailPopupBinding.inflate(LayoutInflater.from(activity))
    dialog.setContentView(view.root)
    val width = (activity.resources.displayMetrics.widthPixels / 1.1).toInt()
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    dialog.window?.setLayout(width, height)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    view.ivProfile.glide(
        requestManager = requestManager,
        image = BuildConfig.PROFILE_BASE_URL.plus(image),
        placeholder = R.drawable.ic_user_placholder,
        error = R.drawable.ic_user_placholder
    )
    view.tvName.text = name
    view.tvEmail.text = email
    view.ivClose.setOnClickListener {
        dialog.dismiss()
    }
    dialog.show()
}

/**
 * Streak complete dialog box
 * @param streakCount
 * @param activity
 * */
fun streakCompleteDialog(
    streakCount: Int,
    activity: Activity
) {

    val dialog = Dialog(activity)
    val view = CongratulationsDialogBinding.inflate(LayoutInflater.from(activity))
    dialog.setContentView(view.root)
    val width = activity.resources.displayMetrics.widthPixels
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    dialog.window?.setLayout(width, height)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    val title = "${
        (activity.getString(R.string.congratulations_on_7_days_of_meditation)).replace(
            "7",
            streakCount.toString()
        )
    } \uD83C\uDF1F\uD83C\uDF89"
    view.btnStartMeditation.setOnClickListener {
        dialog.dismiss()
    }
    view.tvTitle.text = title
    view.tvStreak.text = streakCount.toString()
    val description =
        "${activity.getString(R.string.you_are_making_remarkable_progress_on_your_mindfulness_journey__Keep_up_the_great_work)} ✨"
    view.tvDescription.text = description

    dialog.show()
}

/**
 * Error dialog box
 * @param message shown in dialog box
 * @param retry retry call back
 * */
fun Activity.alertDialogBox(
    message: String,
    retry: () -> Unit
) {
    val dialog = Dialog(this)
    val view = ItemErrorDialogBinding.inflate(LayoutInflater.from(this))
    dialog.setContentView(view.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val width = (this.resources.displayMetrics.widthPixels / 1.1).toInt()
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    dialog.window?.setLayout(width, height)

    view.tvTitle.text = message
    view.btnCancel.setOnClickListener {
        dialog.dismiss()
    }

    view.btnRetry.setOnClickListener {
        retry.invoke()
        dialog.dismiss()
    }
    dialog.show()
}

/**
 * Image picker bottom sheet
 * @param context
 * @param select selected type callback
 * */
fun imagePicker(
    context: Context,
    select: (selected: Int) -> Unit,
    title: String? = null,
    text1: String? = null,
    text2: String? = null
) {
    val bottomSheet = BottomSheetDialog(context)
    val view = FragmentMediaPickerSheetBinding.inflate(LayoutInflater.from(context))
    bottomSheet.setContentView(view.root)
    bottomSheet.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    val width = (context.resources.displayMetrics.widthPixels / 1.1).toInt()
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    bottomSheet.window?.setLayout(width, height)

    if (title != null) view.tvHeading.text = title
    if (text1 != null) view.tvCamera.text = text1
    if (text2 != null) view.tvGallery.text = text2


    view.tvCamera.setOnClickListener {
        select(0)
        bottomSheet.dismiss()
    }

    view.tvGallery.setOnClickListener {
        select(1)
        bottomSheet.dismiss()
    }
    bottomSheet.show()
}


/**
 * Error dialog box
 * @param message shown in dialog box
 * */
fun Activity.messageDialogBox(
    message: String,
    cancelOutSide : Boolean = true,
    okay: () -> Unit
) {
    val dialog = Dialog(this)
    val view = ItemRecordingDialogBinding.inflate(LayoutInflater.from(this))
    dialog.setContentView(view.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val width = (this.resources.displayMetrics.widthPixels / 1.1).toInt()
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    dialog.window?.setLayout(width, height)
    dialog.setCanceledOnTouchOutside(cancelOutSide)

    view.tvTitle.text = message
    view.btnOkay.setOnClickListener {
        okay.invoke()
        dialog.dismiss()
    }
    dialog.show()
}