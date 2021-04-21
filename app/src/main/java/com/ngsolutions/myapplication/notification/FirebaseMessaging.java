package com.ngsolutions.myapplication.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ngsolutions.myapplication.HomePage;
import com.ngsolutions.myapplication.IntroductoryActivity;
import com.ngsolutions.myapplication.R;
import com.ngsolutions.myapplication.ReplyActivity;

import java.util.Locale;

public class FirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        loadLocale();
        SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
        String savedCurrentUser = sp.getString("Current_USERID","None");

        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser != null && sent.equals(fUser.getUid()))
        {
            if(!savedCurrentUser.equals(user))
            {
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                    sendOAndAboveNotification(remoteMessage);
                }
                else
                {
                    sendNormalNotification(remoteMessage);
                }
            }
        }
    }

    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        //String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String cropCode = remoteMessage.getData().get("cropCode");
        String originalMessage = remoteMessage.getData().get("originalMessage");
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, ReplyActivity.class);
        intent.putExtra("cropCode", cropCode);
        intent.putExtra("chatTitle", this.getString(R.string.your));
        intent.putExtra("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        intent.putExtra("message", originalMessage);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
        Notification.Builder builder = notification1.getONotifications(getString(R.string.querry_answered), body,pIntent, defSoundUri, icon);

        int j = 0;
        if (i > 0) {
            j = i;
        }
        notification1.getManager().notify(j, builder.build());
    }
    private void sendNormalNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        //String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String cropCode = remoteMessage.getData().get("cropCode");
        String originalMessage = remoteMessage.getData().get("originalMessage");
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent = new Intent(this, ReplyActivity.class);
        intent.putExtra("cropCode",cropCode);
        intent.putExtra("chatTitle",this.getString(R.string.your));
        intent.putExtra("userID",FirebaseAuth.getInstance().getCurrentUser().getUid());
        intent.putExtra("message",originalMessage);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(getString(R.string.querry_answered))
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j = 0;
        if(i>0){
            j=i;
        }
        notificationManager.notify(j,builder.build());
    }
    private void setLocale(String lang)
    {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_lang",lang);
        editor.apply();
    }
    private void loadLocale()
    {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_lang","en");
        setLocale(lang);
    }
}
