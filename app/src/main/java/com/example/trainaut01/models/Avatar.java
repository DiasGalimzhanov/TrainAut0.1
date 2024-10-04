package com.example.trainaut01.models;


public class Avatar {
    private String id;
    private int lvl;
    private String urlAvatar;
    private String desc;

    public Avatar(String id, int lvl, String urlAvatar, String desc) {
        this.id = id;
        this.lvl = lvl;
        this.urlAvatar = urlAvatar;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public int getLvl() {
        return lvl;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public String getDesc() {
        return desc;
    }
}

