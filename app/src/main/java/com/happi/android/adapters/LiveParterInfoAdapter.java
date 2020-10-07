package com.happi.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewBold;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.LiveParterInfoModel;



import java.util.ArrayList;

import java.util.List;


public class LiveParterInfoAdapter extends RecyclerView.Adapter<LiveParterInfoAdapter.LivePartnerViewHolder> {
    private Context context;
    private List<LiveParterInfoModel> liveParterInfoModelList;

    public LiveParterInfoAdapter(Context context) {
        this.liveParterInfoModelList = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public LivePartnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_partner_info, null);
        return new LivePartnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LivePartnerViewHolder holder, int position) {

        if (liveParterInfoModelList.size() != 0) {
           holder.tv_schedule_time.setText(liveParterInfoModelList.get(position).getPartnerName());
           holder.tv_schedule_title.setText(liveParterInfoModelList.get(position).getPartnerDescription());
        }
    }





    @Override
    public int getItemCount() {
        return liveParterInfoModelList.size();
    }

    public static class LivePartnerViewHolder extends RecyclerView.ViewHolder {
        TypefacedTextViewRegular tv_schedule_time;
        TypefacedTextViewBold tv_schedule_title;
        TypefacedTextViewBold tv_schedule_day;
        ImageView iv_schedule_image;

        public LivePartnerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_schedule_time = itemView.findViewById(R.id.tv_schedule_time);
            this.tv_schedule_title = itemView.findViewById(R.id.tv_schedule_title);
            this.iv_schedule_image = itemView.findViewById(R.id.iv_schedule_image);
            this.tv_schedule_day = itemView.findViewById(R.id.tv_schedule_day);

        }
    }

    public void clearAll() {
        liveParterInfoModelList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<LiveParterInfoModel> moveResults) {
        for (LiveParterInfoModel result : moveResults) {
            add(result);
        }
    }

    public void add(LiveParterInfoModel r) {
        liveParterInfoModelList.add(r);
        notifyItemInserted(liveParterInfoModelList.size() - 1);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }
}

