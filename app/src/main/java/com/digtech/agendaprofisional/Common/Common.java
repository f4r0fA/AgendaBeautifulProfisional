package com.digtech.agendaprofisional.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.digtech.agendaprofisional.Model.Cabeleleiro;
import com.digtech.agendaprofisional.Model.MyToken;
import com.digtech.agendaprofisional.Model.Saloes;
import com.digtech.agendaprofisional.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.paperdb.Paper;

public class Common {
    public static final Object DISABLE_TAG = "DISABLE";
    public static final int TIME_SLOT_TOTAL = 19 ;
    public static final String LOGGED_KEY = "LOGGED";
    public static final String STATE_KEY = "STATE";
    public static final String SALON_KEY = "SALON";
    public static final String CABELELEIRO_KEY = "CABELELEIRO";
    public static final String TITLE_KEY = "title";
    public static final String CONTENT_KEY = "content";
    public static String state_name="";
    public static Saloes selected_salon;
    public static Cabeleleiro currentCabeleleiro;
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
    public static Calendar bookingDate = Calendar.getInstance();
    public static Saloes currentSalom;

    public static String convertTimeSlotToString(int slot) {
        switch (slot) {
            case 0:
                return "9:00 - 9:30";
            case 1:
                return "9:30 - 10:00";
            case 2:
                return "10:00 - 10:30";
            case 3:
                return "10:30 - 11:00";
            case 4:
                return "11:00 - 11:30";
            case 5:
                return "13:00 - 13:30";
            case 6:
                return "13:30 - 14:00";
            case 7:
                return "14:00 - 14:30";
            case 8:
                return "14:30 - 15:00";
            case 9:
                return "15:00 - 15:30";
            case 10:
                return "15:30 - 16:00";
            case 11:
                return "16:00 - 16:30";
            case 12:
                return "16:30 - 17:00";
            case 13:
                return "17:00 - 17:30";
            case 14:
                return "17:30 - 18:00";
            case 15:
                return "18:00 - 18:30";
            case 16:
                return "18:30 - 19:00";
            case 17:
                return "19:00 - 19:30";
            case 18:
                return "19:30 - 20:00";
            default:
                return "INDISPONÍVEL";
        }

    }

    public static void showNotification(Context context, int notification_id, String title, String content, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null)
            pendingIntent = PendingIntent.getActivity(context,
                    notification_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = "digtech_desenvolvimento_01";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Agenda Beautiful Digtech",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Agenda PRO");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);

        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher));

        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

        notificationManager.notify(notification_id, notification);
    }

    public enum TOKEN_TYPE{
        CLIENTE,
        CABELELEIRO,
        MANAGER
    }

    public static void updateToken(Context context, String token) {
        Paper.init(context);
        String user = Paper.book().read(Common.LOGGED_KEY);
        if (user != null){
            if (!TextUtils.isEmpty(user)){
                MyToken myToken = new MyToken();
                myToken.setToken(token);
                myToken.setTokenType(TOKEN_TYPE.CABELELEIRO);
                myToken.setUser(user);

                FirebaseFirestore.getInstance()
                        .collection("Tokens")
                        .document(user)
                        .set(myToken)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
            }
        }
    }
}
