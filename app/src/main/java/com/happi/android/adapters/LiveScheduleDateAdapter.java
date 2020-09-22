package com.happi.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.happi.android.R;

import java.util.ArrayList;
import java.util.List;

public class LiveScheduleDateAdapter extends RecyclerView.Adapter<LiveScheduleDateAdapter.DateViewHolder> {

    private List<String> dateList;
    private Context context;
    private onItemClicked onItemClicked;

    public LiveScheduleDateAdapter(Context context, onItemClicked onItemClicked){
        this.context = context;
        this.onItemClicked = onItemClicked;
        this.dateList = new ArrayList<>();
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_date_picker, null);
        return new DateViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder dateViewHolder, int i) {
        if(dateList.size() > 0){
            dateViewHolder.tv_date.setText(dateList.get(i));
        }

    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class DateViewHolder extends RecyclerView.ViewHolder{

        TextView tv_date;
        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClicked.onDateItemClicked(getAdapterPosition());
                }
            });
        }
    }
    public void addAll(List<String> newList){
        for(String date : newList){
            add(date);
        }
    }
    public void add(String newdate){
        dateList.add(newdate);
        notifyItemInserted(dateList.size() - 1);
    }
    public void clearAll(){
        dateList.clear();
        notifyDataSetChanged();
    }
    public boolean isEmpty(){
        return getItemCount() == 0;
    }

    public interface onItemClicked{
        void onDateItemClicked(int adapterPosition);
    }
}
