package com.example.helpme;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import network.NetworkStatusChecker;
import network.NetworkStatusHandler;

public class NoWifiConnectionActivity extends AppCompatActivity implements NetworkStatusHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_wifi_connection);

        setTitle(R.string.no_internet);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    @Override
    public void checkConnection() {
        NetworkStatusChecker.getInstance().handleConnection(getApplicationContext(), new NetworkStatusChecker.ConnectionCallback() {
            @Override
            public void callback(boolean isConnected) {
                handleConnection(isConnected);
            }
        });
    }

    @Override
    public void handleConnection(boolean isConnected) {
        if (isConnected) {
            finish();
        }
    }
}