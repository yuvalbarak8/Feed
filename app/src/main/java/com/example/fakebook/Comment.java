package com.example.fakebook;

import java.io.Serializable;

public class Comment implements Serializable {
    private String content;
    public Comment(String content)
    {
        this.content = content;
    }
    public String getContent()
    {
        return this.content;
    }

}
