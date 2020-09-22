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

import com.happi.android.common.HappiApplication;
import com.happi.android.models.ShowModel;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.happi.android.R;
import com.happi.android.common.ActivityChooser;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.models.CategoriesHomeListVideoModel;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

public class CategoriesHomeListAdapter extends RecyclerView.Adapter<CategoriesHomeListAdapter
        //.ItemRowHolder> implements VideoList_adapter.nestedItemClickListener {
        .ItemRowHolder> implements ShowList_adapter.nestedItemClickListener {

    private List<CategoriesHomeListVideoModel> categoriesHomeListVideoModelList;
    private Context context;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private ICallAdmobAd iCallAdmobAd;



    public CategoriesHomeListAdapter(List<CategoriesHomeListVideoModel> categoriesHomeListVideoModelList,
                                     Context context) {
        this.categoriesHomeListVideoModelList = categoriesHomeListVideoModelList;
        this.context = context;
        this.iCallAdmobAd =((ICallAdmobAd) context);
        recycledViewPool = new RecyclerView.RecycledViewPool();

    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_vertical_list, null);
        ItemRowHolder rowHolder = new ItemRowHolder(v);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, int position) {

        final String sectionTitle = categoriesHomeListVideoModelList.get(position).getCategoryName();
        List<ShowModel> videoModelList = new ArrayList<>();

        if (categoriesHomeListVideoModelList.get(position).getVideoModelList().size() >= 10) {
            videoModelList = categoriesHomeListVideoModelList.get(position).getVideoModelList().subList(0,
                    10);
        } else {
            videoModelList = categoriesHomeListVideoModelList.get(position).getVideoModelList();
        }
        holder.tv_title.setText(sectionTitle);

        holder.rv_video_grid.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager
                .HORIZONTAL, false));
        VideoList_adapter videoList_adapter = new VideoList_adapter(context, new VideoList_adapter.nestedItemClickListener() {

            @Override
            public void onNestedItemClicked(int nestedPosition, int parentPosition) {

                CategoriesHomeListAdapter.this.onNestedItemClicked(nestedPosition, parentPosition);
            }
        }, position, false);
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
        SnapHelper snapHelper;
        snapHelper = new GravitySnapHelper(Gravity.START);
        snapHelper.attachToRecyclerView(holder.rv_video_grid);
        holder.rv_video_grid.setAdapter(videoList_adapter);
        holder.rv_video_grid.setRecycledViewPool(recycledViewPool);

        holder.ll_title.setOnClickListener(view -> {

            ActivityChooser.goToActivity(ConstantUtils.CATEGORYVIEW_ACTIVITY,
                    categoriesHomeListVideoModelList.get(position).getCategoryId() + ";" +
                            categoriesHomeListVideoModelList.get(position).getCategoryName());
            HappiApplication.getCurrentActivity().overridePendingTransition(R.anim.fade_in, R
                    .anim.fade_out);
        });

    }

    @Override
    public int getItemCount() {

        return (null != categoriesHomeListVideoModelList ? categoriesHomeListVideoModelList.size
                () : 0);
    }

    public boolean isEmpty(){
        return getItemCount() == 0;
    }


    public class ItemRowHolder extends RecyclerView.ViewHolder {
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
                categoriesHomeListVideoModelList.get(parentPosition).getVideoModelList().get
                        (nestedPosition).getShow_id());
       // SharedPreferenceUtility.setShowId( categoriesHomeListVideoModelList.get(parentPosition).getVideoModelList().get(nestedPosition).getShow_id());



        HappiApplication.getCurrentActivity().overridePendingTransition(R.anim.fade_in, R.anim
                .fade_out);
    }


    public interface  ICallAdmobAd{
        public void callAdMobShowAdMethod(int videoId);
    }
}

