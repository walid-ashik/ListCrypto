package com.ashik.listcrypto;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements CurrencyAdapter.OnItemClickListener{

    private static final String TAG = ".MainActivity";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_COIN_NAME = "coin_name";
    public static final String EXTRA_SYMBOL = "symbol";
    public static final String EXTRA_RANK = "rank";
    public static final String EXTRA_PRICE_USD = "price_usd";
    public static final String EXTRA_BTC = "price_btc";
    public static final String EXTRA_VOLUME_24H = "volume_usd_24h";
    public static final String EXTRA_MARKET_CAP = "market_cap_usd";
    public static final String EXTRA_AVAILABLE_SUPPY = "available_supply";
    public static final String EXTRA_TOTAL_SUPPLY = "total_supply";
    public static final String EXTRA_PERCENT_CHANGE_1H = "percent_change_1h";
    public static final String EXTRA_PERCENT_CHANGE_24H = "percent_change_24h";
    public static final String EXTRA_PERCENT_CHANGE_7D = "percent_change_7d";
    public static final String EXTRA_LAST_UPDATED = "last_updated";

    private TextView mHistoDataTitle;
    private LineChart mChart;

    private RequestQueue mRequestQueue, mQueue;

    private RecyclerView mRecyclerView;
    private CurrencyAdapter mCurrencyAdapter;
    private ArrayList<CurrencyItem> mCurrencyList;

    //CoinActivityDetails Intent data
    private String id = "";
    private String coin_name = "";
    private String symbol = "";
    private String rank = "";
    private String price_usd = "";
    private String price_btc = "";
    private String volume_usd_24h = "";
    private String market_cap_usd = "";
    private String available_supply = "";
    private String total_supply = "";
    private String max_supply = "";
    private String percent_change_1h = "";
    private String percent_change_24h = "";
    private String percent_change_7d = "";
    private String last_updated = "";

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

        String histoDayUrl = "https://min-api.cryptocompare.com/data/histoday?fsym=BTH&tsym=USD&limit=60&aggregate=3&e=CCCAGG";

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

                        volume_usd_24h = currency.getString("24h_volume_usd");
                        market_cap_usd = currency.getString("market_cap_usd");
                        available_supply = currency.getString("available_supply");
                        total_supply = currency.getString("total_supply");
                        max_supply = currency.getString("max_supply");
                        percent_change_1h = currency.getString("percent_change_1h");
                        percent_change_24h = currency.getString("percent_change_24h");
                        percent_change_7d = currency.getString("percent_change_7d");
                        last_updated = currency.getString("last_updated");

                        Log.d(TAG, "onResponse:" + "\nid: " + id + "\ncoin_name: " + coin_name + "\n");


                        mCurrencyList.add(new CurrencyItem(id,coin_name,symbol, rank,price_usd,price_btc,volume_usd_24h, market_cap_usd,
                                available_supply, total_supply,max_supply, percent_change_1h,percent_change_24h, percent_change_7d, last_updated));



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                mCurrencyAdapter = new CurrencyAdapter(MainActivity.this, mCurrencyList);
                mRecyclerView.setAdapter(mCurrencyAdapter);
                mCurrencyAdapter.setOnItemClickListener(MainActivity.this);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mRequestQueue.add(jsonArrayRequest);

    }

    @Override
    public void onItemClick(int position) {

        Intent coinDetailsIntent = new Intent(this, CoinDetailsActivity.class);

        CurrencyItem clickedItem = mCurrencyList.get(position);
        coinDetailsIntent.putExtra(EXTRA_ID, clickedItem.getId());
        coinDetailsIntent.putExtra(EXTRA_SYMBOL, clickedItem.getSymbol());
        coinDetailsIntent.putExtra(EXTRA_RANK, clickedItem.getRank());
        coinDetailsIntent.putExtra(EXTRA_PRICE_USD, clickedItem.getPrice_usd());
        coinDetailsIntent.putExtra(EXTRA_BTC, clickedItem.getPrice_btc());
        coinDetailsIntent.putExtra(EXTRA_VOLUME_24H, clickedItem.getVolume_usd_24h());
        coinDetailsIntent.putExtra(EXTRA_MARKET_CAP, clickedItem.getMarket_cap_usd());
        coinDetailsIntent.putExtra(EXTRA_AVAILABLE_SUPPY, clickedItem.getAvailable_supply());
        coinDetailsIntent.putExtra(EXTRA_TOTAL_SUPPLY, clickedItem.getTotal_supply());
        coinDetailsIntent.putExtra(EXTRA_PERCENT_CHANGE_1H, clickedItem.getPercent_change_1h());
        coinDetailsIntent.putExtra(EXTRA_PERCENT_CHANGE_24H, clickedItem.getPercent_change_24h());
        coinDetailsIntent.putExtra(EXTRA_PERCENT_CHANGE_7D, clickedItem.getPercent_change_7d());
        coinDetailsIntent.putExtra(EXTRA_LAST_UPDATED, clickedItem.getLast_updated());

        startActivity(coinDetailsIntent);

    }
}
