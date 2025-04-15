package org.hse.android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    // private static final int TYPE_HEADER = 1; // Пока не используем

    private List<ScheduleItem> dataList;
    private OnItemClick onItemClick;

    public ItemAdapter(List<ScheduleItem> dataList, OnItemClick onItemClick) {
        this.dataList = dataList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ScheduleItem item = dataList.get(position);
            ((ItemViewHolder) holder).bind(item, position);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM; // Пока используем только один тип
    }

    // ViewHolder для обычного элемента
    private class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ItemViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(v -> {
                if (onItemClick != null) {
                    onItemClick.onItemClick(getAdapterPosition());
                }
            });
        }

        void bind(ScheduleItem item, int position) {
            textView.setText(item.getText());
        }
    }

    // Метод для обновления данных (можно вызвать позже, когда будут данные)
    public void updateData(List<ScheduleItem> newData) {
        this.dataList.clear();
        this.dataList.addAll(newData);
        notifyDataSetChanged();
    }
}