package com.example.fakebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends Activity {
    private List<Post> posts;
    private Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Button logoutButton = findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this,MainActivity.class);

                // Optional: If you want to clear all previous activities on the stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Start MainActivity
                startActivity(intent);
                // Optionally, if you want to finish the current activity
                finish();
            }
        });


        Button cancel_img_btn = findViewById(R.id.cancel_img_btn);
        TextView img_select_text = findViewById(R.id.img_selected_text);
        cancel_img_btn.setOnClickListener(v->
        {
            cancel_img_btn.setVisibility(View.GONE);
            img_select_text.setVisibility(View.GONE);
            this.bitmap = null;
        });
        Button upload_img_btn = findViewById(R.id.upload_img_btn);
        upload_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });
        Button toggleModeButton = findViewById(R.id.nightModeButton);
        toggleModeButton.setOnClickListener(new View.OnClickListener() {
            private boolean isNightMode = false;

            @Override
            public void onClick(View v) {
                // Toggle between night mode and light mode
                if (isNightMode) {
                    // Apply light mode styles
                    applyLightModeStyles();
                    this.isNightMode = false;
                } else {
                    // Apply night mode styles
                    applyNightModeStyles();
                    this.isNightMode = true;
                }

            }
        });

        this.posts = generatePosts();
        FeedAdapter feedAdapter = new FeedAdapter(this.posts, this);
        ListView lst = findViewById(R.id.lstFeed);
        lst.setAdapter(feedAdapter);
        TextView error_post = findViewById(R.id.error_empty_post);
        Button submit = findViewById(R.id.submit_btn);
        submit.setOnClickListener(view -> {
            TextView new_post = findViewById(R.id.new_post);
            cancel_img_btn.setVisibility(View.GONE);
            img_select_text.setVisibility(View.GONE);
            String post_content = new_post.getText().toString();
            if(!post_content.equals("")) {
                if(bitmap==null) {
                    Post post = new Post(post_content, "12/12/13", 1);
                    posts.add(0, post);
                }
                else {
                    Post post = new Post(post_content, "12/12/13", 1, this.bitmap);
                    posts.add(0, post);
                }

                error_post.setVisibility(View.GONE);
                feedAdapter.notifyDataSetChanged();
                new_post.setText("");
            }
            else {
                error_post.setVisibility(View.VISIBLE);
            }
        });
    }
    // Function to apply light mode styles
    private void applyLightModeStyles() {
        // Set background color, text color, or any other styles for light mode
        findViewById(R.id.feed_page).setBackgroundColor(getResources().getColor(R.color.light_background));
    }

    // Function to apply night mode styles
    private void applyNightModeStyles() {
        // Set background color, text color, or any other styles for night mode
        findViewById(R.id.feed_page).setBackgroundColor(getResources().getColor(R.color.dark_background));
    }

    private List<Post> generatePosts() {
        List<Post> posts = new ArrayList<>();
        Post test1 = new Post("Hello World", "12/12/12", 1);
        Comment new_comment = new Comment("wowwwww");
        test1.add_comment(new_comment);
        Post test2 = new Post("Hello World1", "12/12/12", 1);
        Post test3 = new Post("Hello World2", "12/12/12", 1);
        posts.add(test1);
        posts.add(test2);
        posts.add(test3);
        return posts;
    }
    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option")
                .setItems(new CharSequence[]{"Gallery", "Camera"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                pickImageFromGallery();
                                break;
                            case 1:
                                pickImageFromCamera();
                                break;
                        }
                    }
                })
                .show();
    }

    private void pickImageFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView img_select_text = findViewById(R.id.img_selected_text);
        Button cancel_img_btn = findViewById(R.id.cancel_img_btn);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                this.bitmap = bitmap;
                img_select_text.setVisibility(View.VISIBLE);
                cancel_img_btn.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            this.bitmap = bitmap;
            img_select_text.setVisibility(View.VISIBLE);
            cancel_img_btn.setVisibility(View.VISIBLE);

        }
    }
}
