package com.mediaremote.vlcontroller.model;

import android.net.Uri;

/**
 * Created by nikita on 27/06/15.
 */
public class VlcServer {
    private static final int DEFAULT_PORT = 8080;

    private long id;
    private String serverName;
    private String password;
    private Uri uri;

    public VlcServer(String serverName, String password, String ipAddress, int port) {
        this.serverName = serverName;
        this.password = password;
        this.uri = Uri.parse("http://" + ipAddress + ":" + String.valueOf(port));
    }

    public VlcServer(String serverName, String ipAddress) {
        this(serverName, "fake_password", ipAddress, DEFAULT_PORT);
    }

    public VlcServer() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getIpAndPort() {
        return uri.getHost() + ":" + uri.getPort();
    }

    @Override
    public String toString() {
        return serverName + "\n"
                + getIpAndPort();
    }
}
