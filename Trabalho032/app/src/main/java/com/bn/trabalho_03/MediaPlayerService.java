package com.bn.trabalho_03;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class MediaPlayerService extends Service {

    private MediaPlayer myPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        myPlayer = MediaPlayer.create(this, R.raw.som);
        myPlayer.setLooping(true);
        myPlayer.setVolume(100, 100);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int StartId){
        myPlayer.start();
        return 1;
    }

    @Override
    public void onDestroy(){
        myPlayer.stop();
        myPlayer.release();
    }

    @Override
    public void onLowMemory(){

    }
}
