package com.ski.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context mContext, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent mIntent = new Intent(mContext, YourService.class);
            mIntent.putExtra("maxCountValue", 1000);
            YourService.enqueueWork(mContext, mIntent);
        }
    }
}