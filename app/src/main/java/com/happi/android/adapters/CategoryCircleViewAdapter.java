package com.happi.android.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.models.CategoryModel;
import com.happi.android.utils.ConstantUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.overrideOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class CategoryCircleViewAdapter extends RecyclerView.Adapter<CategoryCircleViewAdapter.CircleAdapterViewHolder> {

    private Context context;
    private List<CategoryModel.Category> categories;
    private itemClickListenerForCategory itemClickListener;
    private boolean isVertical;

    public CategoryCircleViewAdapter(Context context, itemClickListenerForCategory itemClickListener,boolean isVertical){
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.isVertical = isVertical;
        categories = new ArrayList<>();
    }

    @NonNull
    @Override
    public CircleAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(isVertical){
            View v = LayoutInflater.from(context).inflate(R.layout.item_category_circle_view_vert, viewGroup, false);
            return new CircleAdapterViewHolder(v);
        }else{
            View v = LayoutInflater.from(context).inflate(R.layout.item_category_circle_view, viewGroup, false);
            return new CircleAdapterViewHolder(v);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull CircleAdapterViewHolder circleAdapterViewHolder, int i) {

        if(categories.size() > 0){
            String categoryIcon = categories.get(i).getCategory_icon();
            if (categoryIcon != null){
                circleAdapterViewHolder.tv_category_title.setText("");
                Glide.with(context)
                        .load(ConstantUtils.CHANNEL_THUMBNAIL + categories.get(i).getCategory_icon().trim())
                        .error(Glide.with(context)
                                .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                        .apply(placeholderOf(R.drawable.ic_placeholder))
                        .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        // .apply(fitCenterTransform())
                        .apply(overrideOf(400,333))
                        //  .apply(centerCropTransform())
                        .into(circleAdapterViewHolder.cv_category);
            }else{
                circleAdapterViewHolder.tv_category_title.setText(categories.get(i).getCategory());
                Glide.with(context)
                        .load(R.drawable.bg_pinkishgrey)
                        .error(Glide.with(context)
                                .load(ContextCompat.getDrawable(context,R.drawable.ic_placeholder)))
                        .apply(placeholderOf(R.drawable.ic_placeholder))
                        .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        // .apply(fitCenterTransform())
                        //  .apply(centerCropTransform())
                        .into(circleAdapterViewHolder.cv_category);
            }
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CircleAdapterViewHolder extends RecyclerView.ViewHolder{
        CircleImageView cv_category;
        TypefacedTextViewSemiBold tv_category_title;

        public CircleAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_category = itemView.findViewById(R.id.cv_category);
            tv_category_title = itemView.findViewById(R.id.tv_category_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onCategoryItemClickedForCircleView(getAdapterPosition());
                }
            });
        }
    }
    public boolean isEmpty() {
        return getItemCount() == 0;
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
    public interface itemClickListenerForCategory {
        void onCategoryItemClickedForCircleView(int adapterPosition);
    }
}
