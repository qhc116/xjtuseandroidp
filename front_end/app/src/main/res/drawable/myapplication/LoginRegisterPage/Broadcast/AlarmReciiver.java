package com.example.myapplication.LoginRegisterPage.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

class AlarmReciiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context, Myservice.class);
        Log.d("触发","我被触发了10s");
        context.startService(i);

    }
}
