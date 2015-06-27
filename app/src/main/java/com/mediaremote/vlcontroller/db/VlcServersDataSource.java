package com.mediaremote.vlcontroller.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.mediaremote.vlcontroller.model.VlcServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 27/06/15.
 */
public class VlcServersDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = { DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_SERVER_NAME,
            DatabaseHelper.COLUMN_PASSWORD, DatabaseHelper.COLUMN_URI };

    public VlcServersDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void saveVlcServer(VlcServer vlcServer) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SERVER_NAME, vlcServer.getServerName());
        values.put(DatabaseHelper.COLUMN_PASSWORD, vlcServer.getPassword());
        values.put(DatabaseHelper.COLUMN_URI, vlcServer.getUri().toString());
        database.insert(DatabaseHelper.TABLE_NAME, null, values);
    }

    public void deleteVlcServer(VlcServer vlcServer) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + " = " + vlcServer.getId(), null);
    }

    public List<VlcServer> getAllVlcServers() {
        List<VlcServer> servers = new ArrayList<VlcServer>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            VlcServer server = cursorToServer(cursor);
            servers.add(server);
            cursor.moveToNext();
        }

        cursor.close();
        return servers;
    }

    private VlcServer cursorToServer(Cursor cursor) {
        VlcServer server = new VlcServer();
        server.setId(cursor.getLong(0));
        server.setServerName(cursor.getString(1));
        server.setPassword(cursor.getString(2));
        server.setUri(Uri.parse(cursor.getString(3)));
        return server;
    }
}
