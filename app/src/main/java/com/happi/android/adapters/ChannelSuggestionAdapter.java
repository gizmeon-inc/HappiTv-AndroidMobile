package com.happi.android.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewBold;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.models.ChannelModel;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class ChannelSuggestionAdapter extends RecyclerView.Adapter<ChannelSuggestionAdapter.SuggestionChannelViewHolder>{

    private List<ChannelModel> sChannelList;
    private Context context;
    private SuggesteditemClickListener clickObj;
    private boolean isVertical = false;

    public ChannelSuggestionAdapter(Context context, SuggesteditemClickListener clickObj,
                                    boolean isVertical) {
        this.context = context;
        this.clickObj = clickObj;
        this.isVertical = isVertical;
        sChannelList = new ArrayList<>();
    }

    @Override
    public SuggestionChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isVertical) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channels_vertical, parent, false);
            return new SuggestionChannelViewHolder(view);
        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_horizontal, parent,
                    false);
            return new SuggestionChannelViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(SuggestionChannelViewHolder holder, int position) {
        ChannelModel cModel = sChannelList.get(position);
        Glide.with(context)
                .load(ConstantUtils.CHANNEL_THUMBNAIL + cModel.getLogo())
                .error(Glide.with(context).load(ContextCompat.getDrawable(context,
                        R.drawable.ic_placeholder)))
                .apply(placeholderOf(R.drawable.ic_placeholder))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .into(holder.iv_thumbnail);
        if (cModel.getLiveFlag() != null) {
            if (cModel.getLiveFlag() == 1) {
                holder.tv_live_tag.setVisibility(View.VISIBLE);
            }else {
                holder.tv_live_tag.setVisibility(View.GONE);
            }
        }

     /*   if (!TextUtils.isEmpty(cModel.getChannelName()) && !cModel.getChannelName().equals("")) {
            holder.tv_video_title.setText(cModel.getChannelName());
        }else {
            holder.tv_video_title.setText(context.getResources().getString(R.string.demo_channel));
        }*/
    }

    @Override
    public int getItemCount() {
        return sChannelList.size();
    }

    public void updateList(List<ChannelModel> list) {
        sChannelList = list;
        notifyDataSetChanged();
    }

    public void addAll(List<ChannelModel> moveResults) {
        for (ChannelModel result : moveResults) {
            add(result);
        }
    }

    public void add(ChannelModel r) {
        sChannelList.add(r);
        notifyItemInserted(sChannelList.size() - 1);
    }
    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public ChannelModel getItem(int position) {
        return sChannelList.get(position);
    }

    public void remove(ChannelModel model) {
        int position = sChannelList.indexOf(model);
        if (position > -1) {
            sChannelList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void clearAll() {
        sChannelList.clear();
        notifyDataSetChanged();
    }
    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public class SuggestionChannelViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_thumbnail;
        TypefacedTextViewBold tv_live_tag;
        TypefacedTextViewSemiBold tv_video_title;
        public SuggestionChannelViewHolder(View itemView) {
            super(itemView);
            this.iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            this.tv_live_tag = itemView.findViewById(R.id.tv_live_tag);
            this.tv_video_title = itemView.findViewById(R.id.tv_video_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     clickObj.onSuggestedItemClicked(getAdapterPosition());
                }
            });
        }
    }

    public interface SuggesteditemClickListener {
        void onSuggestedItemClicked(int adapterPosition);
    }
}
