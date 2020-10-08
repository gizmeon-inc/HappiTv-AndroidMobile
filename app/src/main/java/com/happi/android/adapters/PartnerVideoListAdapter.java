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
import com.happi.android.models.PartnerVideoListResponseModel;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;
import static com.bumptech.glide.request.RequestOptions.overrideOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class PartnerVideoListAdapter extends RecyclerView.Adapter<PartnerVideoListAdapter.MyViewHolder>  {

    private List<PartnerVideoListResponseModel.PartnerVideoModel> partnerVideoModelList;
    private Context context;
    private PartnerVideoItemClickListener partnerVideoItemClickListener;

    public PartnerVideoListAdapter(Context context, PartnerVideoItemClickListener itemClickListener) {

        partnerVideoModelList = new ArrayList<>();
        this.partnerVideoItemClickListener = itemClickListener;
        this.context = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_videos_vertical, parent, false);
            return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_video_title.setText("");
        Glide.with(context)
                .load(ConstantUtils.THUMBNAIL_URL + partnerVideoModelList.get(position).getLogo().trim())
                .error(Glide.with(context)
                        .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                .apply(placeholderOf(R.drawable.ic_placeholder))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .apply(fitCenterTransform())
                .apply(overrideOf(400,600))
                //   .apply(centerCropTransform())
                .into(holder.iv_thumbnail);

    }

    @Override
    public int getItemCount() {

        return partnerVideoModelList.size();
    }

    public void updateList(List<PartnerVideoListResponseModel.PartnerVideoModel> list) {
        partnerVideoModelList = list;
        notifyDataSetChanged();
    }

    public void addAll(List<PartnerVideoListResponseModel.PartnerVideoModel> moveResults) {
        for (PartnerVideoListResponseModel.PartnerVideoModel result : moveResults) {
            add(result);
        }
    }

    public void add(PartnerVideoListResponseModel.PartnerVideoModel r) {
        partnerVideoModelList.add(r);
        notifyItemInserted(partnerVideoModelList.size() - 1);
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public PartnerVideoListResponseModel.PartnerVideoModel getItem(int position) {
        return partnerVideoModelList.get(position);
    }

    public void remove(PartnerVideoListResponseModel.PartnerVideoModel model) {
        int position = partnerVideoModelList.indexOf(model);
        if (position > -1) {
            partnerVideoModelList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearAll() {
        partnerVideoModelList.clear();
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


            tv_video_title.setSelected(true);

            itemView.setOnClickListener(v -> {
                partnerVideoItemClickListener.onPartnerVideoItemClicked(getAdapterPosition());

            });
        }
    }

    public interface PartnerVideoItemClickListener {

        void onPartnerVideoItemClicked(int adapterPosition);

    }


}

