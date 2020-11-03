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
import com.happi.android.models.ShowModel;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class ShowList_adapter extends RecyclerView.Adapter<ShowList_adapter.MyViewHolder> {

    public List<ShowModel> showList;
    private Context context;
    private ShowList_adapter.itemClickListener itemClickListener;
    private ShowList_adapter.nestedItemClickListener nestedItemClickListener;
    private int parentPosition;
    private boolean isNested = false;
    private boolean isVertical = false;

    private int width;

    public ShowList_adapter(Context context, ShowList_adapter.itemClickListener itemClickListener, boolean isVertical, int width) {
        showList = new ArrayList<>();
        this.itemClickListener = itemClickListener;
        this.context = context;
        this.isVertical = isVertical;
        isNested = false;

        this.width = width;
    }

    public ShowList_adapter(Context context, ShowList_adapter.nestedItemClickListener nestedItemClickListener, int parentPosition, boolean isVertical) {
        showList = new ArrayList<>();
        this.nestedItemClickListener = nestedItemClickListener;
        this.context = context;
        this.parentPosition = parentPosition;
        this.isVertical = isVertical;
        isNested = true;
    }

    @Override
    public ShowList_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        if (isVertical) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_videos_vertical, parent, false);
            return new ShowList_adapter.MyViewHolder(view);
        } else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_videos_horizontal, parent, false);
            return new ShowList_adapter.MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ShowList_adapter.MyViewHolder holder, int position) {

        holder.tv_video_title.setText("");
        Glide.with(context)
                // .load(ConstantUtils.RELEASE_THUMBNAIL + showList.get(position).getLogo())
                .load(ConstantUtils.RELEASE_THUMBNAIL + showList.get(position).getLogo())
                .error(Glide.with(context)
                        .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                .apply(placeholderOf(R.drawable.ic_placeholder))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .apply(fitCenterTransform())
                //  .apply(overrideOf(400,600))
                //   .apply(centerCropTransform())
                .into(holder.iv_thumbnail);

    }

    @Override
    public int getItemCount() {

        return showList.size();
    }


    public void updateList(List<ShowModel> list) {
        showList = list;
        notifyDataSetChanged();
    }

    public void addAll(List<ShowModel> moveResults) {
        for (ShowModel result : moveResults) {
            add(result);
        }
    }

    public void add(ShowModel r) {
        showList.add(r);
        notifyItemInserted(showList.size() - 1);
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public ShowModel getItem(int position) {
        return showList.get(position);
    }

    public void remove(ShowModel model) {
        int position = showList.indexOf(model);
        if (position > -1) {
            showList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearAll() {
        showList.clear();
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


        public MyViewHolder(View itemView) {
            super(itemView);

            this.iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            this.tv_video_title = itemView.findViewById(R.id.tv_video_title);
            this.iv_premium_tag = itemView.findViewById(R.id.iv_premium_tag);

            this.ll_main_layout = itemView.findViewById(R.id.ll_main_layout);


            if(isVertical){
                int new_width = (width/3);
                int new_height = (3*(new_width-15))/2;

                FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, new_height);
                this.ll_main_layout.setLayoutParams(fl);
                this.iv_thumbnail.setLayoutParams(fl);
            }


            tv_video_title.setSelected(true);

            itemView.setOnClickListener(v -> {

                if (!isNested) {

                    itemClickListener.onShowsItemClicked(getAdapterPosition());

                } else {

                    nestedItemClickListener.onNestedItemClicked(getAdapterPosition(), parentPosition);
                }
            });
        }
    }

    public interface itemClickListener {

        void onShowsItemClicked(int adapterPosition);

    }

    public interface nestedItemClickListener {


        void onNestedItemClicked(int nestedPosition, int parentPosition);

    }

}

