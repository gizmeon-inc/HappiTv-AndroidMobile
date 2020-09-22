package com.happi.android.adapters;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.happi.android.common.HappiApplication;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.ChannelScheduleModel;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class ChannelScheduleAdapter extends RecyclerView.Adapter<ChannelScheduleAdapter.ViewHolder> {
    private List<ChannelScheduleModel> channelScheduleList;
    Context context;
    itemClickListener thisItemClickListener;




    public ChannelScheduleAdapter(Context context,itemClickListener thisItemClickListener) {
        this.context=context;
        this.thisItemClickListener=thisItemClickListener;
        channelScheduleList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(HappiApplication.getCurrentContext()).inflate(R.layout.item_channel_schedule,viewGroup,false);
        return new ViewHolder(v);

    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_from;
        TextView tv_to;
        TypefacedTextViewRegular tv_ProgramTitle;
        ImageView iv_live;
        ImageView iv_thumbnail;
        public ViewHolder(@NonNull View itemView)
        { super(itemView);
            tv_from = itemView.findViewById(R.id.tv_from);
            tv_to = itemView.findViewById(R.id.tv_to);
            tv_ProgramTitle = itemView.findViewById(R.id.tv_ProgramTitle);
            iv_live = itemView.findViewById(R.id.iv_live);
            iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    thisItemClickListener.onScheduleItemClicked(getAdapterPosition());
                }
            });
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ChannelScheduleModel channelScheduleModel = channelScheduleList.get(i);

        Glide.with(context)
                .load(ConstantUtils.CHANNEL_THUMBNAIL + channelScheduleModel.getImage())
                .error(Glide.with(context).load(ContextCompat.getDrawable(context,
                        R.drawable.ic_placeholder)))
                .apply(placeholderOf(R.drawable.ic_placeholder))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.DATA))
                .into(viewHolder.iv_thumbnail);
        viewHolder.tv_from.setText(channelScheduleModel.getPgm_start_time());
        viewHolder.tv_to.setText(channelScheduleModel.getPgm_end_time());
        viewHolder.tv_ProgramTitle.setText(channelScheduleModel.getProgram_name());

        if(channelScheduleModel.getStatus_flag()){
            viewHolder.iv_live.setVisibility(View.VISIBLE);
        }else{
            viewHolder.iv_live.setVisibility(View.GONE);

        }


    }

    @Override
    public int getItemCount() {
        return channelScheduleList.size();
    }



public void updateList(List<ChannelScheduleModel> list)
{
    channelScheduleList = list;
    notifyDataSetChanged();
}
    public ChannelScheduleModel getItem(int position) {
        return channelScheduleList.get(position);
    }


    public interface itemClickListener {
        void onScheduleItemClicked(int adapterPosition);
    }
}
