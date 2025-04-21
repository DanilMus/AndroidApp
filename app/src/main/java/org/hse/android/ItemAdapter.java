package org.hse.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    private List<ScheduleItem> dataList = new ArrayList<>();
    private OnItemClick onItemClick;

    public ItemAdapter(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == TYPE_ITEM) {
            View contactView = inflater.inflate(R.layout.item_schedule, parent, false);
            return new ViewHolder(contactView, context, this, onItemClick);
        } else if (viewType == TYPE_HEADER) {
            View contactView = inflater.inflate(R.layout.item_schedule_header, parent, false);
            return new ViewHolderHeader(contactView, context, onItemClick);
        }

        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ScheduleItem data = dataList.get(position);
        if (viewHolder instanceof ViewHolder) {
            ((ViewHolder) viewHolder).bind(data);
        } else if (viewHolder instanceof ViewHolderHeader) {
            ((ViewHolderHeader) viewHolder).bind((ScheduleItemHeader) data);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ScheduleItem data = dataList.get(position);
        if (data instanceof ScheduleItemHeader) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    // ViewHolder для обычного элемента
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private ItemAdapter adapter; // Добавляем ссылку на адаптер
        private OnItemClick onItemClick;
        private TextView start;
        private TextView end;
        private TextView type;
        private TextView name;
        private TextView place;
        private TextView teacher;

        public ViewHolder(View itemView, Context context, ItemAdapter adapter, OnItemClick onItemClick) {
            super(itemView);
            this.context = context;
            this.adapter = adapter; // Сохраняем адаптер
            this.onItemClick = onItemClick;
            start = itemView.findViewById(R.id.start);
            end = itemView.findViewById(R.id.end);
            type = itemView.findViewById(R.id.type);
            name = itemView.findViewById(R.id.name);
            place = itemView.findViewById(R.id.place);
            teacher = itemView.findViewById(R.id.teacher);

            itemView.setOnClickListener(v -> {
                if (onItemClick != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClick.onItemClick(adapter.dataList.get(position)); // Используем adapter.dataList
                    }
                }
            });
        }

        public void bind(ScheduleItem data) {
            start.setText(data.getStart());
            end.setText(data.getEnd());
            type.setText(data.getType());
            name.setText(data.getName());
            place.setText(data.getPlace());
            teacher.setText(data.getTeacher());
        }
    }

    // ViewHolder для заголовка
    public static class ViewHolderHeader extends RecyclerView.ViewHolder {
        private Context context;
        private OnItemClick onItemClick;
        private TextView title;

        public ViewHolderHeader(View itemView, Context context, OnItemClick onItemClick) {
            super(itemView);
            this.context = context;
            this.onItemClick = onItemClick;
            title = itemView.findViewById(R.id.title);
        }

        public void bind(ScheduleItemHeader data) {
            title.setText(data.getTitle());
        }
    }

    // Метод для установки данных
    public void setDataList(List<ScheduleItem> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }
}