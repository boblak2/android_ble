package si.blazdev.bluetoothtest;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);


        Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();

        Intent notificationIntent = new Intent(this, TestServiceActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, App.CHANEL_ID)
                .setContentTitle("Naslov")
                .setContentText("Me gusta comer el burek")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pIntent)
                .build();

        //startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }
}
