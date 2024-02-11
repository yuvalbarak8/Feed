package com.example.fakebook;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final int COMMENTS_REQUEST_CODE = 1;
    List<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        posts = generatePosts();
        FeedAdapter feedAdapter = new FeedAdapter(posts,this);
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
        submit.setOnClickListener(view -> {
            TextView new_post = findViewById(R.id.new_post);
            String post_content = new_post.getText().toString();
            Post post = new Post(post_content, "12/12/13", 1, 2);
            posts.add(0, post);
            feedAdapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COMMENTS_REQUEST_CODE && resultCode == RESULT_OK) {
            // Retrieve data from the comments page
            String comment = data.getStringExtra("comment");

            // Update the corresponding post with the comment
            if (comment != null && !comment.isEmpty()) {
                posts.get(0).setName(posts.get(0).getName() + "\nComment: " + comment);
            }

            // Update the adapter to reflect the changes
            ((FeedAdapter) ((ListView) findViewById(R.id.lstFeed)).getAdapter()).notifyDataSetChanged();
        }
    }
}
