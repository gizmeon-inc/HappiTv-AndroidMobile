package com.happi.android.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewBold;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.UserSubscriptionModel;

import java.util.ArrayList;
import java.util.List;

public class PremiumItemAdapter extends RecyclerView.Adapter<PremiumItemAdapter.ViewHolder> {
    Context context;
    itemClickListener thisItemClickListener;
    private List<UserSubscriptionModel> userSubscriptionModelList;
    public int row_index = -1;



    public PremiumItemAdapter(Context context, itemClickListener thisItemClickListener) {
        this.context = context;
        userSubscriptionModelList = new ArrayList<>();
        this.thisItemClickListener = thisItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_premium, viewGroup, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        UserSubscriptionModel userSubscriptionModel = userSubscriptionModelList.get(i);
        if(userSubscriptionModel!=null) {
            if (userSubscriptionModel.getSubscription_name() != null) {
                viewHolder.tv_subs_name.setText(userSubscriptionModel.getSubscription_name());
            } else{
                viewHolder.tv_subs_name.setText("");
            }

            if (userSubscriptionModel.getSubscription_type_name() != null){
                viewHolder.tv_subs_type.setText(userSubscriptionModel.getSubscription_type_name());
            } else{
                viewHolder.tv_subs_type.setText("");
            }
            /*if (userSubscriptionModel.getPrice() != null){
                String price = "$" + userSubscriptionModel.getPrice();
                viewHolder.tv_subs_price.setText(price);
            }else{
                viewHolder.tv_subs_price.setText("");
            }*/

            if (userSubscriptionModel.getValid_to() != null) {
                String validity = context.getResources().getString(R.string.subs_valid)
                        + " " + userSubscriptionModel.getValid_to();
                viewHolder.tv_subs_valid_date.setText(validity);
            }else{
                viewHolder.tv_subs_valid_date.setText("");
            }
        }

        if (row_index == i) {
            viewHolder.tv_subs_name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            viewHolder.tv_subs_type.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            viewHolder.tv_subs_valid_date.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            viewHolder.tv_subs_name.setTextColor(ContextCompat.getColor(context, R.color.whiteThree));
            viewHolder.tv_subs_type.setTextColor(ContextCompat.getColor(context, R.color.whiteThree));
            viewHolder.tv_subs_valid_date.setTextColor(ContextCompat.getColor(context, R.color.whiteThree));
        }


    }

    @Override
    public int getItemCount() {
        return userSubscriptionModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TypefacedTextViewBold tv_subs_name;
        TypefacedTextViewBold tv_subs_type;
        TypefacedTextViewBold tv_subs_price;
        TypefacedTextViewRegular tv_subs_valid_date;
        ImageView iv_subs_tag;
        View vw_line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_subs_name = itemView.findViewById(R.id.tv_subs_name);
            this.tv_subs_type = itemView.findViewById(R.id.tv_subs_type);
            this.tv_subs_price = itemView.findViewById(R.id.tv_subs_price);
            this.tv_subs_valid_date = itemView.findViewById(R.id.tv_subs_valid_date);

            this.tv_subs_type.setVisibility(View.GONE);
            this.tv_subs_price.setVisibility(View.GONE);
          //  this.iv_subs_tag = itemView.findViewById(R.id.iv_subs_tag);
            this.vw_line = itemView.findViewById(R.id.vw_line);
            itemView.setOnClickListener(v -> {
                thisItemClickListener.onItemClicked(getAdapterPosition());
                //row_index = getAdapterPosition();
                //notifyDataSetChanged();

            });

        }
    }
    public void updateList(List<UserSubscriptionModel> list) {
        userSubscriptionModelList = list;
        notifyDataSetChanged();
    }

    public UserSubscriptionModel getItem(int position) {
        return userSubscriptionModelList.get(position);
    }
    public void setSelected(int index) {
        row_index = index;
    }
    public interface itemClickListener {
        void onItemClicked(int adapterPosition);
    }
}
