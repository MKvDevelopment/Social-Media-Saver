package com.socialmediasaver.status.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.activity.InAppPurchaseExampleActivity;
import com.socialmediasaver.status.interfaces.OnSubscriptionUpdated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/*
public class SubscriptionListAdapter extends RecyclerView.Adapter<SubscriptionListAdapter.MyViewHolder> {
    OnSubscriptionUpdated onSubscriptionUpdated;
    InAppPurchaseExampleActivity inAppPurchaseExampleActivity;
   // List<SkuDetails> skuDetailsList;
   // List<SkuDetails> skuDetailsNewList;
    private int lastCheckedPosition = 0;
    private int row_index = 0;
    private int disablePosition = -1;
    String id;

    public SubscriptionListAdapter(InAppPurchaseExampleActivity inAppPurchaseExampleActivity, List<SkuDetails> skuDetailsList, String id, OnSubscriptionUpdated onSubscriptionUpdated) {
        this.onSubscriptionUpdated = onSubscriptionUpdated;
        this.inAppPurchaseExampleActivity = inAppPurchaseExampleActivity;
        this.skuDetailsList = skuDetailsList;
        this.id = id;
        skuDetailsNewList = new ArrayList<>(4);

        for (int i = 0; i < skuDetailsList.size(); i++) {
            if (skuDetailsList.get(i).getTitle().contains("One")) {
                skuDetailsNewList.add(skuDetailsList.get(i));
            }
//            }else if(skuDetailsList.get(i).getTitle().contains("Three")){
//                skuDetailsNewList.add(skuDetailsList.get(i));
//            }else if (skuDetailsList.get(i).getTitle().contains("Six")){
//                skuDetailsNewList.add(skuDetailsList.get(i));
//            }else if(skuDetailsList.get(i).getTitle().contains("12"))  {
//                skuDetailsNewList.add(skuDetailsList.get(i));
//            }
        }
        for (int i = 0; i < skuDetailsList.size(); i++) {
            if (skuDetailsList.get(i).getTitle().contains("Three")) {
                skuDetailsNewList.add(skuDetailsList.get(i));
            }
        }
        for (int i = 0; i < skuDetailsList.size(); i++) {
            if (skuDetailsList.get(i).getTitle().contains("Six")) {
                skuDetailsNewList.add(skuDetailsList.get(i));
            }
        }

        for (int i = 0; i < skuDetailsList.size(); i++) {
            if (skuDetailsList.get(i).getTitle().contains("12")) {
                skuDetailsNewList.add(skuDetailsList.get(i));
            }
        }


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscribe_items, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if ((!(id.equalsIgnoreCase("")))&&id.equalsIgnoreCase(skuDetailsNewList.get(position).getSku())){
            holder.subscription_status.setVisibility(View.VISIBLE);

            holder.subscription_status.setText("Active Subscription");
            disablePosition=position;

        }else {
            holder.subscription_status.setVisibility(View.GONE);

        }
        holder.subscription_title.setText(skuDetailsNewList.get(position).getTitle());
        holder.subscription_price.setText("Price : " + skuDetailsNewList.get(position).getPrice());

        if (skuDetailsNewList.get(position).getTitle().contains("Six")) {
            holder.subscription_off.setVisibility(View.VISIBLE);
            holder.subscription_off.setText("20% off (80 Rs/Month)");
        } else if (skuDetailsNewList.get(position).getTitle().contains("12")) {
            holder.subscription_off.setVisibility(View.VISIBLE);
            holder.subscription_off.setText("25% off (75 Rs/Month)");
        } else if (skuDetailsNewList.get(position).getTitle().contains("Three Month")) {
            holder.subscription_off.setVisibility(View.VISIBLE);
            holder.subscription_off.setText("15% off (84 Rs/Month)");
        } else {
            holder.subscription_off.setVisibility(View.GONE);
        }
        if (disablePosition==position){
            holder.subscription_img.setChecked(false);
            holder.layout.setClickable(false);
            holder.layout.setBackgroundColor(inAppPurchaseExampleActivity.getResources().getColor(R.color.white));

        }
            holder.subscription_img.setChecked(position == lastCheckedPosition);
            if (row_index == position) {
                holder.layout.setBackground(inAppPurchaseExampleActivity.getResources().getDrawable(R.drawable.rounded_button_shape));

            } else if (row_index == -1) {
                holder.layout.setBackground(inAppPurchaseExampleActivity.getResources().getDrawable(R.drawable.rounded_button_shape));

            } else {
                holder.layout.setBackgroundColor(inAppPurchaseExampleActivity.getResources().getColor(R.color.white));

            }
            if (lastCheckedPosition == 0) {
                row_index = 0;

                lastCheckedPosition = 0;

                //because of this blinking problem occurs so
                //i have a suggestion to add notifyDataSetChanged();
                //   notifyItemRangeChanged(0, list.length);//blink list problem
                onSubscriptionUpdated.onSubscribe(0, skuDetailsNewList);
                //notifyDataSetChanged();
            }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (disablePosition==position){
//                    holder.subscription_img.setChecked(false);
//                    holder.layout.setClickable(false);
//                    holder.layout.setBackgroundColor(inAppPurchaseExampleActivity.getResources().getColor(R.color.white));
//
//                }
                row_index = position;

                lastCheckedPosition = position;

                //because of this blinking problem occurs so
                //i have a suggestion to add notifyDataSetChanged();
                //   notifyItemRangeChanged(0, list.length);//blink list problem
                onSubscriptionUpdated.onSubscribe(position, skuDetailsNewList);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return skuDetailsNewList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subscription_title, subscription_price, subscription_off,subscription_status;
        RadioButton subscription_img;
        RelativeLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            subscription_title = itemView.findViewById(R.id.subscription_title);
            subscription_price = itemView.findViewById(R.id.subscription_price);
            subscription_img = itemView.findViewById(R.id.subscription_img);
            layout = itemView.findViewById(R.id.layout);
            subscription_off = itemView.findViewById(R.id.subscription_off);
            subscription_status = itemView.findViewById(R.id.subscription_status);
        }
    }
}*/
