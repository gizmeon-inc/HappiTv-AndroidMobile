package com.happi.android.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.happi.android.models.FeaturedShowsModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.happi.android.R;
import com.happi.android.models.BannerModel;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;
import static com.bumptech.glide.request.RequestOptions.overrideOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class BannerAdapter extends PagerAdapter {

    private Context mContext;
   // private List<VideoModel> videoList;
    private List<FeaturedShowsModel> videoList;
    private itemClickListener clickObj;

    public BannerAdapter(Context context, itemClickListener clickObj) {
        mContext = context;
        this.clickObj = clickObj;
        videoList = new ArrayList<>();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        BannerModel modelObject = BannerModel.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);

        ImageView banner;
        ImageView iv_premium_tag;
        //TypefacedTextViewSemiBold tv_banner;
        banner = layout.findViewById(R.id.iv_banner);
        //tv_banner = layout.findViewById(R.id.tv_banner);
        iv_premium_tag = layout.findViewById(R.id.iv_premium_tag);

        if (videoList != null && videoList.size() != 0) {
           // tv_banner.setText(videoList.get(position).getVideo_title());
            //tv_banner.setText("");
            //tv_banner.setSelected(true);
            Glide.with(mContext)
                    .load(ConstantUtils.THUMBNAIL_URL + videoList.get(position).getBanner2())
                    .error(Glide.with(mContext)
                            .load(ContextCompat.getDrawable(mContext, R.drawable.ic_placeholder)))
                    .apply(placeholderOf(R.drawable.ic_placeholder))
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                    .apply(fitCenterTransform())
                    .apply(overrideOf(1920,1357))
                  //  .apply(centerCropTransform())
                    .into(banner);
            if(videoList.get(position).getPremium_flag()!=null){
                if(videoList.get(position).getPremium_flag().equals("1")){
                     iv_premium_tag.setVisibility(View.INVISIBLE);
                }else{
                     iv_premium_tag.setVisibility(View.INVISIBLE);

                }
            }
        }



        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickObj.onBannerItemClicked(position);
            }
        });

        collection.addView(layout);
        return layout;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {

        return videoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //BannerModel customPagerEnum = BannerModel.values()[position];
        return null;
    }


//    public VideoModel getItem(int position) {
//        return videoList.get(position);
//    }
    public FeaturedShowsModel getItem(int position) {
        return videoList.get(position);
    }


   // public void addAll(List<VideoModel> videoList) {
    public void addAll(List<FeaturedShowsModel> videoList) {
        this.videoList = videoList;

    }

    public boolean isEmpty(){
        return getCount() == 0;
    }

    public interface itemClickListener {

        void onBannerItemClicked(int adapterPosition);
    }
}