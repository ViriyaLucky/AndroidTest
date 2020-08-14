package com.ski.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Random;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity {
    public static final String MESSAGE_STATUS = "message_status";
    private static final String ALLOWED_CHARACTERS ="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NOTIFICATION_CHANNEL_ID = "channel";
    public MyTestReceiver receiverForTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button gen = (Button) findViewById(R.id.generateBarcode);
        final Button pin = (Button) findViewById(R.id.pin);
        Button scan = (Button) findViewById(R.id.scanner);
        final int min = 1111;
        final int max = 9999;
        final Button notif = (Button) findViewById(R.id.notif);
        final WorkManager mWorkManager = WorkManager.getInstance();
        final OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).build();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("NotifApps", "NotifyApps", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        getCurrentFirebaseToken();
        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
//                mWorkManager.enqueue(mRequest);

                onStartJobIntentService(view);
            }
        });
        mWorkManager.getWorkInfoByIdLiveData(mRequest.getId()).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(@Nullable WorkInfo workInfo) {
                if (workInfo != null) {
                    WorkInfo.State state = workInfo.getState();
                    ((TextView) findViewById(R.id.codegenerated)).setText(state.toString());
                }
            }
        });
        gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                QRCodeWriter writer = new QRCodeWriter();
                try {
                    //disable generate code
                    gen.setEnabled(false);

                    //generate random number
                    String random  = getRandomString(8);
                    //generate qr code
                    BitMatrix bitMatrix = writer.encode(String.valueOf(random), BarcodeFormat.QR_CODE, 512, 512);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }

//                    //get company logo
//                    Bitmap bMapScale  = BitmapFactory.decodeResource(getResources(), R.drawable.transformx);
//                    Bitmap bitmapToDrawInTheCenter = Bitmap.createScaledBitmap(bMapScale, bMapScale.getWidth()/12, bMapScale.getHeight()/12, true);
//
//                    Bitmap resultBitmap = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(), bmp.getConfig());
//
//                    //put company logo in qr code
//                    Canvas canvas = new Canvas(resultBitmap);
//                    canvas.drawBitmap(bmp, new Matrix(), null);
//                    canvas.drawBitmap(bitmapToDrawInTheCenter, (bmp.getWidth() - bitmapToDrawInTheCenter.getWidth()) / 2, (bmp.getHeight() - bitmapToDrawInTheCenter.getHeight()) / 2, new Paint());

                    ((ImageView) findViewById(R.id.img_result_qr)).setImageBitmap(bmp);
                    ((TextView) findViewById(R.id.codegenerated)).setText(String.valueOf(random));
                    gen.setEnabled(true);
                } catch (WriterException e) {
                    e.printStackTrace();
                    gen.setEnabled(true);
                }
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent aa = new Intent(getBaseContext(), Scanner.class);
                startActivity(aa);
            }
        });
//        notif.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                YourService.enqueueWork(getApplicationContext(), new Intent());
//
//            }
//        });
        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent aa = new Intent(getBaseContext(), pin.class);
                startActivity(aa);
            }
        });

    }
    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
    public void onStartJobIntentService(View view) {
//        Intent mIntent = new Intent(this, YourService.class);
        Intent mIntent = new Intent(this, MyFirebaseMessagingService.class);

        mIntent.putExtra("maxCountValue", 1000);
        YourService.enqueueWork(this, mIntent);
    }
    private void getCurrentFirebaseToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.e("currentToken", token);

                        // Log and toast
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}