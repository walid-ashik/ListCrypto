package com.ashik.listcrypto;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = ".MainActivity";

    private TextView mHistoDataTitle;
    private LineChart mChart;

    private RequestQueue mRequestQueue, mQueue;

    private RecyclerView mRecyclerView;
    private CurrencyAdapter mCurrencyAdapter;
    private ArrayList<CurrencyItem> mCurrencyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //Histo Data of BTC of Last Month
        mHistoDataTitle = findViewById(R.id.text_view_btc_history_title);
        String title = "High & Low rate of last month of BTC";
        SpannableString spannableString = new SpannableString(title);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorAccent));
        spannableString.setSpan(foregroundColorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mHistoDataTitle.setText(spannableString);

        mRequestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        mQueue = Volley.newRequestQueue(this);

        mRecyclerView = findViewById(R.id.currency_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCurrencyList  = new ArrayList<>();

        mChart = findViewById(R.id.line_chart);
        mChart.getAxisLeft().setDrawLabels(false);
        mChart.getAxisRight().setDrawLabels(false);
        mChart.getXAxis().setDrawLabels(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getAxisRight().setDrawGridLines(false);
        mChart.animateX(3000);

        //Modify LineGraph
        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(false);
        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setEnabled(false);
        YAxis yAxis2 = mChart.getAxisRight();
        yAxis2.setEnabled(false);

        mChart.setDrawBorders(false);
        mChart.setDrawGridBackground(false);
        mChart.getLegend().setEnabled(false);
        // no description text
        mChart.getDescription().setEnabled(false);
        // enable touch gestures
        mChart.setTouchEnabled(true);
        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setScaleXEnabled(true);
        mChart.setScaleYEnabled(true);
        // hide legend
        Legend legend = mChart.getLegend();
        legend.setEnabled(false);
        mChart.invalidate();

        //for all currencies list
        parseJson();

        //show graph
        parseJsonForCurrencyDay();


    }

    private void parseJsonForCurrencyDay() {

        String histoDayUrl = "https://min-api.cryptocompare.com/data/histoday?fsym=BTC&tsym=USD&limit=60&aggregate=3&e=CCCAGG";



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, histoDayUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        ArrayList<Entry> yVals1 = new ArrayList<>();
                        ArrayList<Entry> yVals2 = new ArrayList<>();

                        try {

                            JSONArray jsonArray = response.getJSONArray("Data");
                            for (int i = 0; i<jsonArray.length(); i++){

                                JSONObject data = jsonArray.getJSONObject(i);

                                //format timestamp to date
                                long timestamp = data.getLong("time");

                                Date d = new Date(timestamp * 1000);
                                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
                                String date = df2.format(d);

                                double high = data.getDouble("high");
                                double low = data.getDouble("low");

                                yVals1.add(new Entry(timestamp / 1000, (float) high));
                                yVals2.add(new Entry(timestamp / 1000, (float) low));

                               // setGraphData(jsonArray.length(),timestamp, high, low);

                                Log.d(TAG, "histoday: " + "             date: " + date + "      high: " + high + "      low: " + low);

                            }

                            LineDataSet setHigh, setLow;

                            setHigh = new LineDataSet(yVals1, "High");
                            setHigh.setColor(getResources().getColor(R.color.colorAccent));
                            setHigh.setLineWidth(2f);
                            setHigh.setDrawCircles(false);
                            setHigh.setDrawValues(false);

                            setLow = new LineDataSet(yVals2, "low");
                            setLow.setColor(getResources().getColor(R.color.almostWhite));
                            setLow.setDrawCircles(false);

                            LineData lineData = new LineData(setHigh, setLow);

                            mChart.setData(lineData);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(jsonObjectRequest);

    }

    private void setGraphData(int length, long timestamp, double high, double low) {




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
