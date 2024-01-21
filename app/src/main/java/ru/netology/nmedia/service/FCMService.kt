package ru.netology.nmedia.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import kotlin.random.Random


class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    override fun onMessageReceived(message: RemoteMessage) {
        message.data[action]?.let {
            val actionEnum = try {
                Action.valueOf(it)
            } catch (e: IllegalArgumentException) {
                getSharedPreferences("IllegalArgumentException", Context.MODE_PRIVATE)
                    .edit()
                    .putString("IllegalArgumentException", e.toString())
                    .apply()
                return
            }
            when (actionEnum) {
                Action.NEWPOST -> handleNewPost(gson.fromJson(message.data[content], NewPostNotify::class.java))

            }
        }
    }




    override fun onNewToken(token: String) {
        getSharedPreferences("my_token", Context.MODE_PRIVATE)
            .edit()
            .putString("token", token)
            .apply()
    }

    private fun handleNewPost(content: NewPostNotify) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_new_post,
                    content.userName,
                    content.postAuthor,
                    content.postContent
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(content.postContent))
            .build()

        notify(notification)
    }

    private fun notify(notification: Notification) {
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }
}

enum class Action {
    NEWPOST,
}

data class NewPostNotify(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
    val contentId: Long,
    val postContent: String,
)