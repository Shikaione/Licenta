package com.mpetroiu.smc;

import com.google.firebase.database.Exclude;

public class Comment {

    private String Comment;
    private String Name;
    private String Key;

    public Comment(){

    }
    public Comment(String comment, String name, String key) {
        Comment = comment;
        Name = name;
        Key = key;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Exclude
    public String getKey() {
        return Key;
    }

    @Exclude
    public void setKey(String key) {
        Key = key;
    }
}
