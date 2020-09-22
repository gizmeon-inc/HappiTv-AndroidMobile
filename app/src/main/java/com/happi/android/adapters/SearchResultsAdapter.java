package com.happi.android.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.models.VideoModel;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.MyViewHolder> {

    private List<VideoModel> videoList;
    private Context context;
    private itemClickListener clickObj;
    private boolean isVertical = false;


    public SearchResultsAdapter(Context context, itemClickListener clickObj, boolean isVertical) {
        videoList = new ArrayList<>();
        this.clickObj = clickObj;
        this.context = context;
        this.isVertical = isVertical;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        if (isVertical) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_videos_vertical, parent, false);
            return new MyViewHolder(view);
        } else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_videos_horizontal, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_video_title.setText(videoList.get(position).getVideo_title());
        Glide.with(context)
                .load(ConstantUtils.THUMBNAIL_URL + videoList.get(position).getThumbnail())
                .error(Glide.with(context)
                        .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                .apply(placeholderOf(R.drawable.ic_placeholder))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .apply(centerCropTransform())
                .into(holder.iv_thumbnail);
        if(videoList.get(position).getPremiumFlag()!=null){
            if(videoList.get(position).getPremiumFlag().equals("1")){
                holder.iv_premium_tag.setVisibility(View.VISIBLE);
            }else{
                holder.iv_premium_tag.setVisibility(View.INVISIBLE);

            }
        }
    }

    @Override
    public int getItemCount() {

        return videoList.size();
    }

    public void updateList(List<VideoModel> list) {
        videoList = list;
        notifyDataSetChanged();
    }

    public void addAll(List<VideoModel> moveResults) {
        for (VideoModel result : moveResults) {
            add(result);
        }
    }

    public void add(VideoModel r) {
        videoList.add(r);
        notifyItemInserted(videoList.size() - 1);
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public VideoModel getItem(int position) {
        return videoList.get(position);
    }

    public void remove(VideoModel model) {
        int position = videoList.indexOf(model);
        if (position > -1) {
            videoList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearAll() {
        videoList.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_thumbnail;
        TypefacedTextViewSemiBold tv_video_title;
        ImageView iv_premium_tag;


        public MyViewHolder(View itemView) {
            super(itemView);

            this.iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            this.tv_video_title = itemView.findViewById(R.id.tv_video_title);
            this.iv_premium_tag = itemView.findViewById(R.id.iv_premium_tag);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickObj.onSearchItemClicked(getAdapterPosition());
                }
            });
        }

    }

    public interface itemClickListener {
        void onSearchItemClicked(int adapterPosition);
    }
}

