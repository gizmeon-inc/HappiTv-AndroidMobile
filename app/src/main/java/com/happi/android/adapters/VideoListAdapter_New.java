package com.happi.android.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.models.VideoModel;
import com.happi.android.utils.ConstantUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;
import static com.bumptech.glide.request.RequestOptions.overrideOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class VideoListAdapter_New extends RecyclerView.Adapter<VideoListAdapter_New.MyViewHolder>  {

    private List<VideoModel> videoList;
    private Context context;
    private VideoList_adapter.itemClickListener itemClickListener;
    private VideoList_adapter.nestedItemClickListener nestedItemClickListener;
    private int parentPosition;
    private boolean isNested = false;
    private boolean isVertical = false;

    public VideoListAdapter_New(Context context, VideoList_adapter.itemClickListener itemClickListener, boolean isVertical) {
        videoList = new ArrayList<>();
        this.itemClickListener = itemClickListener;
        this.context = context;
        this.isVertical = isVertical;
        isNested = false;
    }

    public VideoListAdapter_New(Context context, VideoList_adapter.nestedItemClickListener nestedItemClickListener, int parentPosition, boolean isVertical) {
        videoList = new ArrayList<>();
        this.nestedItemClickListener = nestedItemClickListener;
        this.context = context;
        this.parentPosition = parentPosition;
        this.isVertical = isVertical;
        isNested = true;
    }

    @Override
    public VideoListAdapter_New.MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        if (isVertical) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_videos_vertical, parent, false);
            return new VideoListAdapter_New.MyViewHolder(view);
        } else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_videos_horizontal, parent, false);
            return new VideoListAdapter_New.MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(VideoListAdapter_New.MyViewHolder holder, int position) {

        // holder.tv_video_title.setText(videoList.get(position).getShow_name());
        holder.tv_video_title.setText("");
        Glide.with(context)
                .load(ConstantUtils.RELEASE_THUMBNAIL + videoList.get(position).getLogo())
                .error(Glide.with(context)
                        .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                .apply(placeholderOf(R.drawable.ic_placeholder))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .apply(fitCenterTransform())
                .apply(overrideOf(400,600))
                //   .apply(centerCropTransform())
                .into(holder.iv_thumbnail);

        /*if(videoList.get(position).getPremiumFlag()!=null){
            if(videoList.get(position).getPremiumFlag().equals("1")){
                holder.iv_premium_tag.setVisibility(View.INVISIBLE);
            }else{
                holder.iv_premium_tag.setVisibility(View.INVISIBLE);

            }
        }*/
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
        ImageView iv_premium_tag;
        TypefacedTextViewSemiBold tv_video_title;


        public MyViewHolder(View itemView) {
            super(itemView);

            this.iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            this.tv_video_title = itemView.findViewById(R.id.tv_video_title);
            this.iv_premium_tag = itemView.findViewById(R.id.iv_premium_tag);
            tv_video_title.setSelected(true);

            itemView.setOnClickListener(v -> {

                if (!isNested) {

                    itemClickListener.onItemClicked(getAdapterPosition());

                } else {

                    nestedItemClickListener.onNestedItemClicked(getAdapterPosition(), parentPosition);
                }
            });
        }
    }

    public interface itemClickListener {

        void onItemClicked(int adapterPosition);

    }

    public interface nestedItemClickListener {


        void onNestedItemClicked(int nestedPosition, int parentPosition);

    }

}
