package com.example.fakebook;
import static com.example.fakebook.JsonFileReader.readJsonFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                    Post post = new Post(post_content, "Israel123", 1);
                    posts.add(0, post);
                }
                else {
                    Post post = new Post(post_content, "Israel123", 1, this.bitmap);
                    posts.add(0, post);
                }

                error_post.setVisibility(View.GONE);
                feedAdapter.notifyDataSetChanged();
                this.bitmap = null;
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
        Button toggleModeButton = findViewById(R.id.nightModeButton);
        toggleModeButton.setText("DARK MODE");
        toggleModeButton.setBackgroundColor(getResources().getColor(R.color.dark_background));
    }

    // Function to apply night mode styles
    private void applyNightModeStyles() {
        // Set background color, text color, or any other styles for night mode
        findViewById(R.id.feed_page).setBackgroundColor(getResources().getColor(R.color.gray));
        Button toggleModeButton = findViewById(R.id.nightModeButton);
        toggleModeButton.setText("LIGHT MODE");
        toggleModeButton.setBackgroundColor(getResources().getColor(R.color.light_background));
    }

    private List<Post> generatePosts() {
        List<Post> posts = new ArrayList<>();
        int resourceId = R.raw.posts;
        String jsonString = readJsonFile(getResources(), resourceId);
        if (jsonString != null) {
            try {
                // Parse the JSON array
                JSONArray jsonArray = new JSONArray(jsonString);
                for(int i =0; i< jsonArray.length();i++) {
                    // Get the object

                        JSONObject object = jsonArray.getJSONObject(i);
                    Bitmap bitmap = null;
                    if(i<6)
                    {
                        Bitmap defaultProfileImage = BitmapFactory.decodeResource(getResources(), R.drawable.nature);
                        if (i % 2 == 1) {
                            defaultProfileImage = BitmapFactory.decodeResource(getResources(), R.drawable.neture2);
                        }

                        // Convert the default profile image to a Base64-encoded string
                        String base64EncodedDefaultImage = encodeBitmapToBase64(defaultProfileImage);
                        object.put("bitmap", base64EncodedDefaultImage);
                        // Get the "bitmap" value
                         bitmap = decodeBase64ToBitmap(object.getString("bitmap"));
                    }
                    // Get the "content" value
                    String contentValue = object.getString("content");
                    // Get the "username" value
                    String usernameValue = object.getString("username");
                    // Get the "profile_image" value
                    int profileValue = object.getInt("profile_image");

                    // Print the "content" value
                    Post post = new Post(contentValue, usernameValue, profileValue, bitmap);
                    posts.add(post);
                }

            } catch (JSONException e) {

            }
        }

        return posts;
    }
    // Method to convert a Base64-encoded string to a Bitmap
    private Bitmap decodeBase64ToBitmap(String base64EncodedString) {
        byte[] decodedBytes = Base64.decode(base64EncodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    // Method to convert a Bitmap to a Base64-encoded string
    private String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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
    public String getImageFormat(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();

        // Try to get the MIME type directly from the ContentResolver
        String mimeType = contentResolver.getType(uri);
        if (mimeType != null) {
            return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }

        // If MIME type is not available, try to extract file extension from URI
        String path = uri.getPath();
        if (path != null) {
            int lastDotIndex = path.lastIndexOf(".");
            if (lastDotIndex != -1) {
                return path.substring(lastDotIndex + 1);
            }
        }

        // If all else fails, return null or a default value based on your requirements
        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            if(!Objects.equals(getImageFormat(this, selectedImageUri), "jpeg") &&
                    !Objects.equals(getImageFormat(this, selectedImageUri), "png") &&
                    !Objects.equals(getImageFormat(this, selectedImageUri), "jpg"))
                return;
            TextView img_select_text = findViewById(R.id.img_selected_text);
            Button cancel_img_btn = findViewById(R.id.cancel_img_btn);
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
            TextView img_select_text = findViewById(R.id.img_selected_text);
            Button cancel_img_btn = findViewById(R.id.cancel_img_btn);
            img_select_text.setVisibility(View.VISIBLE);
            cancel_img_btn.setVisibility(View.VISIBLE);

        }
    }
}
