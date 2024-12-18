package com.example.trainaut01.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.R;
import com.example.trainaut01.models.CalendarDay;


import java.util.List;

/**
 * Адаптер для отображения календаря в виде RecyclerView.
 * Поддерживает заголовок месяца, дни недели и дни месяца.
 */
public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_WEEKDAY = 0;
    private static final int VIEW_TYPE_DAY = 1;
    private static final int VIEW_TYPE_MONTH_HEADER = -1;

    private final Context _context;
    private final List<CalendarDay> _calendarDays;
    private final int _currentDay;
    private final String _currentMonth;
    private final String[] _weekdays = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};

    /**
     * Конструктор адаптера.
     *
     * @param context       Контекст активности или фрагмента.
     * @param calendarDays  Список объектов {@link CalendarDay}, представляющих дни месяца.
     * @param currentDay    Номер текущего дня месяца.
     * @param currentMonth  Название текущего месяца (локализованное).
     */
    public CalendarAdapter(Context context, List<CalendarDay> calendarDays, int currentDay, String currentMonth) {
        this._context = context;
        this._calendarDays = calendarDays;
        this._currentDay = currentDay;
        this._currentMonth = currentMonth;
    }

    /**
     * Определяет тип элемента в указанной позиции.
     *
     * @param position Позиция элемента.
     * @return Тип элемента: заголовок месяца, день недели или день месяца.
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_MONTH_HEADER;
        } else if (position > 0 && position <= 7) {
            return VIEW_TYPE_WEEKDAY;
        } else {
            return VIEW_TYPE_DAY;
        }
    }

    /**
     * Создает ViewHolder для указанного типа элемента.
     *
     * @param parent   Родительский ViewGroup.
     * @param viewType Тип элемента.
     * @return ViewHolder для указанного типа.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MONTH_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_month_header, parent, false);
            return new MonthHeaderViewHolder(view);
        } else if (viewType == VIEW_TYPE_WEEKDAY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weekday, parent, false);
            return new WeekdayViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
            return new CalendarViewHolder(view);
        }
    }

    /**
     * Привязывает данные к ViewHolder.
     *
     * @param holder   ViewHolder для привязки.
     * @param position Позиция элемента.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MonthHeaderViewHolder) {
            MonthHeaderViewHolder monthHolder = (MonthHeaderViewHolder) holder;
            monthHolder._monthTextView.setText(_currentMonth);
        } else if (holder instanceof WeekdayViewHolder) {
            WeekdayViewHolder weekdayViewHolder = (WeekdayViewHolder) holder;
            weekdayViewHolder._weekdayTextView.setText(_weekdays[position - 1]);
        } else if (holder instanceof CalendarViewHolder) {
            CalendarDay day = _calendarDays.get(position - 8);
            CalendarViewHolder dayHolder = (CalendarViewHolder) holder;


            if (day.getDay() == 0) {
                dayHolder._dayTextView.setText("");
                dayHolder._llDayContainer.setBackgroundColor(Color.TRANSPARENT);
                dayHolder._dayTextView.setBackgroundColor(Color.TRANSPARENT);
            } else {
                dayHolder._dayTextView.setText(String.valueOf(day.getDay()));
                dayHolder._dayTextView.setTextColor(Color.WHITE);

                if (isWeekend(position)) {
                    dayHolder._dayTextView.setTextColor(_context.getResources().getColor(R.color.Scarlet));
                }
                if (day.isCompleted() && day.getDay() == _currentDay) {
                    dayHolder._llDayContainer.setBackgroundResource(R.drawable.current_calendar_day_back);
                    dayHolder._dayTextView.setBackgroundResource(R.drawable.completed_calendar_day_back);
                } else if (day.isCompleted()) {
                    dayHolder._llDayContainer.setBackgroundResource(R.drawable.completed_calendar_day_back);
                } else if (day.getDay() == _currentDay) {
                    dayHolder._llDayContainer.setBackgroundResource(R.drawable.current_calendar_day_back);
                }
            }
        }

    }

    /**
     * Возвращает общее количество элементов в адаптере.
     *
     * @return Общее количество элементов (заголовок + дни недели + дни месяца).
     */
    @Override
    public int getItemCount() {
        return _calendarDays.size() + 8;
    }

    /**
     * Определяет, является ли указанный день выходным.
     *
     * @param position Позиция элемента в RecyclerView.
     * @return true, если день является выходным (суббота или воскресенье), иначе false.
     */
    private boolean isWeekend(int position) {
        int dayOfWeek = ((position - 1) % 7) + 1;
        return dayOfWeek == 6 || dayOfWeek == 7;
    }

    /**
     * ViewHolder для отображения заголовка месяца.
     */
    static class MonthHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView _monthTextView;

        public MonthHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            _monthTextView = itemView.findViewById(R.id.tvMonthHeader);
        }
    }

    /**
     * ViewHolder для отображения дня недели.
     */
    static class WeekdayViewHolder extends RecyclerView.ViewHolder {
        TextView _weekdayTextView;

        public WeekdayViewHolder(@NonNull View itemView) {
            super(itemView);
            _weekdayTextView = itemView.findViewById(R.id.tvWeekday);
        }
    }

    /**
     * ViewHolder для отображения дня месяца.
     */
    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView _dayTextView;
        LinearLayout _llDayContainer;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            _dayTextView = itemView.findViewById(R.id.tvDay);
            _llDayContainer = itemView.findViewById(R.id.llDayContainer);
        }
    }
}
