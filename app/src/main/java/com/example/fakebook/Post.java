package com.example.fakebook;

import java.util.ArrayList;
import java.util.List;

public class Post {

    private String content;
    private String when_posted;
    private int profile_image;
    private int image;
    private List<Comment> comments;

    public Post(String content, String when_posted, int profile_image, int image) {
        this.content = content;
        this.when_posted = when_posted;
        this.profile_image = profile_image;
        this.image = image;
        this.comments = new ArrayList<>();
    }

    public String getContent() {
        return this.content;
    }

    public String getWhen_posted() {
        return this.when_posted;
    }

    public int getProfile_image() {
        return this.profile_image;
    }

    public int getImage() {
        return this.image;
    }
    public void add_comment(Comment comment) {
        this.comments.add(comment);
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
