package com.example.trainaut01.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.google.firebase.Timestamp;

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
    private Long createdAt;
    private Long updatedAt;

    public ChildNote() {
        this.id = UUID.randomUUID().toString();
    }

    public ChildNote(String parentId, String childId, String title, String content, Long createdAt, Long updatedAt) {
        this.parentId = parentId;
        this.childId = childId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> noteMap = new HashMap<>();
        noteMap.put("id", id);
        noteMap.put("parentId", parentId);
        noteMap.put("childId", childId);
        noteMap.put("title", title);
        noteMap.put("content", content);
        noteMap.put("createdAt", createdAt != null ? createdAt : Timestamp.now());
        noteMap.put("updatedAt", updatedAt != null ? updatedAt : Timestamp.now());
        return noteMap;
    }

}
