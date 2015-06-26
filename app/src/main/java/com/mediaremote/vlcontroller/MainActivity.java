package com.mediaremote.vlcontroller;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.toString();

    public final static String IP_ADDRESS = "10.42.0.1";
    public final static String PORT = "8080";

    private RequestQueue requestQueue;
    private TextView textView;
    private Button btnPlayPause;
    private Button btnStop;
    private Button btnVolumeUp;
    private Button btnVolumeDown;
    private Status status;
    private PendingIntent pendingIntent;
    private Intent intent;
    private Intent temp = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.display_state);
        btnPlayPause = (Button) findViewById(R.id.play_pause);
        btnStop = (Button) findViewById(R.id.stop);
        btnVolumeDown = (Button) findViewById(R.id.volume_down);
        btnVolumeUp = (Button) findViewById(R.id.volume_up);
        pendingIntent = createPendingResult(1, temp, 0);
        intent = new Intent(this, StatusService.class)
                .putExtra("URL", "http://10.42.0.1:8080/requests/status.json")
                .putExtra("PI", pendingIntent);
        requestQueue = Volley.newRequestQueue(this);
        startService(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_find_vlc) {
            startActivity(new Intent(this, VlcServersListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void playPause(View view) {

        final String urlPlay = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json?command=pl_forceresume";
        final String urlPause = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json?command=pl_forcepause";

        VlcRequest vlcRequest = null;

        if (status.getState().equals("paused")) {
            vlcRequest = new VlcRequest(Request.Method.GET, urlPlay, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            vlcRequest.setUsernameAndPassword("", "123");

        } else if (status.getState().equals("playing")) {
            vlcRequest = new VlcRequest(Request.Method.GET, urlPause, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            vlcRequest.setUsernameAndPassword("", "123");
        }


        requestQueue.add(vlcRequest);

    }

    public void Stop(View view) {
        final String urlStop = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json?command=pl_stop";

        VlcRequest vlcRequest = null;
        vlcRequest = new VlcRequest(Request.Method.GET, urlStop, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        vlcRequest.setUsernameAndPassword("", "123");
        requestQueue.add(vlcRequest);
        stopService(new Intent(this, StatusService.class));
    }

    public void VolumeUp(View view) {
        final String volumeUpURL = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json?command=volume&val=+5";

        VlcRequest vlcRequest = null;
        vlcRequest = new VlcRequest(Request.Method.GET, volumeUpURL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        vlcRequest.setUsernameAndPassword("", "123");
        requestQueue.add(vlcRequest);
    }

    public void VolumeDown(View view) {
        final String volumeDownURL = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json?command=volume&val=-5";

        VlcRequest vlcRequest = null;
        vlcRequest = new VlcRequest(Request.Method.GET, volumeDownURL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        vlcRequest.setUsernameAndPassword("", "123");
        requestQueue.add(vlcRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        status = Status.getInstance();
        Log.d(TAG, status.getArtist());

    }
}
