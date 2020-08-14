package com.ski.myapplication;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;
import android.app.Notification;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Date;
import java.util.Random;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

public class YourService extends JobIntentService {
    public static final String ACTION = "com.ski.myapplication.MyTestService";

    private static final String NOTIFICATION_CHANNEL_ID = "stings";
    final Handler mHandler = new Handler();

    public static final int JOB_ID = 1;
    private static final String TAG = "MyJobIntentService";
    /**
     * Unique job ID for this service.
     */
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, YourService.class, JOB_ID, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showToast("Running on background");
    }
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        /**
         * Write code here.. Perform Long operation here such as Download/Upload of file, Sync Some data
         * The system or framework is already holding a wake lock for us at this point
         */
        int maxCount = intent.getIntExtra("maxCountValue", -1);
        /**
         * Suppose we want to print 1 to 1000 number with one-second interval, Each task will take time 1 sec, So here now sleeping thread for one second.
         */
        for (int i = 0; i < maxCount; i++) {
            Log.d(TAG, "onHandleWork: The number is: " + i);
            sendMessage();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        // Fetch data passed into the intent on start
//        String val = intent.getStringExtra("foo");
//        // Construct an Intent tying it to the ACTION (arbitrary event namespace)
//        Intent in = new Intent(ACTION);
//        // Put extras into the intent as usual
//        in.putExtra("resultCode", MainActivity.RESULT_OK);
//        in.putExtra("resultValue", "My Result Value. Passed in: " + val);
//        // Fire the broadcast with intent packaged
//        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
//        // or sendBroadcast(in) for a normal broadcast;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        showToast("Job Execution Finished");
    }
    // Helper for showing tests
    void showToast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(YourService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private static String getRandomString(final int sizeOfRandomString)
    {
        final String ALLOWED_CHARACTERS ="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
    private void sendMessage(){

        NotificationManager notificationManager = (NotificationManager)       getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

//        String NOTIFICATION_CHANNEL_ID = getRandomString(4);
        String NOTIFICATION_CHANNEL_ID = "1";
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0,
                notificationIntent, 0);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//           NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
//            // Configure the notification channel.
//            notificationChannel.setDescription("Sample Channel description");
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
//            notificationChannel.enableVibration(true);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        int randnum = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS  | Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Transform X")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("New Attendees")
                .setContentText("There is 1 request awaiting for your approval")
                .setContentInfo("Information")
                .setContentIntent(pendingIntent);
        notificationManager.notify(randnum, notificationBuilder.build());
    }
}