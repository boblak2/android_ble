package si.blazdev.bluetoothtest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVER_BT = 2;

    private TextView tvStatus;
    private TextView tvPaired;

    private BluetoothAdapter bluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        tvPaired = findViewById(R.id.tvPaired);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null){
            tvStatus.setText("BT is not availabel");
        }else {
            tvStatus.setText("BT is awailable");
        }

    }

    public void didPressbtn(View view) {

        switch (view.getId()){

            case R.id.btnOn:

                if (!bluetoothAdapter.isEnabled()){
                    showToast("Turning on BT...");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }else {
                    showToast("BT is already on");
                }
                break;

            case R.id.btnOff:

                if (bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.disable();
                    showToast("Turnign BT off");
                }else {
                    showToast("BT is already off");
                }
                break;

            case R.id.btnDiscover:

                if (!bluetoothAdapter.isDiscovering()){
                    showToast("Making your device discoverable");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                }
                break;

            case R.id.btnPaired:

                if (bluetoothAdapter.isEnabled()){
                    tvPaired.setText("Paired devices");
                    Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                    for (BluetoothDevice d : devices){
                        tvPaired.append("\n" + d.getName() +", "+d.getUuids() +", "+ d);
                        //for (ParcelUuid el : d.getUuids()){
                        //    Log.e(TAG, el.getUuid().toString());
                        //}
                    }
                    findBT();
                }else {
                    showToast("Turn BT on to get paired devices");
                }
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode){

            case REQUEST_ENABLE_BT:

                if (resultCode == RESULT_OK){
                    showToast("BT is on");
                }else {
                    showToast("Couldn't on BT");
                }
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }




    void findBT()
    {

        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                Log.e(TAG, "Povezan sem na napravo:" + device.getName());
                mmDevice = device;
                break;
            }
        }

        try {
            openBT();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        tvStatus.setText("Bluetooth Opened");
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "UTF-8");
                                    readBufferPosition = 0;
                                    Log.e(TAG, data);

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            tvStatus.setText(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData() throws IOException
    {
        /*
        String msg = myTextbox.getText().toString();
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        myLabel.setText("Data Sent");
        */
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        tvStatus.setText("Bluetooth Closed");
    }


}