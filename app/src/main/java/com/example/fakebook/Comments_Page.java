package com.example.fakebook;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class Comments_Page extends AppCompatActivity {

    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_page);

        ListView commentsListView = findViewById(R.id.commentsListView);
        EditText commentEditText = findViewById(R.id.commentEditText);
        Button addCommentButton = findViewById(R.id.addCommentButton);
        Button back = findViewById(R.id.back);

        commentAdapter = new CommentAdapter(this); // Create a custom adapter for comments, you need to implement this adapter

        Intent intent = getIntent();
        if (intent != null) {
            int postIndex = intent.getIntExtra("postIndex", -1);

            if (postIndex != -1) {
                Post post = ((MainActivity) getParent()).posts.get(postIndex);
                commentAdapter.setComments(post.getComments());
            }
        }

        commentsListView.setAdapter(commentAdapter);

        addCommentButton.setOnClickListener(view -> {
            String newComment = commentEditText.getText().toString();
            if (!newComment.isEmpty()) {
                commentAdapter.addComment(new Comment(newComment));
                commentEditText.setText(""); // Clear the EditText after adding a comment
            }
        });

        back.setOnClickListener(view -> {
            // Pass the comments back to MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("comments", commentAdapter.getComments());
            setResult(RESULT_OK, resultIntent);

            // Finish the current activity
            finish();
        });
    }
}
