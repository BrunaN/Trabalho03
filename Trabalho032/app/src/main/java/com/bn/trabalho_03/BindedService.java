package com.bn.trabalho_03;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Timer;
import java.util.TimerTask;

public class BindedService extends Service {

    private static final String TAG = "BindedService";
    private static String lastResponse = "";
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
        final Handler handler = new Handler();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                RequestQueue queue = Volley.newRequestQueue(BindedService.this);
                String url ="http://dontpad.com/android03";

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("nome", "Sucesso na requisição");

                                if(response.equals(lastResponse)){
                                    Log.d("nome", "Não mudou");
                                }else if(!lastResponse.equals("")){
                                    Log.d("nome", "Mudou");
                                    change++;
                                    Toast.makeText(BindedService.this, "Informação monitorada mudou!", Toast.LENGTH_LONG).show();
                                    lastResponse = response;
                                }else{
                                    lastResponse = response;
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("nome", error.getMessage());
                                Log.d("nome", "Erro na requisição");
                            }
                        });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        }, 0, 1000);//5 Seconds

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
