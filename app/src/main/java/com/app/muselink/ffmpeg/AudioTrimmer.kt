package com.app.muselink.ffmpeg

import android.content.Context
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFmpeg.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.FFmpeg.RETURN_CODE_SUCCESS
import java.io.File
import java.io.IOException

class AudioTrimmer private constructor(private val context: Context) {

    private var audio: File? = null
    private var callback: FFMpegCallback? = null

    private var startTime = "00:00:00"
    private var endTime = "00:00:00"
    private var outputPath = ""
    private var outputFileName = ""

    fun setFile(originalFiles: File): AudioTrimmer {
        this.audio = originalFiles
        return this
    }

    fun setCallback(callback: FFMpegCallback): AudioTrimmer {
        this.callback = callback
        return this
    }

    fun setStartTime(startTime: String): AudioTrimmer {
        this.startTime = startTime
        return this
    }

    fun setEndTime(endTime: String): AudioTrimmer {
        this.endTime = endTime
        return this
    }

    fun setOutputPath(output: String): AudioTrimmer {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): AudioTrimmer {
        this.outputFileName = output
        return this
    }

    fun trim() {

        if (audio == null || !audio!!.exists()) {
            callback!!.onFailure(IOException("File not exists"))
            return
        }
        if (!audio!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }

        val outputLocation = getConvertedFile(outputPath, outputFileName)
        val cmd = arrayOf("-i", audio!!.path, "-ss", startTime, "-to", endTime, "-c", "copy", outputLocation.path)
        val command = FFmpeg.execute(cmd)
        if (command == RETURN_CODE_SUCCESS) {
            callback!!.onSuccess(outputLocation, OutputType.TYPE_AUDIO)
            callback!!.onFinish()
        } else if (command == RETURN_CODE_CANCEL) {
            callback!!.onFailure(IOException("Error"))
        } else {

        }

    }

    fun getConvertedFile(folder: String, fileName: String): File {
        val f = File(folder)

        if (!f.exists())
            f.mkdirs()

        return File(f.path + File.separator + fileName)
    }

    companion object {

        val TAG = "AudioTrimmer"

        fun with(context: Context): AudioTrimmer {
            return AudioTrimmer(context)
        }
    }
}
