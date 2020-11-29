package si.blazdev.bluetoothtest;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANEL_ID = "MyServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChanel();
    }

    private void createNotificationChanel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANEL_ID, "Test Service Channel", NotificationManager.IMPORTANCE_LOW);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }
}
