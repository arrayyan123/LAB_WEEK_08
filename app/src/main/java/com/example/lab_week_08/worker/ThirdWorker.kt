package com.example.lab_week_08.worker

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

// Sesuai instruksi: "Add 1 more Worker named ThirdWorker"
class ThirdWorker(
    context: Context, workerParams: WorkerParameters
): Worker(context, workerParams) {

    override fun doWork(): Result {
        val id = inputData.getString(INPUT_DATA_ID)

        // Kita ubah waktu tidurnya agar berbeda, misal 2 detik
        Thread.sleep(2000L)

        val outputData = Data.Builder()
            .putString(OUTPUT_DATA_ID, id)
            .build()

        return Result.success(outputData)
    }

    companion object {
        const val INPUT_DATA_ID = "inId_3" // Gunakan key unik untuk praktik terbaik
        const val OUTPUT_DATA_ID = "outId_3"
    }
}