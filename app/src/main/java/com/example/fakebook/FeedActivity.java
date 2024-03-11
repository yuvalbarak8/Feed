package com.example.fakebook;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FeedActivity extends Activity {
    private List<Post> posts;
    private Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private JSONObject userJsonObject;
    private String user = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // get the user data
        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("user");
        try {
            assert jsonString != null;
            userJsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // welcome message
        TextView welcome = findViewById(R.id.welcome_msg);
        try {
            welcome.setText("Hello " + userJsonObject.getString("username"));
            this.user = userJsonObject.getString("username");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Button logoutButton = findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, MainActivity.class);
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
        cancel_img_btn.setOnClickListener(v ->
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

        this.fetchPostsFromServer(new OnPostsFetchedListener() {
            @Override
            public void onPostsFetched(List<Post> postList) {
                runOnUiThread(() -> {
                    FeedAdapter feedAdapter = new FeedAdapter(postList, FeedActivity.this, user);
                    ListView lst = findViewById(R.id.lstFeed);
                    lst.setAdapter(feedAdapter);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(FeedActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });

        TextView error_post = findViewById(R.id.error_empty_post);
        Button submit = findViewById(R.id.submit_btn);
        submit.setOnClickListener(view -> {
            TextView new_post = findViewById(R.id.new_post);
            cancel_img_btn.setVisibility(View.GONE);
            img_select_text.setVisibility(View.GONE);
            String post_content = new_post.getText().toString();
            if (!post_content.equals("")) {
                if (bitmap == null) {
                    Post post = new Post(post_content, "Israel123", 1);
                    posts.add(0, post);
                } else {
                    Post post = new Post(post_content, "Israel123", 1, bitmap);
                    posts.add(0, post);
                }

                error_post.setVisibility(View.GONE);
                // Assuming posts is a global variable
                FeedAdapter feedAdapter = new FeedAdapter(posts, FeedActivity.this, user);
                ListView lst = findViewById(R.id.lstFeed);
                lst.setAdapter(feedAdapter);
                bitmap = null;
                new_post.setText("");
            } else {
                error_post.setVisibility(View.VISIBLE);
            }
        });
    }

    private void applyLightModeStyles() {
        findViewById(R.id.feed_page).setBackgroundColor(getResources().getColor(R.color.light_background));
        Button toggleModeButton = findViewById(R.id.nightModeButton);
        toggleModeButton.setText("DARK MODE");
        toggleModeButton.setBackgroundColor(getResources().getColor(R.color.dark_background));
    }

    private void applyNightModeStyles() {
        findViewById(R.id.feed_page).setBackgroundColor(getResources().getColor(R.color.gray));
        Button toggleModeButton = findViewById(R.id.nightModeButton);
        toggleModeButton.setText("LIGHT MODE");
        toggleModeButton.setBackgroundColor(getResources().getColor(R.color.light_background));
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
        String mimeType = contentResolver.getType(uri);
        if (mimeType != null) {
            return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }

        String path = uri.getPath();
        if (path != null) {
            int lastDotIndex = path.lastIndexOf(".");
            if (lastDotIndex != -1) {
                return path.substring(lastDotIndex + 1);
            }
        }

        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            if (!Objects.equals(getImageFormat(this, selectedImageUri), "jpeg") &&
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

    private interface OnPostsFetchedListener {
        void onPostsFetched(List<Post> postList);
        void onError(String errorMessage);
    }

    private void fetchPostsFromServer(OnPostsFetchedListener listener) {
        new Thread(() -> {
            List<Post> postList = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://" + getString(R.string.ip) + ":" + getString(R.string.port) + "/posts")
                    .get()
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                JSONArray jsonArray = new JSONArray(responseBody);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Bitmap bitmap = null;
                    if (i < 6) {
                        Bitmap defaultProfileImage = BitmapFactory.decodeResource(getResources(), R.drawable.nature);
                        if (i % 2 == 1) {
                            defaultProfileImage = BitmapFactory.decodeResource(getResources(), R.drawable.neture2);
                        }

                        String base64EncodedDefaultImage = encodeBitmapToBase64(defaultProfileImage);
                        object.put("bitmap", base64EncodedDefaultImage);
                        bitmap = decodeBase64ToBitmap(object.getString("bitmap"));
                    }
                    String contentValue = object.optString("text", ""); // Use optString to handle null values
                    // Get the "username" value
                    String usernameValue = object.optString("username", "");
                    // Get the "profile_image" value
                    int profileValue = object.optInt("profilePic", 0);

                    Post post = new Post(contentValue, usernameValue, profileValue, bitmap);
                    postList.add(post);
                }

                runOnUiThread(() -> listener.onPostsFetched(postList));

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                runOnUiThread(() -> listener.onError("Failed to fetch posts. Please try again."));
            }
        }).start();
    }

    private Bitmap decodeBase64ToBitmap(String base64EncodedString) {
        byte[] decodedBytes = Base64.decode(base64EncodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
