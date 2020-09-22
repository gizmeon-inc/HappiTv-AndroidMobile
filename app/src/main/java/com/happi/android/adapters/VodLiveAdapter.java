package com.happi.android.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewBold;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.models.VodToLiveModel;
import com.happi.android.utils.ConstantUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class VodLiveAdapter extends RecyclerView.Adapter<VodLiveAdapter.MyViewHolder>{

    private List<VodToLiveModel> vodToLiveModelList;
    private Context context;
    private VodLiveAdapter.itemClickListener clickObj;
    private boolean isVertical = false;

    public VodLiveAdapter(Context context, VodLiveAdapter.itemClickListener clickObj, boolean isVertical) {
        this.context = context;
        this.clickObj = clickObj;
        this.isVertical = isVertical;
        vodToLiveModelList = new ArrayList<>();
    }

    @Override
    public VodLiveAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (isVertical) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_channels_vertical, parent, false);
            return new VodLiveAdapter.MyViewHolder(view);
        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_channel_horizontal, parent, false);
            return new VodLiveAdapter.MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(VodLiveAdapter.MyViewHolder holder, int position) {
        holder.tv_live_tag.setVisibility(View.GONE);
        VodToLiveModel cModel = vodToLiveModelList.get(position);
        Glide.with(context)
                .load(ConstantUtils.CHANNEL_THUMBNAIL.trim() + cModel.getThumbnail().trim())
                // .load("https://gizmeon.s.llnwi.net/vod/thumbnails/images/1564037567.png")
                .error(Glide.with(context).load(ContextCompat.getDrawable(context,
                        R.drawable.ic_placeholder)))
                .apply(placeholderOf(R.drawable.ic_placeholder))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.DATA))
                .into(holder.iv_thumbnail);
        Log.d("LOGO",ConstantUtils.CHANNEL_THUMBNAIL + cModel.getThumbnail());
        /*if (cModel.getLiveFlag() != null) {
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

      /*  if (!TextUtils.isEmpty(cModel.get()) && !cModel.getChannelName().equals("")) {
            holder.tv_video_title.setText(cModel.getChannelName());
        }else {
            holder.tv_video_title.setText(context.getResources().getString(R.string.demo_channel));
        }*/
    }

    @Override
    public int getItemCount() {
        return vodToLiveModelList.size();
    }

    public void updateList(List<VodToLiveModel> list){
        vodToLiveModelList = list;
        notifyDataSetChanged();
    }
    public void addAll(List<VodToLiveModel> moveResults) {
        for (VodToLiveModel result : moveResults) {
            add(result);
        }
    }
    public void add(VodToLiveModel r) {
        vodToLiveModelList.add(r);
        notifyItemInserted(vodToLiveModelList.size() - 1);
    }
    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }
    public VodToLiveModel getItem(int position) {
        return vodToLiveModelList.get(position);
    }
    public void remove(VodToLiveModel model) {
        int position = vodToLiveModelList.indexOf(model);
        if (position > -1) {
            vodToLiveModelList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void clearAll() {
        vodToLiveModelList.clear();
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
                    clickObj.onVodItemClicked(getAdapterPosition());
                }
            });
        }
    }

    public interface itemClickListener {
        void onVodItemClicked(int adapterPosition);
    }
}

