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
import com.happi.android.models.ShowsResponseModel;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class ShowsAdapter extends RecyclerView.Adapter<ShowsAdapter.MyViewHolder> {

    private List<ShowsResponseModel.ShowData> showData;
    private Context context;
    private itemClickListener itemClickListener;
    private boolean isVertical;

    public ShowsAdapter(Context context, itemClickListener itemClickListener, boolean isVertical) {
        showData = new ArrayList<>();
        this.itemClickListener = itemClickListener;
        this.context = context;
        this.isVertical = isVertical;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        if (isVertical) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_shows_vertical, parent, false);
            return new MyViewHolder(view);
        } else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_shows_horizontal, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_video_title.setText(showData.get(position).getShow_name());
        Glide.with(context)
                .load(ConstantUtils.SHOWS_THUMBNAIL + showData.get(position).getLogo())
                .error(Glide.with(context)
                        .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                .apply(placeholderOf(R.drawable.ic_placeholder))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .apply(centerCropTransform())
                .into(holder.iv_thumbnail);

        if(showData.get(position).getPremiumFlag()!=null){
            if(showData.get(position).getPremiumFlag().equals("1")){
                holder.iv_premium_tag.setVisibility(View.VISIBLE);
            }else{
                holder.iv_premium_tag.setVisibility(View.INVISIBLE);

            }
        }

    }

    @Override
    public int getItemCount() {

        return showData.size();
    }

    public void updateList(List<ShowsResponseModel.ShowData> list) {
        showData = list;
        notifyDataSetChanged();
    }

    public void addAll(List<ShowsResponseModel.ShowData> moveResults) {
        for (ShowsResponseModel.ShowData result : moveResults) {
            add(result);
        }
    }

    public void add(ShowsResponseModel.ShowData r) {
        showData.add(r);
        notifyItemInserted(showData.size() - 1);
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public ShowsResponseModel.ShowData getItem(int position) {
        return showData.get(position);
    }

    public void remove(ShowsResponseModel.ShowData model) {
        int position = showData.indexOf(model);
        if (position > -1) {
            showData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearAll() {
        showData.clear();
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
            this.tv_video_title = itemView.findViewById(R.id.tv_show_title);
            this.iv_premium_tag = itemView.findViewById(R.id.iv_premium_tag);
            tv_video_title.setSelected(true);

            itemView.setOnClickListener(v -> {

                itemClickListener.onShowItemClicked(getAdapterPosition());
            });
        }
    }

    public interface itemClickListener {

        void onShowItemClicked(int adapterPosition);
    }
}
