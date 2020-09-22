package com.happi.android.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.ChannelModel;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {
    private List<ChannelModel> channelList;
    private itemClickListener thisItemClickListener;
    private Context context;

    public ChannelAdapter(Context context, itemClickListener thisItemClickListener) {
        this.context = context;
        this.thisItemClickListener = thisItemClickListener;
        channelList = new ArrayList<>();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_channel_logo;
        TypefacedTextViewRegular tv_channel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_channel_logo = itemView.findViewById(R.id.iv_channel_logo);
            this.tv_channel = itemView.findViewById(R.id.tv_channel);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    thisItemClickListener.onChannelItemClicked(getAdapterPosition());
                }
            });



        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_channel_list,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ChannelModel channelModel=channelList.get(i);
        Glide.with(context)
                .load(ConstantUtils.CHANNEL_THUMBNAIL + channelModel.getLogo())
                .error(Glide.with(context).load(ContextCompat.getDrawable(context,
                        R.drawable.ic_placeholder)))
                .apply(placeholderOf(R.drawable.ic_placeholder))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.DATA))
                .into(viewHolder.iv_channel_logo);
        if (!TextUtils.isEmpty(channelModel.getChannelName()) && !channelModel.getChannelName().equals("")) {
            viewHolder.tv_channel.setText(channelModel.getChannelName());
        }else {
            viewHolder.tv_channel.setText(context.getResources().getString(R.string.demo_channel));
        }

    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public void updateList(List<ChannelModel> list){
        channelList = list;
        notifyDataSetChanged();
    }
    public ChannelModel getItem(int position) {
        return channelList.get(position);
    }

    public interface itemClickListener {
        void onChannelItemClicked(int adapterPosition);
    }

}
