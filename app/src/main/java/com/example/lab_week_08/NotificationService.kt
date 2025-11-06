package com.example.lab_week_08

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class NotificationService : Service() { // [cite: 300]

    private lateinit var notificationBuilder: NotificationCompat.Builder // [cite: 304]
    private lateinit var serviceHandler: Handler // [cite: 306]

    // Tidak digunakan untuk komunikasi satu arah, jadi return null [cite: 308-310]
    override fun onBind(intent: Intent): IBinder? = null // [cite: 310]

    override fun onCreate() { // [cite: 314]
        super.onCreate()
        // Membuat notifikasi [cite: 316]
        notificationBuilder = startForegroundService() // [cite: 318]

        // Membuat HandlerThread untuk menjalankan service di thread terpisah [cite: 321-325]
        val handlerThread = HandlerThread("SecondThread") // [cite: 333]
            .apply { start() } // [cite: 334]
        serviceHandler = Handler(handlerThread.looper) // [cite: 337]
    }

    // Callback yang dipanggil saat service dimulai [cite: 467]
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int { // [cite: 470]
        val returnValue = super.onStartCommand(intent, flags, startId) // [cite: 472]

        // Mendapatkan ID channel dari Intent [cite: 474]
        val id = intent?.getStringExtra(EXTRA_ID) // [cite: 475]
            ?: throw IllegalStateException("Channel ID must be provided") // [cite: 476]

        // Mem-posting tugas ke handler (thread terpisah) [cite: 477-478]
        serviceHandler.post { // [cite: 479]
            // Menjalankan hitung mundur di notifikasi [cite: 483]
            countDownFromTenToZero(notificationBuilder) // [cite: 484]
            // Memberi tahu MainActivity bahwa proses selesai [cite: 485]
            notifyCompletion(id) // [cite: 486]
            // Menghentikan foreground service (notifikasi hilang) [cite: 487-489]
            stopForeground(STOP_FOREGROUND_REMOVE) // [cite: 489]
            // Menghentikan dan menghancurkan service [cite: 490]
            stopSelf() // [cite: 490]
        }

        return returnValue // [cite: 493]
    }

    // Fungsi untuk hitung mundur [cite: 497]
    private fun countDownFromTenToZero(notificationBuilder: NotificationCompat.Builder) { // [cite: 499]
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager // [cite: 502]
        for (i in 10 downTo 0) { // [cite: 505]
            Thread.sleep(1000L) // [cite: 506]
            // Memperbarui teks notifikasi [cite: 507]
            notificationBuilder.setContentText("$i seconds until last warning") // [cite: 508]
                .setSilent(true) // [cite: 509]
            // Memberi tahu NotificationManager tentang pembaruan [cite: 510]
            notificationManager.notify( // [cite: 511]
                NOTIFICATION_ID, // [cite: 512]
                notificationBuilder.build() // [cite: 513]
            )
        }
    }

    // Memperbarui LiveData di Main Thread saat selesai [cite: 517-519]
    private fun notifyCompletion(Id: String) { // [cite: 520]
        Handler(Looper.getMainLooper()).post { // [cite: 521]
            mutableID.value = Id // [cite: 524]
        }
    }

    // Mempersiapkan dan memulai foreground service [cite: 338]
    private fun startForegroundService(): NotificationCompat.Builder { // [cite: 339]
        // Membuat PendingIntent untuk membuka MainActivity saat notifikasi diklik [cite: 341-345]
        val pendingIntent = getPendingIntent() // [cite: 346]
        // Membuat channel notifikasi [cite: 347]
        val channelId = createNotificationChannel() // [cite: 350]
        // Membangun notifikasi [cite: 351-353]
        val notificationBuilder = getNotificationBuilder( // [cite: 354]
            pendingIntent, channelId // [cite: 356]
        )
        // Memulai foreground service [cite: 358]
        startForeground(NOTIFICATION_ID, notificationBuilder.build()) // [cite: 360]
        return notificationBuilder // [cite: 361]
    }

    // Membuat PendingIntent [cite: 362]
    private fun getPendingIntent(): PendingIntent { // [cite: 364]
        // Mengecek versi SDK untuk flag PendingIntent [cite: 367-369]
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // [cite: 372]
            PendingIntent.FLAG_IMMUTABLE // [cite: 373]
        } else {
            0
        }
        // Mengarahkan ke MainActivity [cite: 374-376]
        return PendingIntent.getActivity( // [cite: 377]
            this, 0, Intent(
                this, // [cite: 380]
                MainActivity::class.java // [cite: 379]
            ), flag // [cite: 381]
        )
    }

    // Membuat Channel Notifikasi (wajib untuk API 26+) [cite: 386]
    private fun createNotificationChannel(): String { // [cite: 390]
        // Pengecekan versi SDK (Oreo) [cite: 391-393]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // [cite: 394]
            val channelId = "001" // [cite: 396]
            val channelName = "001 Channel" // [cite: 398]
            val channelPriority = NotificationManager.IMPORTANCE_DEFAULT // [cite: 407]
            val channel = NotificationChannel( // [cite: 410]
                channelId, // [cite: 411]
                channelName,
                channelPriority // [cite: 412]
            )
            val service = requireNotNull( // [cite: 416]
                ContextCompat.getSystemService(this, NotificationManager::class.java) // [cite: 418]
            )
            service.createNotificationChannel(channel) // [cite: 422]
            return channelId // [cite: 424]
        } else {
            return "" // [cite: 426]
        }
    }

    // Membangun notifikasi [cite: 429]
    private fun getNotificationBuilder(pendingIntent: PendingIntent, channelId: String) = // [cite: 429]
        NotificationCompat.Builder(this, channelId) // [cite: 430]
            .setContentTitle("Second worker process is done") // [cite: 432]
            .setContentText("Check it out!") // [cite: 434]
            .setSmallIcon(R.drawable.ic_launcher_foreground) // [cite: 436]
            .setContentIntent(pendingIntent) // [cite: 439]
            .setTicker("Second worker process is done, check it out!") // [cite: 440]
            .setOngoing(true) // Notifikasi tidak bisa di-dismiss oleh user [cite: 441-443]

    companion object { // [cite: 446]
        const val NOTIFICATION_ID = 0xCA7 // [cite: 447]
        const val EXTRA_ID = "Id" // [cite: 448]
        // LiveData untuk melacak status penyelesaian [cite: 449-453]
        private val mutableID = MutableLiveData<String>() // [cite: 458]
        val trackingCompletion: LiveData<String> = mutableID // [cite: 459]
    }
}