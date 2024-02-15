package com.example.fakebook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button signupButton = findViewById(R.id.btnSignupTwo);
        Button uploadImageButton = findViewById(R.id.btnUploadImage);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUsername();
                validatePassword();
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });
    }
    private void validateUsername() {
        EditText usernameEditText = findViewById(R.id.editTextTextPersonName);
        TextView errorTextView = findViewById(R.id.textViewUsernameError); // Reusing the password error TextView for simplicity

        String username = usernameEditText.getText().toString().trim();
        Pattern pattern = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]{3,19}$");
        Matcher matcher = pattern.matcher(username);

        if (username.isEmpty()) {
            errorTextView.setText("Username cannot be empty");
        } else if (!matcher.matches()) {
            errorTextView.setText("Invalid username. Must have alphabet letters, and numbers");
        } else {
            errorTextView.setText("Good Username"); // Clear any previous error messages
        }
    }

    private void validatePassword() {
        EditText passwordEditText = findViewById(R.id.editTextTextPassword);
        TextView errorTextView = findViewById(R.id.textViewPasswordError);

        String password = passwordEditText.getText().toString();
        // Updated pattern to include at least one digit, one lowercase letter, one uppercase letter, and one special character
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?]).{8,}$");
        Matcher matcher = pattern.matcher(password);

        if (password.isEmpty()) {
            errorTextView.setText("Password cannot be empty");
        } else if (password.length() < 8) {
            errorTextView.setText("Password must be at least 8 characters long");
        } else if (!matcher.matches()) {
            errorTextView.setText("Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character");
        } else {
            errorTextView.setText("Good Password"); // Indicate the password is strong/correct
            // Add your signup logic here
        }
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
            Uri imageUri = data.getData();
            ImageView profileImageView = findViewById(R.id.profileImageView);
            profileImageView.setImageURI(imageUri);
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            // Handle the camera result here, e.g., set the captured image to the ImageView
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ImageView profileImageView = findViewById(R.id.profileImageView);
                profileImageView.setImageBitmap(imageBitmap);
            }
        }
    }
}
