package com.mediaremote.vlcontroller.net;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by neon on 23.06.15.
 */
public class VlcRequest extends JsonObjectRequest {

    private String username;
    private String password;

    public VlcRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public void setUsernameAndPassword(String username, String password) {
        this.username = username;
        this.password = password;
    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        String creds = String.format("%s:%s", username, password);
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        auth = auth.replace("\n", "");
        params.put("Authorization", auth);
        return params;
    }
}
