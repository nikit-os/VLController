package com.mediaremote.vlcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
    public Status status = new Status();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.display_state);
        btnPlayPause = (Button) findViewById(R.id.play_pause);
        btnStop = (Button) findViewById(R.id.stop);
        btnVolumeDown = (Button) findViewById(R.id.volume_down);
        btnVolumeUp = (Button) findViewById(R.id.volume_up);
        requestQueue = Volley.newRequestQueue(this);
        getStatusInfo();


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
        }

        return super.onOptionsItemSelected(item);
    }

    private void getStatusInfo() {
        final String URL = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json";

        VlcRequest vlcRequest = new VlcRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
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
        requestQueue.add(vlcRequest);
    }

    public void playPause(View view) {
        textView.setText(status.getState());
        final String urlPlay = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json?command=pl_forceresume";
        final String urlPause = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json?command=pl_forcepause";

        getStatusInfo();
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
    public void Stop (View view){
        final String urlStop = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json?command=pl_stop";

        getStatusInfo();
        VlcRequest vlcRequest = null;
        vlcRequest = new VlcRequest(Request.Method.GET, urlStop, null, new Response.Listener<JSONObject>(){

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

    public void VolumeUp (View view){
        final String volumeUpURL = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json?command=volume&val=+5";

        getStatusInfo();
        VlcRequest vlcRequest = null;
        vlcRequest = new VlcRequest(Request.Method.GET, volumeUpURL, null, new Response.Listener<JSONObject>(){

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
    public void VolumeDown (View view){
        final String volumeDownURL = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json?command=volume&val=-5";

        getStatusInfo();
        VlcRequest vlcRequest = null;
        vlcRequest = new VlcRequest(Request.Method.GET, volumeDownURL, null, new Response.Listener<JSONObject>(){

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
}
