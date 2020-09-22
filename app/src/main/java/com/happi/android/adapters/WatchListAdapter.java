package com.happi.android.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.models.GetWatchListModel;
import com.happi.android.utils.ConstantUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class WatchListAdapter extends RecyclerView.Adapter<WatchListAdapter.ViewHolder> {

    private List<GetWatchListModel> watchListModelList;
    private Context context;
    private OnItemClicked onItemClicked;
    private OnLongItemclicked onLongItemclicked;
    private String pageContext;
    private int position = -1;


    public WatchListAdapter(String pageContext, List<GetWatchListModel> watchListModelList, Context context, OnItemClicked onItemClicked, OnLongItemclicked onLongItemclicked){
        this.watchListModelList = watchListModelList;
        this.context = context;
        this.onItemClicked = onItemClicked;
        this.onLongItemclicked = onLongItemclicked;
        this.pageContext = pageContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_show_list_watch_fav, viewGroup, false);
            return new WatchListAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if(watchListModelList.size() != 0){
           // position = viewHolder.getAdapterPosition();
            viewHolder.ll_delete.setVisibility(View.GONE);
            Glide.with(context)
                    .load(ConstantUtils.RELEASE_THUMBNAIL + watchListModelList.get(i).getLogo())
                    .error(Glide.with(context)
                            .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                    .apply(placeholderOf(R.drawable.ic_placeholder))
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                    .apply(fitCenterTransform())
                    .into(viewHolder.iv_thumbnail);
            viewHolder.tv_video_title.setText("");
            watchListModelList.get(i).setSelected(false);

        }

    }

    @Override
    public int getItemCount() {
        return watchListModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_thumbnail;
        ImageView iv_delete_icon;
        TypefacedTextViewSemiBold tv_video_title;
        LinearLayout ll_delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            iv_delete_icon = itemView.findViewById(R.id.iv_delete_icon);
            tv_video_title = itemView.findViewById(R.id.tv_video_title);
            ll_delete = itemView.findViewById(R.id.ll_delete);
           // ll_delete.setVisibility(View.GONE);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  int pos = getAdapterPosition();
                    if(ll_delete.getVisibility() == View.VISIBLE){
                        ll_delete.setVisibility(View.GONE);
                        watchListModelList.get(getAdapterPosition()).setSelected(false);
                    }else{
                        onItemClicked.onShowItemClicked(getAdapterPosition());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(pageContext.equalsIgnoreCase("Watch List")){
                        ll_delete.setVisibility(View.VISIBLE);
                        watchListModelList.get(getAdapterPosition()).setSelected(true);
                    }
                    // onLongItemclicked.onShowItemLongClick();
                    return true;
                }
            });
            iv_delete_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLongItemclicked.onShowItemLongClick(getAdapterPosition());
                }
            });
        }
    }
    public void addAll(List<GetWatchListModel> moveResults) {
        for (GetWatchListModel result : moveResults) {
            add(result);
        }
    }
    public void add(GetWatchListModel r) {
        watchListModelList.add(r);
        notifyItemInserted(watchListModelList.size() - 1);
    }
    public void remove(GetWatchListModel model) {
        int position = watchListModelList.indexOf(model);
        if (position > -1) {
            watchListModelList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void removeItem(int position){
        if(position > -1){
            watchListModelList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearAll() {
        watchListModelList.clear();
        notifyDataSetChanged();
    }
    public GetWatchListModel getItem(int position) {
        return watchListModelList.get(position);
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
    public interface OnLongItemclicked{
        public void onShowItemLongClick(int adapterPosition);
    }
}
