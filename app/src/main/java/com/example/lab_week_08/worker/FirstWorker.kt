package com.example.lab_week_08.worker

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class FirstWorker(
    context: Context, workerParams: WorkerParameters
): Worker(context, workerParams) { // [cite: 67-69]

    // Fungsi ini mengeksekusi proses yang telah ditentukan [cite: 70]
    override fun doWork(): Result { // [cite: 72]
        // Mendapatkan parameter input [cite: 73]
        val id = inputData.getString(INPUT_DATA_ID) // [cite: 74]

        // Mensimulasikan proses berat dengan tidur selama 3 detik [cite: 76, 92]
        Thread.sleep(3000L) // [cite: 76]

        // Membangun output berdasarkan hasil proses [cite: 77]
        val outputData = Data.Builder() // [cite: 78]
            .putString(OUTPUT_DATA_ID, id) // [cite: 79]
            .build() // [cite: 80]

        // Mengembalikan output [cite: 81]
        return Result.success(outputData) // [cite: 82]
    }

    companion object { // [cite: 86]
        const val INPUT_DATA_ID = "inId" // [cite: 87]
        const val OUTPUT_DATA_ID = "outId" // [cite: 88]
    }
}