package com.happi.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewBold;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.CategoryModel;
import com.happi.android.models.PartnerResponseModel;
import com.happi.android.utils.ConstantUtils;


import java.util.ArrayList;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;


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

