package com.example.fakebook.model;

import android.graphics.Bitmap;

import com.example.fakebook.R;

import java.util.ArrayList;
import java.util.List;

public class Post {

    private String content;
    private String username;
    private int profile_image;
    private Bitmap post_image;
    ;
    private List<Comment> comments;

    public Post(String content, String username, int profile_image, Bitmap post_image) {
        this.content = content;
        this.username = username;
        this.profile_image = R.drawable.profile_image;
        this.post_image = post_image;
        this.comments = new ArrayList<>();
    }
    public Post(String content, String username, int profile_image) {
        this.content = content;
        this.username = username;
        this.profile_image = R.drawable.profile_image;
        this.post_image = null;
        this.comments = new ArrayList<>();
    }

    public String getContent() {
        return this.content;
    }

    public String getUsername() {
        return this.username;
    }

    public int getProfile_image() {
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
