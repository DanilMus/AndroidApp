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

// Адаптер для RecyclerView, используемый в ScheduleActivity для отображения списка расписания
public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Константы для типов элементов в списке
    private static final int TYPE_ITEM = 0; // Тип для обычного элемента расписания (например, занятие)
    private static final int TYPE_HEADER = 1; // Тип для заголовка (например, "Понедельник, 28 января")

    // Поля класса
    private List<ScheduleItem> dataList = new ArrayList<>(); // Список данных (элементы расписания и заголовки)
    private OnItemClick onItemClick; // Интерфейс для обработки кликов по элементам

    // Конструктор адаптера
    public ItemAdapter(OnItemClick onItemClick) {
        this.onItemClick = onItemClick; // Сохраняем обработчик кликов
    }

    // Метод для создания ViewHolder в зависимости от типа элемента
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Получаем контекст из родительского ViewGroup
        Context context = parent.getContext();
        // Создаём LayoutInflater для раздувания layout'ов
        LayoutInflater inflater = LayoutInflater.from(context);

        // В зависимости от типа элемента создаём соответствующий ViewHolder
        if (viewType == TYPE_ITEM) {
            // Для обычного элемента расписания используем layout item_schedule.xml
            View contactView = inflater.inflate(R.layout.item_schedule, parent, false);
            // Создаём ViewHolder для обычного элемента
            return new ViewHolder(contactView, context, this, onItemClick);
        } else if (viewType == TYPE_HEADER) {
            // Для заголовка используем layout item_schedule_header.xml
            View contactView = inflater.inflate(R.layout.item_schedule_header, parent, false);
            // Создаём ViewHolder для заголовка
            return new ViewHolderHeader(contactView, context, onItemClick);
        }

        // Если тип элемента неизвестен, выбрасываем исключение
        throw new IllegalArgumentException(context.getString(R.string.invalid_view_type));
    }

    // Метод для привязки данных к ViewHolder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        // Получаем данные для текущей позиции
        ScheduleItem data = dataList.get(position);
        // В зависимости от типа ViewHolder вызываем соответствующий метод bind
        if (viewHolder instanceof ViewHolder) {
            // Для обычного элемента вызываем bind в ViewHolder
            ((ViewHolder) viewHolder).bind(data);
        } else if (viewHolder instanceof ViewHolderHeader) {
            // Для заголовка вызываем bind в ViewHolderHeader, приводя данные к ScheduleItemHeader
            ((ViewHolderHeader) viewHolder).bind((ScheduleItemHeader) data);
        }
    }

    // Метод для получения общего количества элементов в списке
    @Override
    public int getItemCount() {
        return dataList.size(); // Возвращаем размер списка данных
    }

    // Метод для определения типа элемента на заданной позиции
    @Override
    public int getItemViewType(int position) {
        // Получаем данные для текущей позиции
        ScheduleItem data = dataList.get(position);
        // Если данные являются заголовком (ScheduleItemHeader), возвращаем TYPE_HEADER
        if (data instanceof ScheduleItemHeader) {
            return TYPE_HEADER;
        }
        // Иначе возвращаем TYPE_ITEM для обычного элемента
        return TYPE_ITEM;
    }

    // Внутренний статический класс ViewHolder для обычного элемента расписания
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Поля для хранения контекста, адаптера и элементов UI
        private Context context; // Контекст активности
        private ItemAdapter adapter; // Ссылка на адаптер для доступа к dataList
        private OnItemClick onItemClick; // Обработчик кликов
        private TextView start; // TextView для времени начала занятия
        private TextView end; // TextView для времени окончания занятия
        private TextView type; // TextView для типа занятия
        private TextView name; // TextView для названия дисциплины
        private TextView place; // TextView для места проведения
        private TextView teacher; // TextView для имени преподавателя

        // Конструктор ViewHolder
        public ViewHolder(View itemView, Context context, ItemAdapter adapter, OnItemClick onItemClick) {
            super(itemView); // Вызываем конструктор родительского класса
            this.context = context; // Сохраняем контекст
            this.adapter = adapter; // Сохраняем адаптер
            this.onItemClick = onItemClick; // Сохраняем обработчик кликов

            // Инициализируем TextView из layout'а item_schedule.xml
            start = itemView.findViewById(R.id.start); // Находим TextView для времени начала
            end = itemView.findViewById(R.id.end); // Находим TextView для времени окончания
            type = itemView.findViewById(R.id.type); // Находим TextView для типа занятия
            name = itemView.findViewById(R.id.name); // Находим TextView для названия дисциплины
            place = itemView.findViewById(R.id.place); // Находим TextView для места проведения
            teacher = itemView.findViewById(R.id.teacher); // Находим TextView для имени преподавателя

            // Устанавливаем слушатель кликов на элемент
            itemView.setOnClickListener(v -> {
                if (onItemClick != null) {
                    int position = getAdapterPosition(); // Получаем текущую позицию элемента
                    if (position != RecyclerView.NO_POSITION) { // Проверяем, что позиция валидна
                        // Вызываем обработчик кликов, передавая данные из dataList
                        onItemClick.onItemClick(adapter.dataList.get(position));
                    }
                }
            });
        }

        // Метод для привязки данных к UI элементам
        public void bind(ScheduleItem data) {
            // Устанавливаем данные в соответствующие TextView
            start.setText(data.getStart()); // Время начала занятия
            end.setText(data.getEnd()); // Время окончания занятия
            type.setText(data.getType()); // Тип занятия
            name.setText(data.getName()); // Название дисциплины
            place.setText(data.getPlace()); // Место проведения
            teacher.setText(data.getTeacher()); // Имя преподавателя
        }
    }

    // Внутренний статический класс ViewHolderHeader для заголовка
    public static class ViewHolderHeader extends RecyclerView.ViewHolder {
        // Поля для хранения контекста, обработчика кликов и UI элемента
        private Context context; // Контекст активности
        private OnItemClick onItemClick; // Обработчик кликов (пока не используется)
        private TextView title; // TextView для заголовка

        // Конструктор ViewHolderHeader
        public ViewHolderHeader(View itemView, Context context, OnItemClick onItemClick) {
            super(itemView); // Вызываем конструктор родительского класса
            this.context = context; // Сохраняем контекст
            this.onItemClick = onItemClick; // Сохраняем обработчик кликов
            title = itemView.findViewById(R.id.title); // Находим TextView для заголовка из item_schedule_header.xml
        }

        // Метод для привязки данных к UI элементу
        public void bind(ScheduleItemHeader data) {
            title.setText(data.getTitle()); // Устанавливаем текст заголовка (например, "Понедельник, 28 января")
        }
    }

    // Метод для установки нового списка данных
    public void setDataList(List<ScheduleItem> dataList) {
        this.dataList.clear(); // Очищаем текущий список
        this.dataList.addAll(dataList); // Добавляем новые данные
        notifyDataSetChanged(); // Уведомляем адаптер об изменении данных для обновления UI
    }
}