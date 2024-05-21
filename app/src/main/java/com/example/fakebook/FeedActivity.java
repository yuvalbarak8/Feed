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
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FeedActivity extends Activity {
    private List<Post> posts;
    private String uri;
    private Context context;
    private Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private JSONObject userJsonObject;
    private String user = "";
    private TextView error_post;
    private TextView bad_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        bad_link = findViewById(R.id.error_bad_link);
        context = this;


        // get the user data
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                // Update the JSON format in the request body
                String json = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
                RequestBody requestBody = RequestBody.create(JSON, json);

                Request request = new Request.Builder()
                        .url("http://" + getString(R.string.ip) + ":" + getString(R.string.port) + "/api/token")
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        userJsonObject = new JSONObject(responseBody).optJSONObject("user");
                        if (userJsonObject != null) {
                            runOnUiThread(() -> {
                                // Handle successful userJsonObject retrieval
                                TextView welcome = findViewById(R.id.welcome_msg);
                                welcome.setText("Hello " + userJsonObject.optString("displayName"));
                                user = userJsonObject.optString("displayName");
                            });
                        } else {
                            // Handle null userJsonObject
                            runOnUiThread(() -> {
                                // Display an error message or handle the null case appropriately
                                Toast.makeText(FeedActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        // Handle unsuccessful response
                        runOnUiThread(() -> {
                            // Display an error message or handle the unsuccessful response appropriately
                            Toast.makeText(FeedActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle network IO exception
                    runOnUiThread(() -> {
                        // Display an error message or handle the network IO exception appropriately
                        Toast.makeText(FeedActivity.this, "Network error occurred", Toast.LENGTH_SHORT).show();
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        //


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
                    FeedAdapter feedAdapter = new FeedAdapter(postList, FeedActivity.this, user,context );
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

        error_post = findViewById(R.id.error_empty_post);
        Button submit = findViewById(R.id.submit_btn);
        submit.setOnClickListener(view -> {
            bad_link.setText("");
            TextView new_post = findViewById(R.id.new_post);
            cancel_img_btn.setVisibility(View.GONE);
            img_select_text.setVisibility(View.GONE);
            String post_content = new_post.getText().toString();
            if (!post_content.equals("")) {
                error_post.setVisibility(View.GONE);
                new_post.setText("");
                sendPostToServer(post_content);
            } else {
                error_post.setText("please write something");
                error_post.setVisibility(View.VISIBLE);
            }
        });
    }
    // Add a method to update the feed after sending a post
    private void updateFeed() {
        fetchPostsFromServer(new OnPostsFetchedListener() {
            @Override
            public void onPostsFetched(List<Post> postList) {
                runOnUiThread(() -> {
                    FeedAdapter feedAdapter = new FeedAdapter(postList, FeedActivity.this, user, context);
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
    }
    private void sendPostToServer(String post_content){
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            // Update the JSON format in the request body
            String json = "{\"display\": \"" + user + "\", \"text\": \"" + post_content + "\", \"img\": \"" + escapeJsonString(uri) + "\", \"profile\": \""+escapeJsonString(userJsonObject.optString("profileImage"))+"\"}";


            RequestBody requestBody = RequestBody.create(JSON, json);

            Request request = null;
            try {
                request = new Request.Builder()
                        .url("http://"+getString(R.string.ip)+":"+getString(R.string.port)+"/api/users/"+userJsonObject.getString("_id")+"/posts")
                        .post(requestBody)
                        //.addHeader("Authorization", "Bearer "+userJsonObject.getString("token"))
                        .build();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                if(response.isSuccessful()) {
                    // Check if the post was successfully uploaded
                    if (!responseBody.equals("null")) {
                        runOnUiThread(() -> {
                            updateFeed();
                            this.bitmap = null;
                            this.uri = null;
                        });
                    } else {
                        runOnUiThread(() -> {
                            // Show a message indicating that the post couldn't be uploaded
                            Toast.makeText(FeedActivity.this, "Failed to upload post, post contain bad link", Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        // Show a message indicating that the request was unsuccessful
                        Toast.makeText(FeedActivity.this, "Failed to send post. Server returned: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("NetworkError", "Error during network operation: " + e.getMessage());
                runOnUiThread(() -> {
                    // Handle the error or show a message to the user
                    Toast.makeText(FeedActivity.this, "Network error occurred", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }


    private void applyLightModeStyles() {
        findViewById(R.id.feed_page).setBackgroundColor(getResources().getColor(R.color.light_background));
        Button toggleModeButton = findViewById(R.id.nightModeButton);
        toggleModeButton.setText("DARK MODE");
        toggleModeButton.setBackgroundColor(getResources().getColor(R.color.dark_background));
    }
    private String escapeJsonString(String input) {
        if (input == null) {
            return null;
        }
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
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
    private String imageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String base64String = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    return "data:image/png;base64," + base64String;
                } finally {
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Consider logging the error instead of just printing the stack trace
        } catch (SecurityException e) {
            e.printStackTrace(); // Handle security exceptions
        }
        return null; // Return null to indicate failure
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
                this.uri = imageToBase64(selectedImageUri);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                // Compress the image

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
    private Bitmap base64ToImage(String base64String) {
        try {
            // Extract base64 data by removing the data URI prefix
            String base64Image = base64String.split(",")[1];

            // Decode base64 string into bytes
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);

            // Convert bytes to Bitmap
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private void fetchPostsFromServer(OnPostsFetchedListener listener) {
        new Thread(() -> {
            List<Post> postList = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://" + getString(R.string.ip) + ":" + getString(R.string.port) + "/api/posts")
                    .get()
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                JSONArray jsonArray = new JSONArray(responseBody);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String imageValue = object.optString("img","");
                    Bitmap bitmapImage = base64ToImage(imageValue);

                    String contentValue = object.optString("text", ""); // Use optString to handle null values
                    // Get the "username" value
                    String usernameValue = object.optString("username", "");
                    // Get the "profile_image" value
                    String profileValue = object.optString("profilePic", "");
                    Bitmap bitmapProfile = base64ToImage(profileValue);
                    // get date
                    String dateValue = object.optString("publishDate","");
                    String id = object.optString("_id","");
                    String user_id = userJsonObject.optString("_id");

                    Post post = new Post(user_id, id, contentValue, usernameValue, bitmapProfile, bitmapImage, dateValue);
                    postList.add(0, post);
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
    public static Bitmap base64ToBitmap(String base64String) {
        // Extract the encoded data from the Base64 string
        String[] parts = base64String.split(",");
        String encodedData = parts[1];

        // Decode the Base64 data
        byte[] decodedData = Base64.decode(encodedData, Base64.DEFAULT);

        // Convert the decoded byte array to Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedData, 0, decodedData.length);

        return bitmap;
    }
    private String encodeBitmapToBase64(Bitmap bitmap) {
        if (bitmap==null)
        {
            return "null";
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
