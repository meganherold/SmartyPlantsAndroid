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
import java.util.logging.Handler;

public class Bluetooth extends Activity {
    private BluetoothAdapter btAdapter;
    protected static final int DISCOVERY_REQUEST = 1;
    private BluetoothServerSocket mmServerSocket;
    private BluetoothDevice remoteDevice;
    private final UUID MY_UUID = UUID.fromString("94F39D29-7D6D-437D-973B-FBA39E49D4EE");
    public ConnectThread connectThread;
    BroadcastReceiver bluetoothState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            callbluetooth();//turn bluetooth on
        }
    };

    //public Intent

    /* public void onCreate() {
         callbluetooth();
         //connectdevices();
     }*/
    public void callbluetooth(){
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
            IntentFilter filter = new IntentFilter(ScanModeChanged); //trying to create a new activity
            //registerReceiver(bluetoothState,filter);
            startActivityForResult(new Intent(beDiscoverable),DISCOVERY_REQUEST);//look for other devices when on

            //Intent  enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableBtIntent, 0);
        }
        else{
            //bluetooth is already enabled on the device
            //right now, we'll assume the devices are already paired
            Set<BluetoothDevice> bonded = btAdapter.getBondedDevices();
            //ConnectThread connectThread = new ConnectThread((BluetoothDevice) bonded.toArray()[0], stream!!);
            connectThread = new ConnectThread((BluetoothDevice) bonded.toArray()[0]);
            //connectThread.run();
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

    class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
            try {
                mmSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(String message) {
            // Cancel discovery because it will slow down the connection
            btAdapter.cancelDiscovery();

            //Do work to manage the connection (in a separate thread)
                manageConnectedSocket(mmSocket, message);
                //mmSocket.close();


        }


        public void manageConnectedSocket(BluetoothSocket mmSocket, String message) {
            //initiate the thread for transferring data
            ConnectedThread connectedThread = new ConnectedThread(mmSocket);

            byte bytes[] = new byte[]{0000, 0001, 0010, 0011};

            //String message = "Hello Ricky!";
            connectedThread.write(message.getBytes());
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


     class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
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

        Handler mHandler;
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    //MessageService.getInstance().obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                          //  .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}
