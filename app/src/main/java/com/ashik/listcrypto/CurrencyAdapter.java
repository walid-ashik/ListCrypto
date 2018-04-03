package com.ashik.listcrypto;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder> {

    private Context mContext;
    private ArrayList<CurrencyItem> mCurrencyList;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public CurrencyAdapter(Context context, ArrayList<CurrencyItem> currencyList) {
        mContext = context;
        mCurrencyList = currencyList;
    }

    @Override
    public CurrencyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_item_coin, parent, false);
        return new CurrencyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(CurrencyViewHolder holder, int position) {

        CurrencyItem currentItem = mCurrencyList.get(position);

        String currencyName = currentItem.getCoin_name();
        String currencySymbol = currentItem.getSymbol();

       // currencySymbol = currencySymbol.substring(0,2);

        String currencyRank = currentItem.getRank();
        String currencyRate = currentItem.getPrice_btc();
        String currencyRateInUSD = currentItem.getPrice_usd();

        holder.mCurrencySymbol.setText(currencySymbol);
        holder.mCurrencyName.setText(currencyName);
        holder.mCurrencyRank.setText("rank: " + currencyRank);
        holder.mCurrencyRate.setText(currencyRate);
        holder.mCurrencyRateInUSD.setText("$"+ currencyRateInUSD);

    }

    @Override
    public int getItemCount() {
        return mCurrencyList.size();
    }

    public class CurrencyViewHolder extends  RecyclerView.ViewHolder{

        public TextView mCurrencySymbol;
        public TextView mCurrencyName;
        public TextView mCurrencyRank;
        public TextView mCurrencyRate;
        public TextView mCurrencyRateInUSD;

        public CurrencyViewHolder(View itemView) {
            super(itemView);

            mCurrencySymbol = itemView.findViewById(R.id.text_view_currency_icon);
            mCurrencyName = itemView.findViewById(R.id.text_view_currency_name);
            mCurrencyRank = itemView.findViewById(R.id.text_view_currency_rank);
            mCurrencyRate = itemView.findViewById(R.id.text_view_currency_rate);
            mCurrencyRateInUSD = itemView.findViewById(R.id.text_view_currency_rate_in_usd);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(mListener != null){

                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){

                            mListener.onItemClick(position);

                        }

                    }

                }
            });

        }
    }

}
