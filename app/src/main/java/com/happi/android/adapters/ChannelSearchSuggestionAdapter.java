package com.happi.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.roomController.RoomChannelSearchModel;

import java.util.List;


public class ChannelSearchSuggestionAdapter extends RecyclerView.Adapter<ChannelSearchSuggestionAdapter.MyViewHolder> {

    private List<RoomChannelSearchModel> keywordList;
    private Context context;
    private ChannelSearchSuggestionAdapter.suggestedChannelItemClickListener suggestedChannelItemClickListener;

    public ChannelSearchSuggestionAdapter(Context context, List<RoomChannelSearchModel>
            keywordList, suggestedChannelItemClickListener suggestedChannelItemClickListener) {
        this.context = context;
        this.keywordList = keywordList;
        this.suggestedChannelItemClickListener = suggestedChannelItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_suggestion, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_keyword.setText(keywordList.get(position).getSearchKeyword());
    }

    @Override
    public int getItemCount() {

        return keywordList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TypefacedTextViewRegular tv_keyword;

        public MyViewHolder(View itemView) {

            super(itemView);
            tv_keyword = itemView.findViewById(R.id.tv_keyword);

            itemView.setOnClickListener(v -> {

                suggestedChannelItemClickListener.onSuggestedChannelItemClicked(getAdapterPosition(),
                        keywordList.get(getAdapterPosition()).getSearchKeyword());
            });
        }
    }

    public interface suggestedChannelItemClickListener {

        void onSuggestedChannelItemClicked(int adapterPosition, String searchKey);
    }
}
