package com.mediaremote.vlcontroller.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mediaremote.vlcontroller.R;
import com.mediaremote.vlcontroller.fragment.FavoritesFragment;
import com.mediaremote.vlcontroller.model.Status;
import com.mediaremote.vlcontroller.service.StatusService;
import com.mediaremote.vlcontroller.net.VlcRequest;

import org.json.JSONObject;


public class PlayerControlActivity extends Activity {
    private static final String TAG = PlayerControlActivity.class.toString();

    public  static String IP_ADDRESS;
    public  static String PORT;

    private RequestQueue requestQueue;
    private Button btnPlayPause;
    private Button btnStop;
    private Button btnVolumeUp;
    private Button btnVolumeDown;
    private Button btnPrev;
    private Button btnNext;
    private Status status;

    private Intent intent;

    public static final String URL_REQUEST = "URL";
    public static final String BROADCAST_ACTION = "Message";
    public static final String PARAM_TASK = "task";
    public final int TASK1 = 1;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String ipAndPort = getIntent().getStringExtra(FavoritesFragment.SERVER_DATA_URI);
        IP_ADDRESS = ipAndPort.split(":")[0];
        PORT = ipAndPort.split(":")[1];

        btnPlayPause = (Button) findViewById(R.id.play_pause);
        btnStop = (Button) findViewById(R.id.stop);
        btnVolumeDown = (Button) findViewById(R.id.volume_down);
        btnVolumeUp = (Button) findViewById(R.id.volume_up);
        btnPrev = (Button) findViewById(R.id.previous);
        btnNext = (Button) findViewById(R.id.next);
        status = Status.getInstance();     broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int task = intent.getIntExtra(PARAM_TASK, 0);

                updateView();
                Log.d(TAG, "OnReceive worked.....");

            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);

        registerReceiver(broadcastReceiver, intentFilter);

        intent = new Intent(this, StatusService.class)
                .putExtra(URL_REQUEST, "http://" + IP_ADDRESS + ":" + PORT +"/requests/status.json")
                .putExtra(PARAM_TASK, TASK1);

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

        final String urlPlay = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json?command=pl_play";
        final String urlPause = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json?command=pl_pause";

        VlcRequest vlcRequest = null;

        if (status.getState().equals("paused") || status.getState().equals("stopped")) {
            vlcRequest = new VlcRequest(Request.Method.GET, urlPlay, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


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

        }

        vlcRequest.setUsernameAndPassword("", "123");
        requestQueue.add(vlcRequest);

    }

    public void stop(View view) {
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


    }

    public void volumeUp(View view) {
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

    public void volumeDown(View view) {
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

    public void nextItem(View view) {
        Log.d(TAG, "" + status.getTrack_total());
        Log.d(TAG, "" + status.getTrack_number());
        if (status.getTrack_number() < status.getTrack_total()) {
            final String nextURL = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json?command=pl_next";

            VlcRequest vlcRequest = null;
            vlcRequest = new VlcRequest(Request.Method.GET, nextURL, null, new Response.Listener<JSONObject>() {

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


    public void prevItem(View view) {

        if (status.getTrack_number() > 1) {

            final String prevURL = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.json?command=pl_previous";

            VlcRequest vlcRequest = null;
            vlcRequest = new VlcRequest(Request.Method.GET, prevURL, null, new Response.Listener<JSONObject>() {

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

    @Override
    protected void onResume() {
        super.onResume();
        startService(intent);
        Log.d(TAG, "OnResume called....");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, StatusService.class));
        Log.d(TAG, "OnPaused called....");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void updateView() {
        String state = status.getState();
        if (state.equals("paused") || state.equals("stopped")) {
            Drawable image = getResources().getDrawable(R.drawable.play);
            btnPlayPause.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
        } else {
            Drawable image = getResources().getDrawable(R.drawable.pause);
            btnPlayPause.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
        }
    }
}
