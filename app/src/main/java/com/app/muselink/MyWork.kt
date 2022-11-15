package com.app.muselink

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import androidx.work.Worker

class MyWork(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("SAdASDADADASD", "Working in BackGround")
        return Result.success()
    }
}
