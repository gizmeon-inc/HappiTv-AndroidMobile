package com.happi.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.happi.android.R;
import com.happi.android.ShowDetailsActivity;
import com.happi.android.models.VideoModelUpdated;

import java.util.List;

public class ShowDetailsAdapter extends RecyclerView.Adapter<ShowDetailsAdapter.ViewHolder> {

    private itemClickListener thisItemClickListener;
    private Context context;
    List<VideoModelUpdated> videoModelUpdatedList;
    private int count = 0;
    private ShowDetailsActivity activity;
    public int row_index = 0;

    public ShowDetailsAdapter(Context context, ShowDetailsActivity activity, itemClickListener thisItemClickListener, List<VideoModelUpdated> videoModelUpdatedList){

        this.context = context;
        this.thisItemClickListener = thisItemClickListener;
        this.videoModelUpdatedList = videoModelUpdatedList;
        this.activity = activity;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout ll_item_show;
        LinearLayout ll_play;
        LinearLayout ll_container;
        TextView tv_show_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ll_item_show = itemView.findViewById(R.id.ll_item_show);
            this.ll_play = itemView.findViewById(R.id.ll_play);
            this.tv_show_name = itemView.findViewById(R.id.tv_show_name);
            this.ll_container = itemView.findViewById(R.id.ll_container);

            this.ll_item_show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    thisItemClickListener.onFeaturedShowItemClicked(getAdapterPosition());
                    row_index = getAdapterPosition();
//                    activity.scrollPosition = getAdapterPosition();
//                    activity.recyclerViewState = activity.rv_show_list.getLayoutManager().onSaveInstanceState();
                    notifyDataSetChanged();
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_show_list, viewGroup, false);

        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if(videoModelUpdatedList.size() != 0){
            String position = String.valueOf(i+1);
            viewHolder.tv_show_name.setText(position+". "+videoModelUpdatedList.get(i).getVideo_title());

            if(videoModelUpdatedList.size() > 1){
                if (row_index == i) {
                    viewHolder.ll_container.setBackgroundResource(R.drawable.bg_outline_blue);
                } else {
                    viewHolder.ll_container.setBackgroundResource(R.drawable.bg_outline_transparent);
                }
            }

        }
        activity.progressDialogDismiss();
    }

    @Override
    public int getItemCount() {
        return videoModelUpdatedList.size();
    }
    public void setSelected(int index) {
        row_index = index;
    }

    public VideoModelUpdated getItem(int position){
        return videoModelUpdatedList.get(position);
    }


    public interface itemClickListener {
        void onFeaturedShowItemClicked(int adapterPosition);
    }

}
