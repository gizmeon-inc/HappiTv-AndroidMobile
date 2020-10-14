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
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.models.CategoryWiseShowsModel;
import com.happi.android.models.ShowModel;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

public class PartnerVideoListAdapter extends RecyclerView.Adapter<PartnerVideoListAdapter
        .ItemRowHolder> implements ShowList_adapter.nestedItemClickListener {

    private List<CategoryWiseShowsModel> categoryWiseShowsModels;
    private Context context;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private int width = 0;


    public PartnerVideoListAdapter(List<CategoryWiseShowsModel> categoryWiseShowsModels,
                                   Context context, int width) {
        this.categoryWiseShowsModels = categoryWiseShowsModels;
        this.context = context;
        recycledViewPool = new RecyclerView.RecycledViewPool();
        this.width = width;

    }

    @NonNull
    @Override
    public PartnerVideoListAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_vertical_list, null);
        PartnerVideoListAdapter.ItemRowHolder rowHolder = new ItemRowHolder(v);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PartnerVideoListAdapter.ItemRowHolder holder, int position) {

        final String sectionTitle = categoryWiseShowsModels.get(position).getCategory_name();
        List<ShowModel> videoModelList = new ArrayList<>();

        if (categoryWiseShowsModels.get(position).getVideos().size() >= 10) {
            videoModelList = categoryWiseShowsModels.get(position).getVideos().subList(0,
                    10);
        } else {
            videoModelList = categoryWiseShowsModels.get(position).getVideos();
        }
        holder.tv_title.setText(sectionTitle);

        holder.rv_video_grid.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager
                .HORIZONTAL, false));
        VideoList_adapter videoList_adapter = new VideoList_adapter(context, new VideoList_adapter.nestedItemClickListener() {

            @Override
            public void onNestedItemClicked(int nestedPosition, int parentPosition) {

                PartnerVideoListAdapter.this.onNestedItemClicked(nestedPosition, parentPosition);
            }
        }, position, false, true, width);
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

            ActivityChooser.goToActivity(ConstantUtils.CATEGORYVIEW_ACTIVITY,
                    categoryWiseShowsModels.get(position).getCategory_id() + ";" +
                            categoryWiseShowsModels.get(position).getCategory_name());
            HappiApplication.getCurrentActivity().overridePendingTransition(0, 0);
        });

    }

    @Override
    public int getItemCount() {

        return (null != categoryWiseShowsModels ? categoryWiseShowsModels.size
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


        ActivityChooser.goToActivity(ConstantUtils.SHOW_DETAILS_ACTIVITY,
                categoryWiseShowsModels.get(parentPosition).getVideos().get
                        (nestedPosition).getShow_id());
        // SharedPreferenceUtility.setShowId( categoriesHomeListVideoModelList.get(parentPosition).getVideoModelList().get(nestedPosition).getShow_id());


        HappiApplication.getCurrentActivity().overridePendingTransition(R.anim.fade_in, R.anim
                .fade_out);
    }

}

