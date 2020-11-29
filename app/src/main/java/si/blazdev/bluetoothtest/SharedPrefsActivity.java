package si.blazdev.bluetoothtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SharedPrefsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = SharedPrefsActivity.class.getSimpleName();

    private static final String PREFFS = "NAME";
    private static final String KEY_ADD = "keyAdd";

    private TextView tvValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_prefs);

        tvValue = findViewById(R.id.tvValue);

        SharedPreferences preferences = getSharedPreferences(PREFFS, Context.MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(this);

    }

    public void didPressBtnSP(View view){

        switch (view.getId()){

            case R.id.btnAdd:
                add();
                break;

        }

    }

    private void add(){
        int currentVal = getVal();
        currentVal++;
        SharedPreferences preferences = getSharedPreferences(PREFFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_ADD, currentVal);
        editor.commit();

        tvValue.setText(""+ currentVal);
    }

    private int getVal(){
        SharedPreferences preferences = getSharedPreferences(PREFFS, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_ADD, 0);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Log.e(TAG, "key:"+key);

        int currenVal = sharedPreferences.getInt(KEY_ADD, -1);
        Log.e(TAG, "currentVal:" + currenVal);

    }
}
