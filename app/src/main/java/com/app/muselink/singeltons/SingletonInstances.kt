package com.app.muselink.singeltons

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.widgets.uploadwaveform.customAudioViews.SoundFile

object SingletonInstances {
    var bottomSheetDialogFragment : BottomSheetDialogFragment? = null
    var soundFile : SoundFile? = null
    var currentAudioFilePlay : ModalAudioFile? = null
    var currentAudioFilePlayPos : Int? = 0
    fun setBottomSheetDialogInstance(bottomSheetDialogFragment : BottomSheetDialogFragment?){
        this.bottomSheetDialogFragment = bottomSheetDialogFragment
    }
    fun setRecordedSoundFile(soundFile: SoundFile?){
        this.soundFile = soundFile
    }
    var listAudioFiles : ArrayList<ModalAudioFile>? = null
    fun setUploadAudioFiles(listAudioFiles : ArrayList<ModalAudioFile>?){
        this.listAudioFiles = listAudioFiles
    }
}