package com.happi.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.models.LanguageModel;

import java.util.List;

public class ListCheckboxAdapter extends RecyclerView.Adapter<ListCheckboxAdapter.MyViewHolder> {

    private List<LanguageModel.LanguageList> languageList;

    public ListCheckboxAdapter(List<LanguageModel.LanguageList> languageList) {

        this.languageList = languageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkbox_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_item.setText(languageList.get(position).getLanguage_name());
        if (languageList.get(position).isSelected()) {
            holder.iv_checkBox.setImageResource(R.drawable.ic_checkbox_fill);
        } else {
            holder.iv_checkBox.setImageResource(R.drawable.ic_checkbox_empty);
        }
    }

    @Override
    public int getItemCount() {

        return languageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TypefacedTextViewSemiBold tv_item;
        ImageView iv_checkBox;

        public MyViewHolder(View itemView) {

            super(itemView);
            tv_item = itemView.findViewById(R.id.tv_item);
            iv_checkBox = itemView.findViewById(R.id.iv_checkBox);

            itemView.setOnClickListener(v -> {

                if (languageList.get(getAdapterPosition()).isSelected()) {
                    languageList.get(getAdapterPosition()).setSelected(false);
                } else {
                    languageList.get(getAdapterPosition()).setSelected(true);
                }
                notifyDataSetChanged();
            });
        }
    }
}
