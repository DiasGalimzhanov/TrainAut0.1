package com.example.trainaut01.adapter;

import android.graphics.Color;
import android.util.Log;
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

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_WEEKDAY = 0;
    private static final int VIEW_TYPE_DAY = 1;

    private final List<CalendarDay> _calendarDays;
    private final int _currentDay;
    private final String[] _weekdays = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};

    public CalendarAdapter(List<CalendarDay> calendarDays, int currentDay) {
        this._calendarDays = calendarDays;
        this._currentDay = currentDay;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 7) {
            return VIEW_TYPE_WEEKDAY;
        } else {
            return VIEW_TYPE_DAY;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_WEEKDAY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weekday, parent, false);
            return new WeekdayViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
            return new CalendarViewHolder(view);
        }
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WeekdayViewHolder) {
            WeekdayViewHolder weekdayViewHolder = (WeekdayViewHolder) holder;
            weekdayViewHolder._weekdayTextView.setText(_weekdays[position]);
        } else if (holder instanceof CalendarViewHolder) {
            CalendarDay day = _calendarDays.get(position - 7);
            CalendarViewHolder dayHolder = (CalendarViewHolder) holder;


            if (day.getDay() == 0) {
                dayHolder._dayTextView.setText("");
                dayHolder._llDayContainer.setBackgroundColor(Color.TRANSPARENT);
                dayHolder._dayTextView.setBackgroundColor(Color.TRANSPARENT);
            }else {
                dayHolder._dayTextView.setText(String.valueOf(day.getDay()));
                dayHolder._dayTextView.setTextColor(Color.BLACK);
                dayHolder._llDayContainer.setBackgroundResource(R.drawable.calendar_day_background);

                if (isWeekend(position)) {
                    dayHolder._llDayContainer.setBackgroundResource(R.drawable.weekend_calendar_back);
                    dayHolder._dayTextView.setTextColor(Color.RED);
                }

                if(day.isCompleted() && day.getDay() == _currentDay){
                    dayHolder._llDayContainer.setBackgroundResource(R.drawable.current_calendar_day_back);
                    dayHolder._dayTextView.setBackgroundResource(R.drawable.completed_calendar_day_back);
                }
                else if (day.isCompleted()) {
                    dayHolder._llDayContainer.setBackgroundResource(R.drawable.completed_calendar_day_back);
                }
                else if (day.getDay() == _currentDay) {
                    dayHolder._llDayContainer.setBackgroundResource(R.drawable.current_calendar_day_back);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return _calendarDays.size() + 7;
    }

    static class WeekdayViewHolder extends RecyclerView.ViewHolder {
        TextView _weekdayTextView;

        public WeekdayViewHolder(@NonNull View itemView) {
            super(itemView);
            _weekdayTextView = itemView.findViewById(R.id.tvWeekday);
        }
    }

    private boolean isWeekend(int position) {
        int dayOfWeek = (position % 7) + 1;
        return dayOfWeek == 6 || dayOfWeek == 7;
    }


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
