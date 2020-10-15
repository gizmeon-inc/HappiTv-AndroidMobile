package com.happi.android.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.happi.android.R;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.models.PartnerShowsModel;
import com.happi.android.models.ShowModel;
import com.happi.android.models.VideoModelUpdated;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

public class PartnerVideoListAdapter extends RecyclerView.Adapter<PartnerVideoListAdapter
        .ItemRowHolder> implements ShowList_adapter.nestedItemClickListener {

    private List<PartnerShowsModel> partnerShowsModels;
    private Context context;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private int width = 0;


    public PartnerVideoListAdapter(List<PartnerShowsModel> partnerShowsModels,
                                   Context context, int width) {
        this.partnerShowsModels = partnerShowsModels;
        this.context = context;
        recycledViewPool = new RecyclerView.RecycledViewPool();
        this.width = width;

    }

    @NonNull
    @Override
    public PartnerVideoListAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partner_video_list, null);
        PartnerVideoListAdapter.ItemRowHolder rowHolder = new ItemRowHolder(v);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PartnerVideoListAdapter.ItemRowHolder holder, int position) {

        final String sectionTitle = partnerShowsModels.get(position).getShow_name();
        List<VideoModelUpdated> videoModelList = new ArrayList<>();

        if (partnerShowsModels.get(position).getVideoModelUpdatedList().size() >= 10) {
            videoModelList = partnerShowsModels.get(position).getVideoModelUpdatedList().subList(0,
                    10);
        } else {
            videoModelList = partnerShowsModels.get(position).getVideoModelUpdatedList();
        }
        holder.tv_title.setText(sectionTitle);

        holder.rv_video_grid.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager
                .HORIZONTAL, false));

        ShowVideoListAdapter videoList_adapter = new ShowVideoListAdapter(context, new ShowVideoListAdapter.nestedItemClickListener() {

            @Override
            public void onNestedItemClicked(int nestedPosition, int parentPosition) {

                PartnerVideoListAdapter.this.onNestedItemClicked(nestedPosition, parentPosition);
            }
        }, position,  true, width);
        /*holder.rv_video_grid.setAdapter(videoList_adapter);*/

        SkeletonScreen loadingVideos = Skeleton.bind(holder.rv_video_grid)
                .adapter(videoList_adapter)
                .load(R.layout.loading_videos_horizontal)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();

        videoList_adapter.clearAll();
        videoList_adapter.addAll(videoModelList);
        //loadingVideos.hide();
        SnapHelper snapHelper;
        snapHelper = new GravitySnapHelper(Gravity.START);
        snapHelper.attachToRecyclerView(holder.rv_video_grid);
        holder.rv_video_grid.setAdapter(videoList_adapter);
        holder.rv_video_grid.setRecycledViewPool(recycledViewPool);

        holder.ll_title.setOnClickListener(view -> {

            SharedPreferenceUtility.setShowId(partnerShowsModels.get(position).getShow_id());
            ActivityChooser.goToActivity(ConstantUtils.SHOW_DETAILS_ACTIVITY, partnerShowsModels.get(position).getShow_id());

            HappiApplication.getCurrentActivity().overridePendingTransition(0, 0);
        });

    }

    @Override
    public int getItemCount() {

        return (null != partnerShowsModels ? partnerShowsModels.size
                () : 0);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public static class ItemRowHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_title;
        TypefacedTextViewSemiBold tv_title;
        RecyclerView rv_video_grid;

        public ItemRowHolder(View itemView) {

            super(itemView);
            this.ll_title = itemView.findViewById(R.id.ll_title);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.rv_video_grid = itemView.findViewById(R.id.rv_video_grid);
        }
    }

    @Override
    public void onNestedItemClicked(int nestedPosition, int parentPosition) {
        SharedPreferenceUtility.setVideoId(partnerShowsModels.get(parentPosition).getVideoModelUpdatedList().get
                (nestedPosition).getVideo_id());
        ActivityChooser.goToActivity(ConstantUtils.VIDEO_PLAYER_ACTIVITY, partnerShowsModels.get(parentPosition).getVideoModelUpdatedList().get
                (nestedPosition).getVideo_id());

        HappiApplication.getCurrentActivity().overridePendingTransition(0,0);
    }

}

