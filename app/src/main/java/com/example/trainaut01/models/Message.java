package com.example.trainaut01.models;

import java.io.Serializable;

public class Message implements Serializable {
    private String id;
    private String title;
    private String content;
    private long timestamp;
    private boolean isRead;

    public Message(String id,String title, String content, long timestamp, boolean isRead) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}

