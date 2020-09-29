package com.happi.android.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.happi.android.HomeActivity;
import com.happi.android.MainHomeActivity;
import com.happi.android.common.SharedPreferenceUtility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewBold;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.models.ChannelModel;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.MyViewHolder>{

    private List<ChannelModel> channelList;
    private Context context;
    private itemClickListener clickObj;
    private boolean isVertical = false;
    private Activity activity;
    private RedirectToLive redirect;

    public ChannelListAdapter(Context context, itemClickListener clickObj, boolean isVertical) {
        this.context = context;
        this.clickObj = clickObj;
        this.isVertical = isVertical;
        channelList = new ArrayList<>();
    }
    public ChannelListAdapter(Context context, itemClickListener clickObj, boolean isVertical, Activity activity, RedirectToLive redirect) {
        this.context = context;
        this.clickObj = clickObj;
        this.isVertical = isVertical;
        this.activity = activity;
        this.redirect = redirect;
        channelList = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (isVertical) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_channels_vertical, parent, false);
            return new MyViewHolder(view);
        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_channel_horizontal, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_live_tag.setVisibility(View.VISIBLE);
        ChannelModel cModel = channelList.get(position);
        Glide.with(context)
                .load(ConstantUtils.CHANNEL_NEW_THUMBNAIL.trim() + cModel.getLogo().trim())
               // .load("https://gizmeon.s.llnwi.net/vod/thumbnails/images/1564037567.png")
                .error(Glide.with(context).load(ContextCompat.getDrawable(context,
                       R.drawable.ic_placeholder)))
                .apply(placeholderOf(R.drawable.ic_placeholder))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.DATA))
                .into(holder.iv_thumbnail);
        Log.d("LOGO",ConstantUtils.CHANNEL_NEW_THUMBNAIL + cModel.getLogo());
        if(activity != null) {
            //if (((HomeActivity) activity).isRedirectToLive) {
            if (((MainHomeActivity) activity).isRedirectToLive) {
              //  if (((HomeActivity) activity).channelId != 0 && (((HomeActivity) activity).channelId == cModel.getChannelId())) {
                if (((MainHomeActivity) activity).channelId != 0 && (((MainHomeActivity) activity).channelId == cModel.getChannelId())) {
                    SharedPreferenceUtility.setChannelTimeZone(cModel.getTimezone());
                    redirect.onRedirectionToLive(cModel);
                }

            }
        }
       /* if (cModel.getLiveFlag() != null) {
            if (cModel.getLiveFlag() == 1) {
                holder.tv_live_tag.setVisibility(View.VISIBLE);
            }else {
                holder.tv_live_tag.setVisibility(View.GONE);
            }
        }*/

       /* if(cModel.getPremiumFlag()!=null){
            if(cModel.getPremiumFlag().equals("1")){
                holder.iv_premium_tag.setVisibility(View.VISIBLE);
            }else{
                holder.iv_premium_tag.setVisibility(View.INVISIBLE);

            }
        }*/

       /* if (!TextUtils.isEmpty(cModel.getChannelName()) && !cModel.getChannelName().equals("")) {
            holder.tv_video_title.setText(cModel.getChannelName());
        }else {
            holder.tv_video_title.setText(context.getResources().getString(R.string.demo_channel));
        }*/
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public void updateList(List<ChannelModel> list){
        channelList = list;
        notifyDataSetChanged();
    }
    public void addAll(List<ChannelModel> moveResults) {
        for (ChannelModel result : moveResults) {
            add(result);
        }
    }
    public void add(ChannelModel r) {
        channelList.add(r);
        notifyItemInserted(channelList.size() - 1);
    }
    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }
    public ChannelModel getItem(int position) {
        return channelList.get(position);
    }
    public void remove(ChannelModel model) {
        int position = channelList.indexOf(model);
        if (position > -1) {
            channelList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void clearAll() {
        channelList.clear();
        notifyDataSetChanged();
    }
    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_thumbnail;
        ImageView iv_premium_tag;
        TypefacedTextViewBold tv_live_tag;
        TypefacedTextViewSemiBold tv_video_title;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            this.iv_premium_tag = itemView.findViewById(R.id.iv_premium_tag);
            this.tv_live_tag = itemView.findViewById(R.id.tv_live_tag);
            this.tv_video_title = itemView.findViewById(R.id.tv_video_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickObj.onChannelItemClicked(getAdapterPosition());
                }
            });
        }
    }

    public interface itemClickListener {
        void onChannelItemClicked(int adapterPosition);
    }
    public interface RedirectToLive{
        void onRedirectionToLive(ChannelModel channelModel);
    }
}
