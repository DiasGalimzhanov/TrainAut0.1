package com.example.trainaut01.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class CalendarDay {
    private int day;
    private boolean isCompleted;

}
