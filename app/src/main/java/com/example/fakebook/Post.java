package com.example.fakebook;

import java.util.ArrayList;
import java.util.List;

public class Post {

    String name;
    String when_posted;
    int profile_image;
    int image;

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    List<Comment> comments;

    public Post(String name, String when_posted, int profile_image, int image) {
        this.name = name;
        this.when_posted = when_posted;
        this.profile_image = profile_image;
        this.image = image;
        this.comments = new ArrayList<>();
    }
    public String getName() {
        return name;
    }

    public String getWhen_posted() {
        return when_posted;
    }

    public int getProfile_image() {
        return profile_image;
    }

    public int getImage() {
        return image;
    }
    public void select()
    {
        name = this.name + "selected";
    }
    public void add_comment(Comment comment)
    {
        this.comments.add(comment);
    }

    public void setName(String s) {
        this.name=s;
    }

    public List<Comment> getComments() {
        return this.comments;
    }
}
