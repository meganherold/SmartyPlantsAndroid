package com.msmcpherson.app;

/**
 * Created by Jessica's Awesome PC on 11/14/2015.
 */
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.IntentFilter;

public class Bluetooth extends Activity {
    private BluetoothAdapter btAdapter;
    //public Intent
    public void onCreate(){
        callbluetooth();
    }
    private void callbluetooth(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            //statusUpdate.setText("No Bluetooth on Device")
            //there is no bluetooth on the device
        }
        else if(!btAdapter.isEnabled()) {
            //if bluetooth is not on turn it on
            Intent  enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }

    }


}
