package com.example.SnapNews;

/**
 * Created by Deb Banerji on 27-Mar-16.
 */
public class Comment {
    private String user;
    private String content;

    public Comment(String user, String content) {
        this.user = user;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getUser() {
        return user;
    }
}
