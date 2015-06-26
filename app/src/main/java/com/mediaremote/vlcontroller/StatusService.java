package com.mediaremote.vlcontroller;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created by neon on 26.06.15.
 */
public class StatusService extends Service {
    private PendingIntent pendingIntent;
    private boolean isRunning = true;
    private RequestQueue requestQueue;
    private Status status = null;
    private String urlRequest = "";
    final String TAG = "myLogs";

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Bind...");
        return null;
    }

    @Override
    public void onCreate() {
        status = Status.getInstance();
        requestQueue = Volley.newRequestQueue(this);
        super.onCreate();
        Log.d(TAG, "Service created...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Log.d(TAG, "Service destroyed...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        urlRequest = intent.getStringExtra("URL");
        pendingIntent = intent.getParcelableExtra("PI");
        statusUpdate();
        return super.onStartCommand(intent, flags, startId);
    }

    public void statusUpdate() {
        final VlcRequest vlcRequest = new VlcRequest(Request.Method.GET, urlRequest, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    status.setState(response.getString("state"));
                    status.setVolume(response.getInt("volume"));
                    status.setTime(response.getInt("time"));
                    status.setLength(response.getInt("length"));
                    status.setArtist(response.getJSONObject("information").getJSONObject("category").getJSONObject("meta").getString("artist"));
                    status.setFilename(response.getJSONObject("information").getJSONObject("category").getJSONObject("meta").getString("filename"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());

            }
        });
        vlcRequest.setUsernameAndPassword("", "123");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    requestQueue.add(vlcRequest);
                    try {
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                stopSelf();
            }
        }).start();

    }

}
