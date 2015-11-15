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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Bluetooth extends Activity {
    private BluetoothAdapter btAdapter;
    protected static final int DISCOVERY_REQUEST = 0;
    private BluetoothServerSocket mmServerSocket;
    private BluetoothDevice remoteDevice;
   // private BluetoothServerSocket socket2ya;
   private InputStream mmInStream;
    private OutputStream mmOutStream;

    //public Intent

    public void onCreate() {
        callbluetooth();
        //connectdevices();
    }
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
            Intent  enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }
        //////////
        //Part 2//
        //////////
        String LastUsedDevice = getLastUsedRemoteBTDevice();
        String ScanModeChanged = BluetoothAdapter.ACTION_SCAN_MODE_CHANGED;
        String beDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
        String myUUID = "1ffaf3f1-f7b2-4490-adbb-fbf9992acd5b";
        //BluetoothSocket socket = null;

        BluetoothServerSocket tmp = null;
        try {
            tmp = btAdapter.listenUsingRfcommWithServiceRecord(btAdapter.getName(), UUID.fromString(myUUID));
        }catch (IOException e){}

        mmServerSocket = tmp;

        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();//this is plant (or pi)
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                manageConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    public void manageConnectedSocket(BluetoothSocket socket) {
        BluetoothSocket mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    /*public connectdevices() {
        String LastUsedDevice = getLastUsedRemoteBTDevice();
        String ScanModeChanged = BluetoothAdapter.ACTION_SCAN_MODE_CHANGED;
        String beDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
        String myUUID = "1ffaf3f1-f7b2-4490-adbb-fbf9992acd5b";

        BluetoothServerSocket tmp = null;
        try {
            tmp = btAdapter.listenUsingRfcommWithServiceRecord(btAdapter.getName(), UUID.fromString(myUUID));
        }catch (IOException e){}

        mmServerSocket = tmp;

        /*if(LastUsedDevice != null){
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            for(BluetoothDevice pairedDevice : pairedDevices ){
                remoteDevice = pairedDevice;
            }
        }
        IntentFilter filter = new IntentFilter(ScanModeChanged);
        registerReceiver(bluetoothState, filter);
        startActivityForResult(new Intent(beDiscoverable),DISCOVERY_REQUEST);
        //TO FIND DEVICES IS ABOVE
        //set phone as server:
        BluetoothServerSocket tmp = null;
        tmp = btAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
    }*/
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
