package com.example.fakebook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    List<Post> posts;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        posts = generatePosts();
        FeedAdapter feedAdapter = new FeedAdapter(posts);
        ListView lst = findViewById(R.id.lstFeed);
        lst.setAdapter(feedAdapter);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post clickedPost = (Post) parent.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, clickedPost.getName(), Toast.LENGTH_LONG).show();
            }
        });

        Button submit = findViewById(R.id.submit_btn);
        submit.setOnClickListener(view ->
                {
                    TextView new_post = findViewById(R.id.new_post);
                    String post_content = new_post.getText().toString();
                    Post post = new Post(post_content, "12/12/13", 1, 1);
                    posts.add(0,post);
                    feedAdapter.notifyDataSetChanged();
                }
                );
    }
    private List<Post> generatePosts()
    {
        List<Post> posts = new ArrayList<>();
        Post test1 = new Post("Hello World", "12/12/12", 1, 2);
        Post test2 = new Post("Hello World1", "12/12/12", 1, 2);
        Post test3 = new Post("Hello World2", "12/12/12", 1, 2);
        posts.add(test1);
        posts.add(test2);
        posts.add(test3);
        return posts;

    }
}
