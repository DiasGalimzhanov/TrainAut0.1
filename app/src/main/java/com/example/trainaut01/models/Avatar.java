package com.example.trainaut01.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Avatar {
    private String id;
    private int lvl;
    private String urlAvatar;
    private String desc;

}

