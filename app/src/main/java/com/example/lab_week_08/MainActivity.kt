package com.example.lab_week_08

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.lab_week_08.worker.FirstWorker
import com.example.lab_week_08.worker.SecondWorker

class MainActivity : AppCompatActivity() { // [cite: 99]

    // Membuat instance dari WorkManager [cite: 100]
    private val workManager by lazy { // [cite: 102]
        WorkManager.getInstance(this) // [cite: 103]
    }

    override fun onCreate(savedInstanceState: Bundle?) { // [cite: 104]
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // [cite: 106]
        setContentView(R.layout.activity_main) // [cite: 107]
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets -> // [cite: 108]
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) // [cite: 110]
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom) // [cite: 111]
            insets // [cite: 112]
        }

        // Membuat batasan (Constraints) agar worker hanya berjalan jika ada koneksi internet [cite: 114-116]
        val networkConstraints = Constraints.Builder() // [cite: 117]
            .setRequiredNetworkType(NetworkType.CONNECTED) // [cite: 118]
            .build()

        val id = "001" // [cite: 123]

        // Membuat request untuk FirstWorker [cite: 130]
        val firstRequest = OneTimeWorkRequest // [cite: 131]
            .Builder(FirstWorker::class.java) // [cite: 132]
            .setConstraints(networkConstraints)
            .setInputData(getIdInputData(FirstWorker.INPUT_DATA_ID, id)) // [cite: 133]
            .build() // [cite: 134]

        // Membuat request untuk SecondWorker [cite: 135]
        val secondRequest = OneTimeWorkRequest // [cite: 136]
            .Builder(SecondWorker::class.java) // [cite: 137]
            .setConstraints(networkConstraints) // [cite: 138]
            .setInputData(getIdInputData(SecondWorker.INPUT_DATA_ID, id)) // [cite: 139-140]
            .build() // [cite: 141]

        // Mengatur urutan proses: FirstWorker, lalu SecondWorker [cite: 142-143]
        workManager.beginWith(firstRequest) // [cite: 144]
            .then(secondRequest) // [cite: 145]
            .enqueue() // [cite: 146]

        // Mengamati (observe) status dari firstRequest [cite: 154]
        workManager.getWorkInfoByIdLiveData(firstRequest.id) // [cite: 156]
            .observe(this) { info -> // [cite: 157]
                if (info.state.isFinished) { // [cite: 160]
                    showResult("First process is done") // [cite: 163]
                }
            }

        // Mengamati (observe) status dari secondRequest [cite: 148]
        workManager.getWorkInfoByIdLiveData(secondRequest.id) // [cite: 164]
            .observe(this) { info -> // [cite: 165]
                if (info.state.isFinished) { // [cite: 166]
                    showResult("Second process is done") // [cite: 167]
                }
            }
    }

    // Fungsi helper untuk membangun data input [cite: 173]
    private fun getIdInputData(idKey: String, idValue: String) = // [cite: 175]
        Data.Builder() // [cite: 176]
            .putString(idKey, idValue) // [cite: 177]
            .build() // [cite: 178]

    // Fungsi helper untuk menampilkan Toast [cite: 179]
    private fun showResult(message: String) { // [cite: 180]
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show() // [cite: 181]
    }
}