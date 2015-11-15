package com.msmcpherson.app;

/**
 * Created by Jessica's Awesome PC on 11/14/2015.
 */
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.IntentFilter;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Bluetooth extends Activity {
    private BluetoothAdapter btAdapter;
    protected static final int DISCOVERY_REQUEST = 1;
    private BluetoothServerSocket mmServerSocket;
    private BluetoothDevice remoteDevice;
    BroadcastReceiver bluetoothState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            callbluetooth();//turn bluetooth on
        }
    }

    //public Intent

   /* public void onCreate() {
        callbluetooth();
        //connectdevices();
    }*/
    private void callbluetooth(){
        //////////
        //Part 1//
        //////////
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            //("No Bluetooth on Device")
            //there is no bluetooth on the device
        }
        else if(!btAdapter.isEnabled()) {
            //if bluetooth is not on turn it on
            String ScanModeChanged = BluetoothAdapter.ACTION_SCAN_MODE_CHANGED;
            String beDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
            IntentFilter filter = new IntentFilter(ScanModeChanged);
            registerReceiver(bluetoothState,filter);
            startActivityForResult(new Intent(beDiscoverable),DISCOVERY_REQUEST);//look for other devices when on

            //Intent  enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableBtIntent, 0);
        }
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == DISCOVERY_REQUEST){
            //FIND BLUETOOTH DEVICES
            findDevices();
        }
    }
    private void findDevices(){
        String LastUsedDevice = getLastUsedRemoteBTDevice();
        if(LastUsedDevice != null){
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            for(BluetoothDevice pairedDevice :pairedDevices){
                if(pairedDevice.getAddress().equals(LastUsedDevice)){
                    remoteDevice = pairedDevice;
                }
            }
        }
    }


    ///////
    private String getLastUsedRemoteBTDevice(){
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String RESULT = prefs.getString("LAST_REMOTE_DEVICE_ADDRESS", null);
        return RESULT;
    }
    
    //for finding unknown devices
    /*BroadcastReceiver discoveryResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }*/
    //next connecting to the plant.
    //first must pair to plant
    //if not paired need to find
    //bluetooth admin permission to find other devices
    //action request discoverable
    //to find start discovery
    //listen for action found (means found anothe bluetooth device)
    //scan modes
    //scan_mode_connectable for previously bonded
    //get bonded devices
    //UUID is hardcodded into application


}
