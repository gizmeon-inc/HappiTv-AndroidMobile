package com.happi.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.roomController.RoomVideoSearchModel;

import java.util.List;

public class VideoSearchSuggestionAdapter extends RecyclerView.Adapter<VideoSearchSuggestionAdapter.MyViewHolder> {

    private List<RoomVideoSearchModel> keywordList;
    private Context context;
    private suggestedVideoItemClickListener suggestedVideoItemClickListener;

    public VideoSearchSuggestionAdapter(Context context, List<RoomVideoSearchModel> keywordList,
                                        suggestedVideoItemClickListener suggestedVideoItemClickListener) {
        this.context = context;
        this.keywordList = keywordList;
        this.suggestedVideoItemClickListener = suggestedVideoItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_suggestion, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoSearchSuggestionAdapter.MyViewHolder holder, int position) {

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

                suggestedVideoItemClickListener.onSuggestedVideoItemClicked(getAdapterPosition(),
                        keywordList.get(getAdapterPosition()).getSearchKeyword());
            });
        }
    }

    public interface suggestedVideoItemClickListener {

        void onSuggestedVideoItemClicked(int adapterPosition, String searchKey);
    }
}