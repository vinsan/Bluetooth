package com.example.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Context ct;
    BluetoothAdapter mBluetoothAdapter;
    BroadcastReceiver mReceiver;
    String LOG_TAG = "BLUETOOTH_TEST";
    ArrayList<BluetoothDevice> devices;
    MyBroadcastReceiver br;
    private String [] permissions = {"android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for(int i=0; i<permissions.length; i++){
            if(ActivityCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, permissions, 200);
        }

        ct = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devices = new ArrayList<BluetoothDevice>();

        Button btOn = findViewById(R.id.on);
        btOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mBluetoothAdapter.isEnabled())
                    mBluetoothAdapter.enable();
            }
        });

        Button btOff = findViewById(R.id.off);
        btOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothAdapter.isEnabled()) {
                    if(mBluetoothAdapter.isDiscovering()){
                        if(mReceiver!=null){
                            unregisterReceiver(mReceiver);
                            mReceiver = null;
                        }
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    mBluetoothAdapter.disable();
                }
            }
        });

        Button scan = findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mBluetoothAdapter.isEnabled())
                    return; //se BT spento esce
                if(mBluetoothAdapter.isDiscovering())
                    mBluetoothAdapter.cancelDiscovery();    //cancella precedenti discovery

                if(mReceiver==null){    //se il ricevitore è null lo istanzia
                    mReceiver = new BroadcastReceiver() {
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();
                            //Finding devices
                            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                                // Get the BluetoothDevice object from the Intent
                                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                // Add the name and address to an array adapter to show in a ListView
                                // mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                                Log.d(LOG_TAG, device.getName() + ": "+ device.getAddress());
                                if(!devices.contains(device))
                                    devices.add(device);
                            }
                        }
                    };
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mReceiver, filter);
                }
                mBluetoothAdapter.startDiscovery();
                Log.d(LOG_TAG, "startDiscovery();");
            }
        });

        Button dev1 = findViewById(R.id.dev1);
        dev1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(devices.size()<1)
                    return;
                if(mBluetoothAdapter.isDiscovering()){
                    mBluetoothAdapter.cancelDiscovery();
                    if(mReceiver!=null){
                        unregisterReceiver(mReceiver);
                        mReceiver = null;
                    }
                }
                BluetoothDevice d = devices.get(0);
                dev1.setText(d.getName());
                boolean bond = d.createBond();
                if(bond){
                    br = new MyBroadcastReceiver(d, ct);
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                    registerReceiver(br, filter);
                }else{
                    Log.e(LOG_TAG, d.getName()+" bond failed!");
                }
            }
        });
        Button dev2 = findViewById(R.id.dev2);
        dev2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(devices.size()<2)
                    return;
                if(mBluetoothAdapter.isDiscovering()){
                    mBluetoothAdapter.cancelDiscovery();
                    if(mReceiver!=null){
                        unregisterReceiver(mReceiver);
                        mReceiver = null;
                    }
                }
                BluetoothDevice d = devices.get(1);
                dev2.setText(d.getName());
                boolean bond = d.createBond();
                if(bond){
                    br = new MyBroadcastReceiver(d, ct);
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                    registerReceiver(br, filter);
                }else{
                    Log.e(LOG_TAG, d.getName()+" bond failed!");
                }
            }
        });

    }

    protected void onDestroy(){
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy()");
        if(br!=null){
            br.closeSocket();
            unregisterReceiver(br);
        }
        if(mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }



}