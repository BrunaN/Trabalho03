package com.bn.trabalho_03;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BindedService extends Service {

    private static final String TAG = "BindedService";

    private final IBinder mBinder = new LocalBinder();

    private int change = 1;

    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();
        CharSequence text = TAG + " - Inicializando service";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText( context, text, duration );
        toast.show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        InternetThread internetThread = new InternetThread();
        internetThread.start();
        change++;

        return super.onStartCommand(intent, flags, startId);
    }

    public class LocalBinder extends Binder {
        public BindedService getService() {
            return BindedService.this;
        }
    }

    public int getChange() {
        return change;
    }

}
