package com.bn.trabalho_03;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private View buttonStart;
    private View buttonStop;

    private BindedService bindedService;
    private boolean mBound = false;

    private TextView downloadStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission()) {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {
            // Code for Below 23 API Oriented Device
            // Do next code
        }

        downloadStatus = findViewById(R.id.download_status);
        Button btnDownload = findViewById(R.id.buttonDownload);
        btnDownload.setOnClickListener(onDownloadListener());

        buttonStart = findViewById(R.id.button_start);
        buttonStop = findViewById(R.id.button_stop);

        buttonStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startPlayer(view);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                stopPlayer(view);
            }
        });

        Intent i = new Intent(this, InternetNotificationService.class);
        startService(i);
        InternetNotificationService.mainActivity = this;

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    public void startPlayer(View view){
        Intent intent = new Intent(this, MediaPlayerService.class);
        startService(intent);
    }

    public void stopPlayer(View view){
        stopService(new Intent(this, MediaPlayerService.class));
    }

    private View.OnClickListener onDownloadListener() {
        return new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DownloadService.class);
                intent.putExtra(DownloadService.FILENAME, "flowers.png");
                intent.putExtra(DownloadService.URL, "https://cdn.iconscout.com/icon/premium/png-256-thumb/flowers-47-835021.png");
                startService(intent);
                downloadStatus.setText("Fazendo download...");
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(DownloadService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(DownloadService.RESULT);
                if (resultCode == RESULT_OK) {
                    Toast.makeText(MainActivity.this, "Download concluído!", Toast.LENGTH_LONG).show();
                    downloadStatus.setText("Download completo!");
                } else {
                    Toast.makeText(MainActivity.this, "Erro no Download", Toast.LENGTH_LONG).show();
                    downloadStatus.setText("Falha no download!");
                }
            }
        }
    };


    public void notification(){

        //NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
        //        .setTicker("teste")
        //       .setSmallIcon(R.drawable.ic_notifications_black_24dp)
        //        .setContentTitle("Notificação da Bruninha")
        //         .setContentText("Bruninha => Notificação de conexão com a Internet");

        //Notification notification = mBuilder.build();

        //mNotificationManager.notify(R.drawable.ic_notifications_black_24dp, notification);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Notificação da Bruninha")
                .setContentText("Bruninha => Notificação de conexão com a Internet")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());

    }

    public void onClickInicializarBindedService( View view ){
        Intent intent = new Intent(this, BindedService.class);
        bindService( intent, mConnection, Context.BIND_AUTO_CREATE );
        startService(intent);

        TextView textView = findViewById(R.id.textView);
        textView.setText("");
    }

    public void onClickFinalizarBindedService( View view ){
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

    }

    public void onClickChamarBindedService( View view ){
        if (mBound) {
            int num = bindedService.getChange();

            TextView textView = findViewById(R.id.textView);
            String txt = textView.getText().toString();
            textView.setText( txt + "Resultado: " + num + "\n " );
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        //Implementar os métodos para quando o serviço estiver conectado
        public void onServiceConnected( ComponentName className,
                                        IBinder service) {

            //Instancia de AndroidBindedService.LocalBinder
            BindedService.LocalBinder binder = (BindedService.LocalBinder) service;
            bindedService = binder.getService();
            mBound = true;
        }

        //Implementar os métodos para quando o serviço estiver desconectado
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
