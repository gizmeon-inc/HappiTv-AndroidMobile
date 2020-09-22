package com.happi.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewBold;
import com.happi.android.models.ChannelSubscriptionModel;

import java.util.ArrayList;
import java.util.List;

public class PremiumChannelItemAdapter extends RecyclerView.Adapter<PremiumChannelItemAdapter.ViewHolder> {
    Context context;
    itemClickListener thisItemClickListener;
    private List<ChannelSubscriptionModel> channelSubscriptionModelList;
    public int row_index = 0;


    public PremiumChannelItemAdapter(Context context, itemClickListener thisItemClickListener) {
        this.context = context;
        this.thisItemClickListener = thisItemClickListener;
        channelSubscriptionModelList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_subscription, viewGroup, false);
        return new ViewHolder(v);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TypefacedTextViewBold tv_subscription_type;
        TypefacedTextViewBold tv_price;
        TypefacedTextViewBold tv_currency;
        TypefacedTextViewBold tv_subscription_name;
        LinearLayout ll_premium;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_subscription_type = itemView.findViewById(R.id.tv_subscription_type);
            this.tv_price = itemView.findViewById(R.id.tv_price);
            this.ll_premium = itemView.findViewById(R.id.ll_premium);
            this.tv_subscription_name = itemView.findViewById(R.id.tv_subscription_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    thisItemClickListener.onPremiumChannelItemClicked(getAdapterPosition());
                    row_index = getAdapterPosition();
                    notifyDataSetChanged();

                }
            });

        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ChannelSubscriptionModel channelSubscriptionModel = channelSubscriptionModelList.get(i);
        viewHolder.tv_subscription_type.setText(channelSubscriptionModel.getSubscription_type_name());
        viewHolder.tv_price.setText(channelSubscriptionModel.getNew_price());
        viewHolder.tv_subscription_name.setText(channelSubscriptionModel.getSubscription_name());

        if (row_index == i) {
            viewHolder.ll_premium.setBackgroundResource(R.drawable.bg_outline_orange);
        } else {
            viewHolder.ll_premium.setBackgroundResource(R.drawable.bg_outline_grey);
        }
    }


    @Override
    public int getItemCount() {
        return channelSubscriptionModelList.size();
    }

    public void updateList(List<ChannelSubscriptionModel> list) {
        channelSubscriptionModelList = list;
        notifyDataSetChanged();
    }

    public ChannelSubscriptionModel getItem(int position) {
        return channelSubscriptionModelList.get(position);
    }

    public void setSelected(int index) {
        row_index = index;
    }


    public interface itemClickListener {
        void onPremiumChannelItemClicked(int adapterPosition);
    }
}
