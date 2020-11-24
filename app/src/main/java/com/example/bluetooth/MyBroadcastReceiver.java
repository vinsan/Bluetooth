package com.example.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.util.Log;
import java.io.IOException;
import java.util.UUID;

public class MyBroadcastReceiver extends BroadcastReceiver {
    String LOG_TAG = "BLUETOOTH_TEST";
    BluetoothDevice d;
    Context ct;
    BroadcastReceiver br;
    BluetoothSocket btSocket = null;

    public MyBroadcastReceiver(BluetoothDevice d, Context ct){
        this.d = d;
        this.ct = ct;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
            Log.d(LOG_TAG, d.getName()+" bond success!");
            //OPERAZIONI PER CONNESSIONE
            d.fetchUuidsWithSdp();
            final String PBAP_UUID = "0000112f-0000-1000-8000-00805f9b34fb";
            Log.d(LOG_TAG, d.getName() + ": " + PBAP_UUID + ": " + AllGattCharacteristics.lookup(PBAP_UUID));
            try {
                btSocket = d.createInsecureRfcommSocketToServiceRecord(ParcelUuid.fromString(PBAP_UUID).getUuid());
            } catch (IOException e) {
                Log.d(LOG_TAG, "IOException: " + e.getMessage());
            }
            try {
                btSocket.connect();
                Log.d(LOG_TAG, "Connessione stabilita!");
            } catch (IOException e) {
                Log.d(LOG_TAG, "Connect. IOException: " + e.getMessage());
            }
        }
    }

    public void closeSocket(){
        if(btSocket!=null){
            try {
                btSocket.close();
            } catch (IOException e) {
                Log.d(LOG_TAG, "Close. IOException: "+e.getMessage());
            }
        }

    }

}
