package com.example.fakebook;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private Boolean imageTaken = Boolean.FALSE;
    private Uri profileImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button signupButton = findViewById(R.id.btnSignupTwo);
        Button uploadImageButton = findViewById(R.id.btnUploadImage);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valid();
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void valid() {
        // TextView for tell the user the status of signup
        TextView message = findViewById(R.id.text_sign_up);
        // get data from the user
        EditText username_edit = findViewById(R.id.editTextTextPersonName);
        EditText password_edit = findViewById(R.id.editTextTextPassword);
        EditText password_again_edit = findViewById(R.id.editTextVerifyPassword);
        EditText nickname_edit = findViewById(R.id.nickname);
        // convert data to the type
        String username = username_edit.getText().toString();
        String password = password_edit.getText().toString();
        String password_again = password_again_edit.getText().toString();
        String nickname = nickname_edit.getText().toString();

        // check if all fields are filled in
        if(username.equals("") || password.equals("")|| password_again.equals("")||
                nickname.equals(""))
        {
            message.setVisibility(View.VISIBLE);
            message.setText("All fields are required.");
            message.setTextColor(android.graphics.Color.parseColor("#FF0000"));
            return;
        }
        // Validity check for username
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{6,10}$");
        Matcher matcher = pattern.matcher(username);
        if(!matcher.matches())
        {
            message.setVisibility(View.VISIBLE);
            message.setText("Username must be 6-10 characters" +
                    " without special characters.");
            message.setTextColor(android.graphics.Color.parseColor("#FF0000"));
            return;
        }
        // Validity check for password
        Pattern pattern1 = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,14}$");
        Matcher matcher1 = pattern1.matcher(password);
        if(!matcher1.matches())
        {
            message.setVisibility(View.VISIBLE);
            message.setText("Password must be 8-14 characters with at least one uppercase, one lowercase, and one number, without special characters.");
            message.setTextColor(android.graphics.Color.parseColor("#FF0000"));
            return;
        }
        // Checking whether the two passwords are the same
        if(!password.equals(password_again))
        {
            message.setVisibility(View.VISIBLE);
            message.setText("The passwords you typed are not the same.");
            message.setTextColor(android.graphics.Color.parseColor("#FF0000"));
            return;
        }
        // Validity check for nickname
        Pattern pattern2 = Pattern.compile("^[a-zA-Z\\d]{4,8}$");
        Matcher matcher2 = pattern2.matcher(nickname);
        if(!matcher2.matches())
        {
            message.setVisibility(View.VISIBLE);
            message.setText("Nickname must be 4-8 characters without special characters.");
            message.setTextColor(android.graphics.Color.parseColor("#FF0000"));
            return;
        }
        // Validity check for nickname
        if(!this.imageTaken)
        {
            message.setVisibility(View.VISIBLE);
            message.setText("Please select a valid image file.");
            message.setTextColor(android.graphics.Color.parseColor("#FF0000"));
            return;
        }

        // all is correct
        message.setVisibility(View.VISIBLE);
        message.setText("The registration was successful!");
        message.setTextColor(android.graphics.Color.parseColor("#42b72a"));
        // go to server
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            // Update the JSON format in the request body
            String json = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\", " +
                    "\"token\": \"" + "123" + "\", \"display\": \"" + nickname + "\", " +
                    "\"profile\": \"" + imageToBase64(this.profileImage) + "\"}";

            RequestBody requestBody = RequestBody.create(JSON, json);

            Request request = new Request.Builder()
                    .url("http://"+getString(R.string.ip)+":"+getString(R.string.port)+"/users/")
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                runOnUiThread(() -> {

                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {

                });
            }
        }).start();

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
                    return Base64.encodeToString(byteArray, Base64.NO_WRAP);
                } finally {
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
           Uri image = data.getData();
            if(!Objects.equals(getImageFormat(this, image), "jpeg") &&
                    !Objects.equals(getImageFormat(this, image), "png") &&
                    !Objects.equals(getImageFormat(this, image), "jpg"))
                return;
            TextView message = findViewById(R.id.text_sign_up);
            message.setVisibility(View.GONE);
            ImageView profileImageView = findViewById(R.id.profileImageView);
            profileImageView.setImageURI(image);
            this.imageTaken = true;
            this.profileImage = image;
            profileImageView.setVisibility(View.VISIBLE);
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            this.imageTaken = false;
            // Handle the camera result here, e.g., set the captured image to the ImageView
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ImageView profileImageView = findViewById(R.id.profileImageView);
                profileImageView.setImageBitmap(imageBitmap);
                profileImageView.setVisibility(View.VISIBLE);
            }
        }
    }
}
