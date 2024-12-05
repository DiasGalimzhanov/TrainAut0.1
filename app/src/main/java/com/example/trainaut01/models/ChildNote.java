package com.example.trainaut01.models;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChildNote {
    private String id = UUID.randomUUID().toString();
    private String parentId;
    private String childId;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;

    public ChildNote(String parentId, String childId, String title, String content, Date createdAt, Date updatedAt) {
        this.parentId = parentId;
        this.childId = childId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
