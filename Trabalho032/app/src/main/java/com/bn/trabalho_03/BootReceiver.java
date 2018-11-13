package com.bn.trabalho_03;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){

            String value = "eeeeeeeeeeeeeentrou aqui";
            Log.d("nome", "value is: " + value);

            context.startService(new Intent(context, BindedService.class));

            Toast toast = Toast.makeText(context, "Broadcast inizializando service", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
