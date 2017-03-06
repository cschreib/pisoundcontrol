package com.cschreib.pisoundcontrol;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

public class PiApplication extends Application {
    Status currentStatus;
    Client client;

    String serverIP;

    String serverName;
    String serverVersion;
    int    serverPort;

    ConnectActivity connectActivity;

    public enum Status {
        DISCONNECTED, CONNECTING, CONNECTED
    }

    public void registerActivity(ConnectActivity activity) {
        connectActivity = activity;
        if (connectActivity != null) {
            connectActivity.setStatus(currentStatus);
        }
    }

    private void setStatus(Status status) {
        currentStatus = status;

        if (connectActivity != null) {
            connectActivity.setStatus(currentStatus);

            if (status == Status.CONNECTED) {
                Intent intent = new Intent(this, PickSourceActivity.class);
                startActivity(intent);
            }
        }
    }

    public Status getStatus() {
        return currentStatus;
    }

    public String getServerIP() { return serverIP; }

    public String getServerName() {
        return serverName;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public int getServerPort() {
        return serverPort;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        client = new Client();
        client.start();

        currentStatus = Status.DISCONNECTED;
    }

    private class ConnectTask extends AsyncTask<String, Void, Void> {
        PiApplication app;
        String message;

        ConnectTask(PiApplication a) {
            message = null;
            app = a;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            app.setStatus(PiApplication.Status.CONNECTING);
        }

        @Override
        protected Void doInBackground(String... params) {
            String serverIP = params[0];
            int    serverPort = Integer.parseInt(params[1]);

            try {
                // Connecting
                client.connect(5000, serverIP, serverPort);
                message = null;
            } catch (IOException ex) {
                // Connection failed
                message = ex.getLocalizedMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if (client.isConnected()) {
                app.setStatus(PiApplication.Status.CONNECTED);
            } else {
                app.setStatus(PiApplication.Status.DISCONNECTED);
                if (message != null) {
                    Toast toast = Toast.makeText(
                        getApplicationContext(), message, Toast.LENGTH_LONG
                    );
                    toast.show();
                }
            }
        }
    }

    public void connectToServer(String ip, int port) {
        serverIP = ip;
        serverPort = port;

        new ConnectTask(this).execute(ip, Integer.toString(port));
    }
}
