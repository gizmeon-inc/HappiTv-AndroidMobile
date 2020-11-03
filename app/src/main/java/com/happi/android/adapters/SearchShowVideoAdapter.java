package com.happi.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.happi.android.R;
import com.happi.android.models.ShowModel;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class SearchShowVideoAdapter extends RecyclerView.Adapter<SearchShowVideoAdapter.MyViewHolder>  {

    public List<ShowModel> showList;
    private Context context;
    private SearchShowVideoAdapter.itemClickListener itemClickListener;
    private   int width = 0;

    //search
    public SearchShowVideoAdapter(Context context, SearchShowVideoAdapter.itemClickListener itemClickListener,int width) {
        showList = new ArrayList<>();
        this.itemClickListener = itemClickListener;
        this.context = context;
        this.width = width;
    }


    @Override
    public SearchShowVideoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {



        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_vertical, parent, false);
        return new SearchShowVideoAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(SearchShowVideoAdapter.MyViewHolder holder, int position) {

        String thumbnailUrl = "";

        if(showList.get(position).getVideo_id() == null){
            //it's a show
            if(showList.get(position).getLogo() != null){
                thumbnailUrl = ConstantUtils.RELEASE_THUMBNAIL + showList.get(position).getLogo();
            }else{
                thumbnailUrl = "";
            }


            if (showList.get(position).getShow_name() != null && !showList.get(position).getShow_name().isEmpty()) {
                holder.tv_video_name.setVisibility(View.VISIBLE);
                holder.tv_video_name.setText(showList.get(position).getShow_name().trim());
            } else {

                holder.tv_video_name.setVisibility(View.INVISIBLE);

            }
        }else{
            // it's a video
            if(showList.get(position).getThumbnail() != null){
                thumbnailUrl = ConstantUtils.THUMBNAIL_URL + showList.get(position).getThumbnail();
            }else{
                thumbnailUrl = "";
            }
            if (showList.get(position).getVideo_title() != null && !showList.get(position).getVideo_title().isEmpty()) {
                holder.tv_video_name.setVisibility(View.VISIBLE);
                holder.tv_video_name.setText(showList.get(position).getVideo_title().trim());
            } else {
                if (showList.get(position).getShow_name() != null && !showList.get(position).getShow_name().isEmpty()) {
                    holder.tv_video_name.setVisibility(View.VISIBLE);
                    holder.tv_video_name.setText(showList.get(position).getShow_name().trim());
                } else {
                    holder.tv_video_name.setVisibility(View.INVISIBLE);
                }
            }
        }


        holder.iv_thumbnail.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(context)
                .load(thumbnailUrl)
                .error(Glide.with(context)
                        .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                .apply(placeholderOf(R.drawable.ic_placeholder))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
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
        CardView cv_show_parent;

        TextView tv_video_name;
        LinearLayout ll_video_name_parent;

        FrameLayout fl_main_layout;


        public MyViewHolder(View itemView) {
            super(itemView);

            this.iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);

            this.cv_show_parent = itemView.findViewById(R.id.cv_show_parent);

            this.tv_video_name = itemView.findViewById(R.id.tv_video_name);
            this.ll_video_name_parent = itemView.findViewById(R.id.ll_video_name_parent);

            this.fl_main_layout = itemView.findViewById(R.id.fl_main_layout);

           /* //int new_width = (width - (width/6))/3;
            int new_width = (width)/3;;
            int new_height = (3*(new_width-15))/2;


            LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(new_width, new_height);
            fl_main_layout.setLayoutParams(fp);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(new_width, new_height);
            iv_thumbnail.setLayoutParams(lp);

            int textWidth = new_width - (int)(context.getResources().getDimension(R.dimen.dimen_2dp));
            LinearLayout.LayoutParams tvp = new LinearLayout.LayoutParams(new_width, LinearLayout.LayoutParams.WRAP_CONTENT);
            ll_video_name_parent.setLayoutParams(tvp);
            ll_video_name_parent.setPadding(2,0,2,0);*/

            int new_width = (width/3);
            int new_height = (3*(new_width-15))/2;
            LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, new_height);
            fl_main_layout.setLayoutParams(fp);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, new_height);
            iv_thumbnail.setLayoutParams(lp);

            this.tv_video_name.setVisibility(View.INVISIBLE);


            itemView.setOnClickListener(v -> {

                itemClickListener.onSearchShowVideoItemClicked(getAdapterPosition());


            });
        }
    }

    public interface itemClickListener {

        void onSearchShowVideoItemClicked(int adapterPosition);

    }


}


