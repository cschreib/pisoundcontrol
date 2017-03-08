package com.cschreib.pisoundcontrol;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class PiApplication extends Application {
    private final WebSocketConnection connection = new WebSocketConnection();
    Status currentStatus;

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
                Intent intent = new Intent(connectActivity, PickSourceActivity.class);
                connectActivity.startActivity(intent);
            }
        }
    }

    public Status getStatus() {
        return currentStatus;
    }

    public String getServerIP() {
        return serverIP;
    }

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

        currentStatus = Status.DISCONNECTED;
    }

    public void connectToServer(String ip, int port) {
        serverIP = ip;
        serverPort = port;
        String uri = "ws://"+ip+":"+Integer.toString(port);

        try {
            connection.connect(uri, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    setStatus(PiApplication.Status.CONNECTED);
                }

                @Override
                public void onTextMessage(String payload) {
                    Toast toast = Toast.makeText(
                        getApplicationContext(), payload, Toast.LENGTH_LONG
                    );
                    toast.show();
                }

                @Override
                public void onClose(int code, String reason) {
                    setStatus(PiApplication.Status.DISCONNECTED);
                }
            });
        } catch (WebSocketException e) {
            setStatus(PiApplication.Status.DISCONNECTED);
            Toast toast = Toast.makeText(
                getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG
            );
            toast.show();
        }
    }

    public void disconnectFromServer() {
        connection.disconnect();
    }
}
