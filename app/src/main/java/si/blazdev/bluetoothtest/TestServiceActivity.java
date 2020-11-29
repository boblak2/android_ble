package si.blazdev.bluetoothtest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class TestServiceActivity extends AppCompatActivity {

    private static final String TAG = TestServiceActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_service);
    }


    public void didPressBtnService(View view){

        switch (view.getId()){

            case R.id.btnStartService:

                Log.e(TAG, "Start service");
                Intent intentStart = new Intent(getBaseContext(), MyService.class);
                //startService(intentStart);
                ContextCompat.startForegroundService(this, intentStart);
                break;

            case R.id.btnStopService:

                Log.e(TAG, "Stop service");
                Intent intentStop = new Intent(getBaseContext(), MyService.class);
                stopService(intentStop);
                break;

        }

    }

}
