package com.example.fakebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends Activity {
    private List<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.posts = generatePosts();
        FeedAdapter feedAdapter = new FeedAdapter(this.posts, this);
        ListView lst = findViewById(R.id.lstFeed);
        lst.setAdapter(feedAdapter);
        TextView error_post = findViewById(R.id.error_empty_post);
        Button submit = findViewById(R.id.submit_btn);
        submit.setOnClickListener(view -> {
            TextView new_post = findViewById(R.id.new_post);
            String post_content = new_post.getText().toString();
            if(!post_content.equals("")) {
                Post post = new Post(post_content, "12/12/13", 1, 2);
                posts.add(0, post);
                error_post.setVisibility(View.GONE);
                feedAdapter.notifyDataSetChanged();
                new_post.setText("");
            }
            else {
                error_post.setVisibility(View.VISIBLE);
            }
        });
    }

    private List<Post> generatePosts() {
        List<Post> posts = new ArrayList<>();
        Post test1 = new Post("Hello World", "12/12/12", 1, 2);
        Comment new_comment = new Comment("wowwwww");
        test1.add_comment(new_comment);
        Post test2 = new Post("Hello World1", "12/12/12", 1, 2);
        Post test3 = new Post("Hello World2", "12/12/12", 1, 2);
        posts.add(test1);
        posts.add(test2);
        posts.add(test3);
        return posts;
    }
}
