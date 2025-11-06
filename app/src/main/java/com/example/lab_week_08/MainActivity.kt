package com.example.lab_week_08

// Tambahkan import ini
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
// ---
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.lab_week_08.worker.FirstWorker
import com.example.lab_week_08.worker.SecondWorker

class MainActivity : AppCompatActivity() {

    private val workManager by lazy {
        WorkManager.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- Tambahkan kode ini ---
        // Meminta izin notifikasi untuk API 33 (TIRAMISU) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // [cite: 544]
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != // [cite: 545]
                PackageManager.PERMISSION_GRANTED) { // [cite: 546]
                // Meminta izin [cite: 547]
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1) // [cite: 547]
            }
        }
        // --- Akhir kode tambahan ---

        val networkConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val id = "001"

        val firstRequest = OneTimeWorkRequest
            .Builder(FirstWorker::class.java)
            .setConstraints(networkConstraints)
            .setInputData(getIdInputData(FirstWorker.INPUT_DATA_ID, id))
            .build()

        val secondRequest = OneTimeWorkRequest
            .Builder(SecondWorker::class.java)
            .setConstraints(networkConstraints)
            .setInputData(getIdInputData(SecondWorker.INPUT_DATA_ID, id))
            .build()

        workManager.beginWith(firstRequest)
            .then(secondRequest)
            .enqueue()

        workManager.getWorkInfoByIdLiveData(firstRequest.id)
            .observe(this) { info ->
                if (info.state.isFinished) {
                    showResult("First process is done")
                }
            }

        workManager.getWorkInfoByIdLiveData(secondRequest.id) // [cite: 572]
            .observe(this) { info -> // [cite: 573]
                if (info.state.isFinished) { // [cite: 574]
                    showResult("Second process is done") // [cite: 575]
                    // --- Tambahkan baris ini ---
                    launchNotificationService() // Memanggil service saat worker kedua selesai [cite: 576]
                    // --- Akhir baris tambahan ---
                }
            }
    }

    private fun getIdInputData(idKey: String, idValue: String) =
        Data.Builder()
            .putString(idKey, idValue)
            .build()

    private fun showResult(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // --- Tambahkan fungsi baru ini ---
    // Fungsi untuk meluncurkan NotificationService
    private fun launchNotificationService() { // [cite: 552]
        // Mengamati status penyelesaian dari service [cite: 553]
        NotificationService.trackingCompletion.observe( // [cite: 554]
            this) { id -> // [cite: 556]
            // Menampilkan toast saat service selesai [cite: 557]
            showResult("Process for Notification Channel ID $id is done!") // [cite: 557]
        }

        // Membuat Intent untuk memulai service [cite: 558]
        val serviceIntent = Intent(this, NotificationService::class.java).apply { // [cite: 560]
            putExtra(EXTRA_ID, "001") // [cite: 561]
        }
        // Memulai foreground service [cite: 563]
        ContextCompat.startForegroundService(this, serviceIntent) // [cite: 564]
    }
    // --- Akhir fungsi baru ---

    // --- Tambahkan companion object ini ---
    companion object { // [cite: 566]
        const val EXTRA_ID = "Id" // [cite: 569]
    }
    // --- Akhir companion object ---
}