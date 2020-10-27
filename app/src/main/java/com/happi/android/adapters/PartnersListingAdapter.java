package com.happi.android.adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.happi.android.R;
import com.happi.android.customviews.AdaptingTextView;
import com.happi.android.customviews.TypefacedTextViewBold;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.CategoryModel;
import com.happi.android.models.PartnerResponseModel;
import com.happi.android.utils.ConstantUtils;


import java.util.ArrayList;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;


/*
public class PartnersListingAdapter extends RecyclerView.Adapter<PartnersListingAdapter.LivePartnerViewHolder> {
    private Context context;
    private List<PartnerResponseModel.PartnerModel> partnerModelList;
    private boolean isHome;
    private PartnerItemClickListener partnerItemClickListener;

    public PartnersListingAdapter(Context context, boolean isHome, PartnerItemClickListener partnerItemClickListener) {
        this.partnerModelList = new ArrayList<>();
        this.context = context;
        this.isHome = isHome;
        this.partnerItemClickListener = partnerItemClickListener;
    }

    @NonNull
    @Override
    public LivePartnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isHome) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partner_info_home, null);
            return new LivePartnerViewHolder(view);
        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partner_info_list, null);
            return new LivePartnerViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull LivePartnerViewHolder holder, int position) {

        if (partnerModelList.size() != 0) {
           holder.tv_partner_name.setText(partnerModelList.get(position).getName());
           holder.tv_partner_description.setText(partnerModelList.get(position).getDescription());
            Glide.with(context)
                    .load(ConstantUtils.THUMBNAIL_URL + partnerModelList.get(position).getImage())
                    .error(Glide.with(context)
                            .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                    .apply(placeholderOf(R.drawable.ic_placeholder))
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                    .into(holder.iv_partner_image);
        }
    }





    @Override
    public int getItemCount() {
        return partnerModelList.size();
    }

    public class LivePartnerViewHolder extends RecyclerView.ViewHolder {
        TypefacedTextViewRegular tv_partner_description;
        TypefacedTextViewBold tv_partner_name;
        ImageView iv_partner_image;

        public LivePartnerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_partner_image = itemView.findViewById(R.id.iv_partner_image);
            this.tv_partner_name = itemView.findViewById(R.id.tv_partner_name);
            this.tv_partner_description = itemView.findViewById(R.id.tv_partner_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    partnerItemClickListener.onPartnerItemClicked(getAdapterPosition());
                }
            });
        }
    }

    public void clearAll() {
        partnerModelList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<PartnerResponseModel.PartnerModel> moveResults) {
        for (PartnerResponseModel.PartnerModel result : moveResults) {
            add(result);
        }
    }

    public void add(PartnerResponseModel.PartnerModel r) {
        partnerModelList.add(r);
        notifyItemInserted(partnerModelList.size() - 1);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public PartnerResponseModel.PartnerModel getItem(int position) {
        return partnerModelList.get(position);
    }

    public interface PartnerItemClickListener{
        void onPartnerItemClicked(int adapterPosition);
    }
}
*/


