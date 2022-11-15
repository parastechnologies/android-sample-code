package com.example.commonresources

object SingeltonInstancesAudio {


    var listAudioFiles : ArrayList<ModalAudioFile>? = null

    fun setUploadeAudioFiles(listAudioFiles : ArrayList<ModalAudioFile>?){
        this.listAudioFiles = listAudioFiles
    }

}