package com.happi.android.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
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
import com.happi.android.models.GetPayperviewVideoListModel;
import com.happi.android.utils.ConstantUtils;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class PayPerViewVideoListAdapter extends RecyclerView.Adapter<PayPerViewVideoListAdapter.ViewHolder> {

    private List<GetPayperviewVideoListModel> payperviewVideoModelList;
    private Context context;
    private OnItemClicked onItemClicked;
    private int position = -1;


    public PayPerViewVideoListAdapter(List<GetPayperviewVideoListModel> payPerViewVideoList, Context context, OnItemClicked onItemClicked){
        this.payperviewVideoModelList = payPerViewVideoList;
        this.context = context;
        this.onItemClicked = onItemClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_show_list_watch_fav, viewGroup, false);
            return new PayPerViewVideoListAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if(payperviewVideoModelList.size() != 0){
            Glide.with(context)
                    //.load(ConstantUtils.RELEASE_THUMBNAIL + payperviewVideoModelList.get(i).getThumbnail())
                    .load(ConstantUtils.THUMBNAIL_URL + payperviewVideoModelList.get(i).getThumbnail())
                    .error(Glide.with(context)
                            .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                    .apply(placeholderOf(R.drawable.ic_placeholder))
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                    .apply(fitCenterTransform())
                    .into(viewHolder.iv_thumbnail);
            viewHolder.tv_video_title.setText("");
            payperviewVideoModelList.get(i).setSelected(false);

        }

    }

    @Override
    public int getItemCount() {
        return payperviewVideoModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_thumbnail;
        TypefacedTextViewSemiBold tv_video_title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tv_video_title = itemView.findViewById(R.id.tv_video_title);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  int pos = getAdapterPosition();
                        onItemClicked.onShowItemClicked(getAdapterPosition());
                }
            });

        }
    }
    public void addAll(List<GetPayperviewVideoListModel> moveResults) {
        for (GetPayperviewVideoListModel result : moveResults) {
            add(result);
        }
    }
    public void add(GetPayperviewVideoListModel r) {
        payperviewVideoModelList.add(r);
        notifyItemInserted(payperviewVideoModelList.size() - 1);
    }
    public void remove(GetPayperviewVideoListModel model) {
        int position = payperviewVideoModelList.indexOf(model);
        if (position > -1) {
            payperviewVideoModelList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void removeItem(int position){
        if(position > -1){
            payperviewVideoModelList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearAll() {
        payperviewVideoModelList.clear();
        notifyDataSetChanged();
    }
    public GetPayperviewVideoListModel getItem(int position) {
        return payperviewVideoModelList.get(position);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public interface OnItemClicked{
        public void onShowItemClicked(int adapterPosition);

    }
}
