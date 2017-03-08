package com.cschreib.pisoundcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class ConnectActivity extends AppCompatActivity {
    PiApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        app = (PiApplication) getApplication();
        app.registerActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.registerActivity((ConnectActivity) null);
    }

    public void setStatus(PiApplication.Status status) {
        EditText editText = (EditText) findViewById(R.id.editTextIP);
        Button buttonConnect = (Button) findViewById(R.id.buttonConnect);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarConnect);
        ImageView statusIcon = (ImageView) findViewById(R.id.imageViewConnectStatus);
        TextView statusText = (TextView) findViewById(R.id.textViewConnectStatus);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);

        switch (status) {
        case DISCONNECTED:
            editText.setActivated(true);
            buttonConnect.setActivated(true);
            buttonConnect.setText(getString(R.string.button_ip_connect));
            buttonConnect.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ConnectActivity.this.connect();
                }
            });
            progressBar.setVisibility(View.INVISIBLE);
            statusIcon.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.INVISIBLE);
            statusText.setText(getString(R.string.connection_status_disconnected));
            break;
        case CONNECTING:
            editText.setActivated(false);
            buttonConnect.setActivated(false);
            statusIcon.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.INVISIBLE);
            statusText.setText(getString(R.string.connection_status_connecting));
            break;
        case CONNECTED:
            TextView textViewStatusIP = (TextView) findViewById(R.id.textViewStatusIP);
            TextView textViewStatusHost = (TextView) findViewById(R.id.textViewStatusHost);
            TextView textViewStatusPort = (TextView) findViewById(R.id.textViewStatusPort);
            TextView textViewStatusVersion = (TextView) findViewById(R.id.textViewStatusVersion);

            editText.setActivated(true);
            buttonConnect.setActivated(true);
            buttonConnect.setText(getString(R.string.button_ip_disconnect));
            buttonConnect.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ConnectActivity.this.disconnect();
                }
            });
            progressBar.setVisibility(View.INVISIBLE);
            statusIcon.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            textViewStatusIP.setText(getString(R.string.connection_status_ip, app.getServerIP()));
            textViewStatusHost.setText(getString(R.string.connection_status_host, app.getServerName()));
            textViewStatusPort.setText(getString(R.string.connection_status_port, app.getServerPort()));
            textViewStatusVersion.setText(getString(R.string.connection_status_version, app.getServerVersion()));
            statusText.setText(getString(R.string.connection_status_connected));
            break;
        }
    }

    // Called when pressing the "ConnectActivity" button
    public void connect() {
        if (app.getStatus() == PiApplication.Status.CONNECTING) {
            return;
        }

        EditText editText = (EditText) findViewById(R.id.editTextIP);
        String host = editText.getText().toString();
        int port = 4444;

        boolean goodIP = true;
        try {
            // See if the user has provided the serverPort as part of the IP address
            if (host.indexOf(':') != -1) {
                String[] splitHost = host.split(":");
                host = splitHost[0];
                port = Integer.parseInt(splitHost[1]);
            }

            // Make sure the user has provided a valid IP address
            String[] splitHost = host.split("\\.");
            if (splitHost.length == 4) {
                for (String part : splitHost) {
                    int ipart = Integer.parseInt(part);
                    if (ipart < 0 || ipart > 255) {
                        goodIP = false;
                        break;
                    }
                }
            } else {
                goodIP = false;
            }
        } catch (NumberFormatException ex) {
            goodIP = false;
        }

        if (!goodIP) {
            // Message the user to let them know they made a mistake
            editText.setError(getString(R.string.edit_ip_wrong_format));
            return;
        }

        app.connectToServer(host, port);
    }

    public void disconnect() {
        app.disconnectFromServer();
    }
}
