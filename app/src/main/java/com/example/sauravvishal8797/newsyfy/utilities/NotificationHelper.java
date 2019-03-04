package com.example.sauravvishal8797.newsyfy.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.webkit.WebView;

import com.example.sauravvishal8797.newsyfy.NewsActivity;
import com.example.sauravvishal8797.newsyfy.R;

public class NotificationHelper {

    private static final String PRIMARY_CHANNEL_ID = "trending_news_notification_channel";

    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotificationManager;
    private Context mContext;

    public NotificationHelper(Context context){
        mContext = context;
    }

    public void createNotificationChannel(){
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "News Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Trending News Notification");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(String notificationTitle, String notificationtext,
                                                              String newsUrl, String sourceName){
        Intent notificationIntent = new Intent(mContext, NewsActivity.class);
        notificationIntent.putExtra("url", newsUrl);
        notificationIntent.putExtra("source", sourceName);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(mContext, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(notificationTitle)
                .setContentText(notificationtext)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        return notifyBuilder;
    }

    public void sendNotification(String notificationTitle, String notificationtext, String newsUrl, String sourceName){
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
        notificationManagerCompat.notify(NOTIFICATION_ID, getNotificationBuilder(notificationTitle,
                notificationtext, newsUrl, sourceName).build());
    }
}
