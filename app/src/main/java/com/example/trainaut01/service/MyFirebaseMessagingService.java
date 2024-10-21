package com.example.trainaut01.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.trainaut01.R;
import com.example.trainaut01.models.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.UUID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        Message newMessage = new Message(UUID.randomUUID().toString(),title, body, System.currentTimeMillis(), false);
        Log.d("MESSAGE", newMessage.getContent());

        Intent intent = new Intent("NewMessage");
        intent.putExtra("message", newMessage);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        sendNotification(title, body);
    }
    private void sendNotification(String title, String messageBody) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "default_channel_id")
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Для Android 8.0 и выше, необходимо создать канал уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default_channel_id",
                    "Уведомления",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
