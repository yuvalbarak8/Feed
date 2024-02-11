package com.example.fakebook;

public class Post {

    String name;
    String when_posted;
    int profile_image;
    int image;

    public Post(String name, String when_posted, int profile_image, int image) {
        this.name = name;
        this.when_posted = when_posted;
        this.profile_image = profile_image;
        this.image = image;
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

}