public class PartnersListingAdapter extends RecyclerView.Adapter<PartnersListingAdapter.LivePartnerViewHolder> {
    private Context context;
    private List<PartnerResponseModel.PartnerModel> partnerModelList;
    public boolean isHome;
    private PartnerItemClickListener partnerItemClickListener;
    private int width = 0;
    public PartnersListingAdapter(Context context, boolean isHome, PartnerItemClickListener partnerItemClickListener, int width) {
        this.partnerModelList = new ArrayList<>();
        this.context = context;
        this.isHome = isHome;
        this.partnerItemClickListener = partnerItemClickListener;
        this.width = width;
    }
    @NonNull
    @Override
    public LivePartnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*if (isHome) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partner_info_home, null);
            return new LivePartnerViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partner_info_list, null);
            return new LivePartnerViewHolder(view);
        }*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partner_info_home, null);
        return new LivePartnerViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull LivePartnerViewHolder holder, int position) {
        if (partnerModelList.size() != 0) {

            Glide.with(context)
                    .load(ConstantUtils.THUMBNAIL_URL + partnerModelList.get(position).getImage())
                    .error(Glide.with(context)
                            .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                    .apply(placeholderOf(R.drawable.ic_placeholder))
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                    .into(holder.iv_partner_image);

            holder.tv_partner_name.setText(partnerModelList.get(position).getName());

            if(partnerModelList.get(position).getDescription() != null && !partnerModelList.get(position).getDescription().isEmpty()){
                String description = partnerModelList.get(position).getDescription();
                if(description.contains("\r\n")){
                    description = description.replace("\r\n"," ");
                }


                holder.tv_partner_description.setText(description);

            }else{
                holder.tv_partner_description.setText("");
            }

        }
    }
    @Override
    public int getItemCount() {
        return partnerModelList.size();
    }
    public class LivePartnerViewHolder extends RecyclerView.ViewHolder {
        //TypefacedTextViewRegular tv_partner_description;
        AdaptingTextView tv_partner_description;
        TypefacedTextViewBold tv_partner_name;
        ImageView iv_partner_image;
        RelativeLayout rl_partner_parent;
        LinearLayout ll_partner_image;
        public LivePartnerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_partner_image = itemView.findViewById(R.id.iv_partner_image);
            this.tv_partner_name = itemView.findViewById(R.id.tv_partner_name);
            this.tv_partner_description = itemView.findViewById(R.id.tv_partner_description);
            this.rl_partner_parent = itemView.findViewById(R.id.rl_partner_parent);
            this.ll_partner_image = itemView.findViewById(R.id.ll_partner_image);


            int widthModified = (width/2) - ((int) context.getResources().getDimensionPixelSize(R.dimen.default_spacing_2dp));
            //int wd = (width - ((int)(width * 1.38)/100));
            //int image_width = (int) ((0.7 * width)/4);
            int image_width = (int) ((widthModified)/4);
            int new_height = (3 * (image_width)) / 2;
            int margin = (int) context.getResources().getDimension(R.dimen.dimen_2dp);

            //int viewHeight = ((int) context.getResources().getDimensionPixelSize(R.dimen.dimen_82dp));
            int viewHeight = ((int) context.getResources().getDimensionPixelSize(R.dimen.dimen_100dp));
            Log.e("PARTNER","viewHeight"+viewHeight);

            //int widthModified = (int) width-15;
            //int widthModified = (int) (width - ((width * 2.083)/100));
            //int widthModified = (int) (width - ((width * 2.77)/100));
            //int widthModified = (int) (width - ((width * 3.47)/100));
            //int widthModified = (int) (width - ((width * 3.88)/100));
            //int widthModified = (int) (width /2);
            //int widthModified = wd ;


            Log.e("PARTNER","width"+width);
            Log.e("PARTNER","widthModified"+widthModified);
            //Log.e("PARTNER","modification="+(width * 3.88)/100);
           // Log.e("PARTNER","modification="+(width * 1.38)/100);
            Log.e("PARTNER","image_width"+image_width);
            Log.e("PARTNER","new_height"+new_height);

            CardView.LayoutParams cl = new CardView.LayoutParams(widthModified, new_height);
            //CardView.LayoutParams cl = new CardView.LayoutParams(widthModified, viewHeight);
            this.rl_partner_parent.setLayoutParams(cl);

            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(image_width, RelativeLayout.LayoutParams.MATCH_PARENT);
            //RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(image_width, new_height);
            rl.setMargins(0,0,margin,0);
            this.ll_partner_image.setLayoutParams(rl);
            this.ll_partner_image.setPadding(0,margin,margin,margin);

//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            this.tv_partner_description.setLayoutParams(lp);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    partnerItemClickListener.onPartnerItemClicked(getAdapterPosition());
                }
            });


        }
    }
    public void clearAll() {
        partnerModelList.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<PartnerResponseModel.PartnerModel> moveResults) {
        for (PartnerResponseModel.PartnerModel result : moveResults) {
            add(result);
        }
    }
    public void add(PartnerResponseModel.PartnerModel r) {
        partnerModelList.add(r);
        notifyItemInserted(partnerModelList.size() - 1);
    }
    public boolean isEmpty() {
        return getItemCount() == 0;
    }
    public PartnerResponseModel.PartnerModel getItem(int position) {
        return partnerModelList.get(position);
    }
    public interface PartnerItemClickListener{
        void onPartnerItemClicked(int adapterPosition);
    }
}

