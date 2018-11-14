package com.bn.trabalho_03;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {


        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            Log.d("nome", "ACTION_SCREEN_ON");
        }else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.d("nome", "ACTION_BOOT_COMPLETED");
        }else{
            return;
        }

        context.startService(new Intent(context, BindedService.class));
        Toast.makeText(context, "Broadcast inizializando service", Toast.LENGTH_LONG).show();

    }
}