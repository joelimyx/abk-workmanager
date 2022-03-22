package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import java.lang.IllegalArgumentException

private const val TAG = "BlueWorker"
class BlurWorker(ctx: Context, params:WorkerParameters): Worker(ctx, params) {
    override fun doWork(): Result {
        val applicationContext = applicationContext

        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Blurring Image", applicationContext)

        sleep()
        try{
            if (TextUtils.isEmpty(resourceUri)){
                Log.e(TAG, "Invalid Uri")
                throw IllegalArgumentException("Invalid Uri")
            }

            val resolver = applicationContext.contentResolver
            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(
                Uri.parse(resourceUri)
                ))

            val output = blurBitmap(picture, applicationContext)

            val file = writeBitmapToFile(applicationContext, output)
            val outputData = workDataOf(KEY_IMAGE_URI to file.toString())

            makeStatusNotification("Image Blurred successful", applicationContext)
            return Result.success(outputData)

        }catch (throwable: Throwable){
            Log.e(TAG, "Error in blurring")
            return Result.failure()
        }

    }
}