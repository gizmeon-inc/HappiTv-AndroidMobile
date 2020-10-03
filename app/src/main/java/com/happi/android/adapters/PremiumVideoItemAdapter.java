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
import com.happi.android.models.VideoSubscriptionModel;

import java.util.ArrayList;
import java.util.List;

public class PremiumVideoItemAdapter extends RecyclerView.Adapter<PremiumVideoItemAdapter.ViewHolder> {
    Context context;
    itemClickListener thisItemClickListener;
    private List<VideoSubscriptionModel> videoSubscriptionModelList;
    public int row_index = 0;


    public PremiumVideoItemAdapter(Context context, itemClickListener thisItemClickListener) {
        this.context = context;
        this.thisItemClickListener = thisItemClickListener;
        videoSubscriptionModelList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_subscription, viewGroup, false);
        return new ViewHolder(v);


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TypefacedTextViewBold tv_subscription_name;
        TypefacedTextViewBold tv_subscription_type;
        TypefacedTextViewBold tv_price;
        LinearLayout ll_premium;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_subscription_name = itemView.findViewById(R.id.tv_subscription_name);
            this.tv_subscription_type = itemView.findViewById(R.id.tv_subscription_type);
            this.tv_price = itemView.findViewById(R.id.tv_price);
            this.ll_premium = itemView.findViewById(R.id.ll_premium);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    thisItemClickListener.onPremiumVideoItemClicked(getAdapterPosition());
                    row_index = getAdapterPosition();
                    notifyDataSetChanged();
                    //    Log.e("AAAAAAAAAAAAAAAA","Adapter "+thisItemClickListener.onPremiumVideoItemClicked(getAdapterPosition()).);

                    //ll_premium.setBackgroundResource(R.drawable.bg_outline_orange);

                }
            });


        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        VideoSubscriptionModel videoSubscriptionModel = videoSubscriptionModelList.get(i);
        viewHolder.tv_subscription_name.setText(videoSubscriptionModel.getSubscription_name());
        viewHolder.tv_subscription_type.setText(videoSubscriptionModel.getSubscription_type_name());
        viewHolder.tv_price.setText(videoSubscriptionModel.getNew_price());

        // if(i==row_index){
        // viewHolder.ll_premium.setBackgroundResource(R.drawable.bg_outline_orange);*/

        /*viewHolder.ll_premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = i;
                notifyDataSetChanged();
            }
        });*/
        if (row_index == i) {
            //viewHolder.ll_premium.setAlpha((float) .08);
            viewHolder.ll_premium.setBackgroundResource(R.drawable.bg_outline_orange);
        } else {
           // viewHolder.ll_premium.setAlpha((float) 1);
            viewHolder.ll_premium.setBackgroundResource(R.drawable.bg_outline_grey);
        }
        //else{
        // viewHolder.ll_premium.setBackgroundResource(R.drawable.bg_outline_grey);

        // }}
    }


    @Override
    public int getItemCount() {
        return videoSubscriptionModelList.size();
    }

    public void updateList(List<VideoSubscriptionModel> list) {
        videoSubscriptionModelList = list;
        notifyDataSetChanged();
    }

    public VideoSubscriptionModel getItem(int position) {
        return videoSubscriptionModelList.get(position);
    }

    public void setSelected(int index) {
        row_index = index;
    }


    public interface itemClickListener {
        void onPremiumVideoItemClicked(int adapterPosition);
    }
}
