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

    private final List<CalendarDay> calendarDays;
    private final int currentDay;
    private final String[] weekdays = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};

    public CalendarAdapter(List<CalendarDay> calendarDays, int currentDay) {
        this.calendarDays = calendarDays;
        this.currentDay = currentDay;
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
            weekdayViewHolder._weekdayTextView.setText(weekdays[position]);
            Log.d("CalendarAdapter", "Binding weekday: " + weekdays[position]);
        } else if (holder instanceof CalendarViewHolder) {
            CalendarDay day = calendarDays.get(position - 7);
            CalendarViewHolder dayHolder = (CalendarViewHolder) holder;


            if (day.getDay() == 0) {
                dayHolder._dayTextView.setText("");
                dayHolder._llDayContainer.setBackgroundResource(R.drawable.calendar_day_background);
                Log.d("CalendarAdapter", "Binding empty day at position: " + position);
            } else {
                dayHolder._dayTextView.setText(String.valueOf(day.getDay()));
                dayHolder._dayTextView.setTextColor(Color.BLACK);
                dayHolder._llDayContainer.setBackgroundResource(R.drawable.calendar_day_background);

                Log.d("CalendarAdapter", "Day: " + day.getDay() + ", isCompleted: " + day.isCompleted() + " (before checking condition)");

                if(day.isCompleted() && day.getDay() == currentDay){
                    dayHolder._llDayContainer.setBackgroundResource(R.drawable.current_calendar_day_back);
                    dayHolder._dayTextView.setBackgroundResource(R.drawable.completed_calendar_day_back);
                }
                else if (day.isCompleted()) {
                    dayHolder._llDayContainer.setBackgroundResource(R.drawable.completed_calendar_day_back);
                    Log.d("CalendarAdapter", "Day: " + day.getDay() + ", isCompleted: " + day.isCompleted());
                    Log.d("CalendarAdapter", "Binding completed day: " + day.getDay() + " at position: " + position);
                }
                else if (day.getDay() == currentDay) {
                    dayHolder._llDayContainer.setBackgroundResource(R.drawable.current_calendar_day_back);
                    Log.d("CalendarAdapter", "Binding current day: " + day.getDay() + " at position: " + position);
                }
            }
        }

    }


    @Override
    public int getItemCount() {
        return calendarDays.size() + 7;
    }

    static class WeekdayViewHolder extends RecyclerView.ViewHolder {
        TextView _weekdayTextView;

        public WeekdayViewHolder(@NonNull View itemView) {
            super(itemView);
            _weekdayTextView = itemView.findViewById(R.id.tvWeekday);
        }
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
