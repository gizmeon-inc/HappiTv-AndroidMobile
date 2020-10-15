package com.happi.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.models.VideoModelUpdated;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;
import static com.bumptech.glide.request.RequestOptions.overrideOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class ShowVideoListAdapter extends RecyclerView.Adapter<ShowVideoListAdapter.MyViewHolder>  {

    private List<VideoModelUpdated> videoList;
    private Context context;
    private ShowVideoListAdapter.nestedItemClickListener nestedItemClickListener;
    private int parentPosition;
    private boolean isHome = false;
    private int width = 0;


    public ShowVideoListAdapter(Context context, ShowVideoListAdapter.nestedItemClickListener nestedItemClickListener, int parentPosition, boolean isHome, int width) {
        videoList = new ArrayList<>();
        this.nestedItemClickListener = nestedItemClickListener;
        this.context = context;
        this.parentPosition = parentPosition;
        this.isHome = isHome;
        this.width = width;
    }

    @Override
    public ShowVideoListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {


            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_video_partner, parent, false);
            return new ShowVideoListAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ShowVideoListAdapter.MyViewHolder holder, int position) {

        holder.tv_video_title.setText(videoList.get(position).getVideo_title());
        Glide.with(context)
                .load(ConstantUtils.THUMBNAIL_URL + videoList.get(position).getThumbnail())
                .error(Glide.with(context)
                        .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                .apply(placeholderOf(R.drawable.ic_placeholder))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .apply(fitCenterTransform())
                .apply(overrideOf(400,600))
                .into(holder.iv_thumbnail);
    }

    @Override
    public int getItemCount() {

        return videoList.size();
    }

    public void updateList(List<VideoModelUpdated> list) {
        videoList = list;
        notifyDataSetChanged();
    }

    public void addAll(List<VideoModelUpdated> moveResults) {
        for (VideoModelUpdated result : moveResults) {
            add(result);
        }
    }

    public void add(VideoModelUpdated r) {
        videoList.add(r);
        notifyItemInserted(videoList.size() - 1);
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public VideoModelUpdated getItem(int position) {
        return videoList.get(position);
    }

    public void remove(VideoModelUpdated model) {
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
        FrameLayout ll_main_layout;
        CardView cv_show_parent;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            this.tv_video_title = itemView.findViewById(R.id.tv_video_title);
            this.iv_premium_tag = itemView.findViewById(R.id.iv_premium_tag);
            this.ll_main_layout = itemView.findViewById(R.id.ll_main_layout);
            this.cv_show_parent = itemView.findViewById(R.id.cv_show_parent);

                if (isHome) {

                    int new_width = (width - (width/6))/3;
                    int new_height = (3*(new_width-15))/2;

                    FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(new_width, new_height);
                    CardView.LayoutParams cl = new CardView.LayoutParams(new_width,new_height);
                    cl.rightMargin = 15;
                    this.ll_main_layout.setLayoutParams(fl);
                    this.cv_show_parent.setLayoutParams(cl);

                } else {
                    FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(128, 165);
                    this.ll_main_layout.setLayoutParams(fl);

                }

            tv_video_title.setSelected(true);

            itemView.setOnClickListener(v -> {

                nestedItemClickListener.onNestedItemClicked(getAdapterPosition(), parentPosition);

            });
        }
    }


    public interface nestedItemClickListener {


        void onNestedItemClicked(int nestedPosition, int parentPosition);

    }

}
