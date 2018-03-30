package com.ashik.listcrypto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = ".MainActivity";

    private RequestQueue mRequestQueue;

    private RecyclerView mRecyclerView;
    private CurrencyAdapter mCurrencyAdapter;
    private ArrayList<CurrencyItem> mCurrencyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        mRequestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        mRecyclerView = findViewById(R.id.currency_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCurrencyList  = new ArrayList<>();

        parseJson();


    }

    private void parseJson() {

        String url = "https://api.coinmarketcap.com/v1/ticker/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for(int i=0; i<response.length(); i++){

                    try {
                        JSONObject currency = (JSONObject) response.get(i);

                        String id = currency.getString("id");
                        String coin_name = currency.getString("name");
                        String symbol = currency.getString("symbol");
                        String rank = currency.getString("rank");
                        String price_usd = currency.getString("price_usd");
                        String price_btc = currency.getString("price_btc");

                       /*
                        String volume_usd_24h = currency.getString("24h_volume_usd");
                        String market_cap_usd = currency.getString("market_cap_usd");
                        String available_supply = currency.getString("available_supply");
                        String total_supply = currency.getString("total_supply");
                        String max_supply = currency.getString("max_supply");
                        String percent_change_1h = currency.getString("percent_change_1h");
                        String percent_change_24h = currency.getString("percent_change_24h");
                        String percent_change_7d = currency.getString("percent_change_7d");
                        String last_updated = currency.getString("last_updated");

                        Log.d(TAG, "onResponse:" + "\nid: " + id + "\ncoin_name: " + coin_name + "\n");

                        */

                        mCurrencyList.add(new CurrencyItem(id,coin_name,symbol, rank,price_usd,price_btc));



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                mCurrencyAdapter = new CurrencyAdapter(MainActivity.this, mCurrencyList);
                mRecyclerView.setAdapter(mCurrencyAdapter);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mRequestQueue.add(jsonArrayRequest);

    }
}
