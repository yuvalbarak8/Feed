package com.example.fakebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSign = findViewById(R.id.btnSignup);
        btnSign.setOnClickListener(view -> {
            Intent i = new Intent(this, SignUp.class);
            startActivity(i);
        });

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view -> {
            TextView username = findViewById(R.id.login_username);
            TextView password = findViewById(R.id.login_password);
            String user_input = username.getText().toString();
            String pass_input = password.getText().toString();
            if(!user_input.equals("Israel123") && !pass_input.equals("Israel123")) {
                Intent i = new Intent(this, FeedActivity.class);
                startActivity(i);
            }
            else
            {
                findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
            }
        });
    }
}