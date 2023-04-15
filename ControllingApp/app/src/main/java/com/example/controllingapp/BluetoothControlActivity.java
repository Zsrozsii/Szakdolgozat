package com.example.controllingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class BluetoothControlActivity extends Activity {
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;

    private boolean mIsBluetoothConnected = false;

    private BluetoothDevice mDevice;

    final static char exitCode = 'E';
    final static String enableCode = "111";
    final static char forwardCode = 'F';
    final static char leftTurnCode = 'L';
    final static char stopCode = 'S';
    final static char rightTurnCode = 'R';
    final static char backwardCode = 'B';
    final static char followCode = 'A';
    final static char quitFollowCode = 'Q';

    private ProgressDialog progressDialog;
    ImageButton homeButton;
    Button followButton, stopFollowButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) { //A Bluetooth kapcsolódás, valamint adat küldés és fogadás működéséhez tartozó kommentek a 'LogActivity-ben találhatók'
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_control);

        ActivityHelper.initialize(this);
        homeButton = findViewById(R.id.homeButton);
        followButton = findViewById(R.id.follow);
        stopFollowButton = findViewById(R.id.stopFollow);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(exitCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mBTSocket.getOutputStream().write(followCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        stopFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mBTSocket.getOutputStream().write(quitFollowCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        createControlButtons();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void createControlButtons(){

        ImageButton forward = findViewById(R.id.forward);

        forward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        try {
                            mBTSocket.getOutputStream().write(forwardCode);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        try {
                            mBTSocket.getOutputStream().write(stopCode);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                }
                return false;
            }
        });

        ImageButton left = findViewById(R.id.left);
        ImageButton right = findViewById(R.id.right);

        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        try {
                            mBTSocket.getOutputStream().write(leftTurnCode);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        try {
                            mBTSocket.getOutputStream().write(stopCode);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                }
                return false;
            }
        });

        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        try {
                            mBTSocket.getOutputStream().write(rightTurnCode);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        try {
                            mBTSocket.getOutputStream().write(stopCode);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                }
                return false;
            }
        });

        ImageButton reverse = findViewById(R.id.reverse);

        reverse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        try {
                            mBTSocket.getOutputStream().write(backwardCode);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        try {
                            mBTSocket.getOutputStream().write(stopCode);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                }
                return false;
            }
        });

    }

    //Amikor elnavigálunk az adott képernyőről a Bluetooth kapcsolat automatikusan megszakításra kerül.
    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mBTSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            boolean mIsUserInitiatedDisconnect = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private void msg(String message) { //Toast üzenet gyors hívására szolgáló metódus
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(BluetoothControlActivity.this, "Kérlek várj", "Bluetooth csatlakozás folyamatban");// http://stackoverflow.com/a/11130220/1287554

        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                msg("A csatlakozás sikertelen. Ellenőrizd az eszközt melyhez csatlakozni szeretnél!");
                finish();
            } else {
                msg("Csatlakozva eszközhöz!");
                mIsBluetoothConnected = true;
                try {
                    mBTSocket.getOutputStream().write(enableCode.getBytes()); //Amint végbemegye a csatlakozás elküldjük a vezérlés aktiváló kódját a robotnak
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            progressDialog.dismiss();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
