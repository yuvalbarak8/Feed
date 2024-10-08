package com.example.fakebook;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;
import java.util.List;

public class Post {

    private String date;

    private String content;
    private String username;
    private Bitmap profile_image;
    private Bitmap post_image;
    ;
    private List<Comment> comments;

    public Post(String content, String username, Bitmap profile_image, Bitmap post_image, String date) {
        this.content = content;
        this.username = username;
        this.profile_image = profile_image;
        this.post_image = post_image;
        this.comments = new ArrayList<>();
        this.date = date;
    }

    public String getContent() {
        return this.content;
    }

    public String getUsername() {
        return this.username;
    }
    public String getDate() {
        return date;
    }


    public Bitmap getProfile_image() {
        return this.profile_image;
    }
    public Bitmap getPost_image()
    {
        return this.post_image;
    }
    public void add_comment(Comment comment) {
        this.comments.add(0, comment);
    }

    public void setContent(String content) {
        this.content = content;
    }


    public List<Comment> getComments() {
        return this.comments;
    }

    public void setComments(List<Comment> updatedCommentsList) {
        this.comments = updatedCommentsList;
    }
}
