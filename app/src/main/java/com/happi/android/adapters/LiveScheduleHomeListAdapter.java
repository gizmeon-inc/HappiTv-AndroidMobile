package com.happi.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewBold;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.LiveScheduleResponse;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

public class LiveScheduleHomeListAdapter extends RecyclerView.Adapter<LiveScheduleHomeListAdapter.LiveHomeViewHolder> {
    private Context context;
    private List<LiveScheduleResponse.LiveScheduleModel> liveScheduleList;

    public LiveScheduleHomeListAdapter(Context context) {
        this.liveScheduleList = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public LiveHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_schedule_home, null);
        return new LiveHomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveHomeViewHolder holder, int position) {

        if (liveScheduleList.size() != 0) {
            holder.tv_schedule_title.setText(liveScheduleList.get(position).getVideo_title());
            Glide.with(context)
                    .load(ConstantUtils.RELEASE_THUMBNAIL + liveScheduleList.get(position).getThumbnail())
                    .into(holder.iv_schedule_image);
        }
    }

    @Override
    public int getItemCount() {
        return liveScheduleList.size();
    }

    public static class LiveHomeViewHolder extends RecyclerView.ViewHolder {
        TypefacedTextViewRegular tv_schedule_time;
        TypefacedTextViewBold tv_schedule_title;
        ImageView iv_schedule_image;

        public LiveHomeViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_schedule_time = itemView.findViewById(R.id.tv_schedule_time);
            this.tv_schedule_title = itemView.findViewById(R.id.tv_schedule_title);
            this.iv_schedule_image = itemView.findViewById(R.id.iv_schedule_image);

        }
    }

    public void clearAll() {
        liveScheduleList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<LiveScheduleResponse.LiveScheduleModel> moveResults) {
        for (LiveScheduleResponse.LiveScheduleModel result : moveResults) {
            add(result);
        }
    }

    public void add(LiveScheduleResponse.LiveScheduleModel r) {
        liveScheduleList.add(r);
        notifyItemInserted(liveScheduleList.size() - 1);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }
}
