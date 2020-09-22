package com.happi.android.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.happi.android.models.CategoryModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;
import static com.bumptech.glide.request.RequestOptions.overrideOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder> {

    private List<CategoryModel.Category> categories;
    private Context context;
    private itemClickListener clickObj;
    private boolean isVertical;

    public CategoryListAdapter(Context context, itemClickListener clickObj, boolean isVertical) {
        categories = new ArrayList<>();
        this.clickObj = clickObj;
        this.context = context;
        this.isVertical = isVertical;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        if (isVertical) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category_vertical, parent, false);
            return new MyViewHolder(view);
        } else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category_horizontal, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String categoryIcon = categories.get(position).getCategory_icon();
        if (categoryIcon != null){
            holder.tv_category_title.setText("");
            Glide.with(context)
                    .load(ConstantUtils.CHANNEL_THUMBNAIL + categories.get(position).getCategory_icon())
                    .error(Glide.with(context)
                            .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                    .apply(placeholderOf(R.drawable.ic_placeholder))
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                    .apply(fitCenterTransform())
                    .apply(overrideOf(400,333))
                  //  .apply(centerCropTransform())
                    .into(holder.iv_thumbnail);
        }
        else {
            holder.tv_category_title.setText(categories.get(position).getCategory());
            Glide.with(context)
                    .load(R.drawable.ic_placeholder_grey)
                    .error(Glide.with(context)
                            .load(ContextCompat.getDrawable(context,R.drawable.ic_placeholder)))
                    .apply(placeholderOf(R.drawable.ic_placeholder))
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                    .apply(fitCenterTransform())
                    .apply(overrideOf(400,333))
                  //  .apply(centerCropTransform())
                    .into(holder.iv_thumbnail);
        }


    }

    @Override
    public int getItemCount() {

        return categories.size();
    }

    public void updateList(List<CategoryModel.Category> list) {
        categories = list;
        notifyDataSetChanged();
    }

    public void addAll(List<CategoryModel.Category> moveResults) {
        for (CategoryModel.Category result : moveResults) {
            add(result);
        }
    }

    public void add(CategoryModel.Category r) {
        categories.add(r);
        notifyItemInserted(categories.size() - 1);
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public CategoryModel.Category getItem(int position) {
        return categories.get(position);
    }

    public void remove(CategoryModel.Category model) {
        int position = categories.indexOf(model);
        if (position > -1) {
            categories.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearAll() {
        categories.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView iv_thumbnail;
        TypefacedTextViewSemiBold tv_category_title;


        public MyViewHolder(View itemView) {
            super(itemView);

            this.iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            this.tv_category_title = itemView.findViewById(R.id.tv_category_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickObj.onCategoryItemClicked(getAdapterPosition());
                }
            });
        }

    }

    public interface itemClickListener {
        void onCategoryItemClicked(int adapterPosition);
    }
}
