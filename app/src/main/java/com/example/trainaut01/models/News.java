package com.example.trainaut01.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class News {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
}
