package com.example.lab_week_08.worker

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class SecondWorker(
    context: Context, workerParams: WorkerParameters
): Worker(context, workerParams) { //

    override fun doWork(): Result {
        // Mendapatkan parameter input
        val id = inputData.getString(INPUT_DATA_ID) // [cite: 74]

        // Mensimulasikan proses berat dengan tidur selama 3 detik
        Thread.sleep(3000L) // [cite: 76]

        // Membangun output berdasarkan hasil proses
        val outputData = Data.Builder() // [cite: 78]
            .putString(OUTPUT_DATA_ID, id) // [cite: 79]
            .build() // [cite: 80]

        // Mengembalikan output
        return Result.success(outputData) // [cite: 82]
    }

    companion object { // [cite: 86]
        // Kunci input/output bisa sama atau berbeda, di sini kita samakan
        const val INPUT_DATA_ID = "inId" // [cite: 87]
        const val OUTPUT_DATA_ID = "outId" // [cite: 88]
    }
}