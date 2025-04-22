package org.hse.android;

// Интерфейс OnItemClick для обработки кликов по элементам расписания в RecyclerView
public interface OnItemClick {
    // Метод, который будет вызван при клике на элемент расписания
    // Принимает объект ScheduleItem, представляющий данные о занятии
    void onItemClick(ScheduleItem data);
}