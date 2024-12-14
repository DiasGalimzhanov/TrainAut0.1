package com.example.trainaut01.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message implements Serializable {
    private final String id;
    private String title;
    private final String content;
    private final long timestamp;
    private boolean isRead;

}

