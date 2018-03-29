package com.ashik.listcrypto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = ".MainActivity";

    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        mQueue = Volley.newRequestQueue(this);

        jsonParse();


    }

    private void jsonParse() {

        String url = "https://api.coinmarketcap.com/v1/ticker/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for(int i=0; i<response.length(); i++){

                    try {
                        JSONObject currency = (JSONObject) response.get(i);

                        String id = currency.getString("id");
                        String coin_name = currency.getString("name");
                        String rank = currency.getString("rank");

                        Log.d(TAG, "onResponse: " + "\nid: " + id + "\n" + "coin_name: " + coin_name + "\n" + "rank: " + rank + "\n\n");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mQueue.add(jsonArrayRequest);

    }
}
