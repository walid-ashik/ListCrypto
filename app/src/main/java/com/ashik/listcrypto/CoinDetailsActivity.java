package com.ashik.listcrypto;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.ashik.listcrypto.MainActivity.EXTRA_AVAILABLE_SUPPY;
import static com.ashik.listcrypto.MainActivity.EXTRA_BTC;
import static com.ashik.listcrypto.MainActivity.EXTRA_COIN_NAME;
import static com.ashik.listcrypto.MainActivity.EXTRA_ID;
import static com.ashik.listcrypto.MainActivity.EXTRA_LAST_UPDATED;
import static com.ashik.listcrypto.MainActivity.EXTRA_MARKET_CAP;
import static com.ashik.listcrypto.MainActivity.EXTRA_PERCENT_CHANGE_1H;
import static com.ashik.listcrypto.MainActivity.EXTRA_PERCENT_CHANGE_24H;
import static com.ashik.listcrypto.MainActivity.EXTRA_PERCENT_CHANGE_7D;
import static com.ashik.listcrypto.MainActivity.EXTRA_PRICE_USD;
import static com.ashik.listcrypto.MainActivity.EXTRA_RANK;
import static com.ashik.listcrypto.MainActivity.EXTRA_SYMBOL;
import static com.ashik.listcrypto.MainActivity.EXTRA_TOTAL_SUPPLY;
import static com.ashik.listcrypto.MainActivity.EXTRA_VOLUME_24H;

public class CoinDetailsActivity extends AppCompatActivity {

    private static final String TAG = "COINDETAILSACTIVITY";

    private LineChart mChart;
    private TextView mCoinPriceInUsd;

    private RequestQueue mQueue;

    private String coinId;
    private String coinCoinName;
    private String coinSymbol;
    private String coinRank;
    private String coinPriceUsd;
    private String coinPriceBtc;
    private String coinVolume24h;
    private String coinMarketCap;
    private String coinAvailableSupply;
    private String coinTotalSupply;
    private String coinChange1h;
    private String coinChnage24h;
    private String coinChange7d;
    private String coinLastUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_details);
        getSupportActionBar().hide();

        mChart = findViewById(R.id.coin_details_line_chart);
        mCoinPriceInUsd = findViewById(R.id.coin_details_price_usd);

        //get details from previous activity
        coinId = getIntent().getStringExtra(EXTRA_ID);
        coinCoinName = getIntent().getStringExtra(EXTRA_COIN_NAME);
        coinSymbol = getIntent().getStringExtra(EXTRA_SYMBOL);
        coinRank = getIntent().getStringExtra(EXTRA_RANK);
        coinPriceUsd = getIntent().getStringExtra(EXTRA_PRICE_USD);
        coinPriceBtc = getIntent().getStringExtra(EXTRA_BTC);
        coinVolume24h = getIntent().getStringExtra(EXTRA_VOLUME_24H);
        coinMarketCap = getIntent().getStringExtra(EXTRA_MARKET_CAP);
        coinAvailableSupply = getIntent().getStringExtra(EXTRA_AVAILABLE_SUPPY);
        coinTotalSupply = getIntent().getStringExtra(EXTRA_TOTAL_SUPPLY);
        coinChange1h = getIntent().getStringExtra(EXTRA_PERCENT_CHANGE_1H);
        coinChnage24h = getIntent().getStringExtra(EXTRA_PERCENT_CHANGE_24H);
        coinChange7d = getIntent().getStringExtra(EXTRA_PERCENT_CHANGE_7D);
        coinLastUpdated = getIntent().getStringExtra(EXTRA_LAST_UPDATED);

        float f = Float.valueOf(coinPriceUsd);
        int usdCoinPrice = Math.round(f);

        //Set coin Price Usd
        //animate if price is greater than 0 else set string directly to textview
        if(usdCoinPrice > 1){
            animatePriceInUsd(0, usdCoinPrice, mCoinPriceInUsd);
        }else {
            mCoinPriceInUsd.setText("$" + coinPriceUsd );
        }

        Log.d(TAG, "details: " + coinId + " " + coinCoinName + " " + coinSymbol + " " +coinRank + " " +coinPriceUsd + " " +coinPriceBtc
                + " " +coinVolume24h + " " +coinMarketCap + " " +coinAvailableSupply + " " +coinTotalSupply + " " +coinChange1h + " " +coinChnage24h + " " +coinChange7d + " " + coinLastUpdated + "\n\n\n");

        mQueue = new VolleySingleton(this).getRequestQueue();

        modifyLineChart(mChart);

        if(!coinSymbol.equals("ACT")){
            //show graph
            parseJsonForCurrencyDay();
        }


    }

    private void animatePriceInUsd(int i, int usdCoinPrice, final TextView mCoinPriceInUsd) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0,  usdCoinPrice);
        valueAnimator.setDuration(1500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCoinPriceInUsd.setText( "$" + valueAnimator.getAnimatedValue().toString());
            }
        });
        valueAnimator.start();

    }

    private void modifyLineChart(LineChart mChart) {

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

    }

    private void parseJsonForCurrencyDay() {

        String histoDayUrl = "https://min-api.cryptocompare.com/data/histoday?fsym="  + coinSymbol + "&tsym=USD&limit=60&aggregate=3&e=CCCAGG";

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


}
