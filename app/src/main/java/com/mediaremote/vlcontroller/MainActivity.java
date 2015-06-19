package com.mediaremote.vlcontroller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.toString();

    public final static String IP_ADDRESS = "192.168.1.101";
    public final static String PORT = "8080";

    private RequestQueue requestQueue;
    private Map<String, String> vlcStatus = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getStatusInfo() {
        final String requestUrl = "http://" + IP_ADDRESS + ":" + PORT + "/requests/status.xml";
        final String url2 = "http://192.168.1.101:8080/requests/status.xml";


        VlcRequest vlcRequest = new VlcRequest(Request.Method.GET, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(new StringReader(response));
                    String state = "";

                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG && parser.getName().equals("state")) {
                             if (parser.next() == XmlPullParser.TEXT) {
                                 state = parser.getText();
                                 vlcStatus.put("state", state);
                                 break;
                            }
                        } else {
                            eventType = parser.next();
                        }
                    }

                    Log.i(TAG, "STATE = " + state);

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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

    public void play_pause(View view) {
        final String urlPlay = "http://192.168.1.101:8080/requests/status.xml?command=pl_play";
        final String urlPause = "http://192.168.1.101:8080/requests/status.xml?command=pl_pause";

        getStatusInfo();
        VlcRequest vlcRequest = null;

        if (vlcStatus.get("state").equals("paused")) {
            vlcRequest = new VlcRequest(Request.Method.GET, urlPlay, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            vlcRequest.setUsernameAndPassword("", "123");

        } else if (vlcStatus.get("state").equals("playing")) {
            vlcRequest = new VlcRequest(Request.Method.GET, urlPause, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

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
}
