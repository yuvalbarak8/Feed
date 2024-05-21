package com.example.fakebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.net.SocketTimeoutException;

public class MainActivity extends AppCompatActivity {
    private JSONObject userJsonObject;

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

            new Thread(() -> {
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                String json = "{\"username\": \"" + escapeJsonString(user_input) + "\", \"password\": \"" + escapeJsonString(pass_input) + "\"}";
                RequestBody requestBody = RequestBody.create(JSON, json);

                Request request = new Request.Builder()
                        .url("http://" + getString(R.string.ip) + ":" + getString(R.string.port) + "/api/token")
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String responseBody = response.body().string();

                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                if (jsonResponse.isNull("user")) {
                                    showLoginError("Invalid username or password.");
                                } else {
                                    userJsonObject = jsonResponse.getJSONObject("user");
                                    findViewById(R.id.login_error).setVisibility(View.GONE);
                                    findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
                                    findViewById(R.id.correct).setVisibility(View.VISIBLE);

                                    Intent i = new Intent(this, FeedActivity.class);
                                    i.putExtra("username", userJsonObject.optString("username"));
                                    i.putExtra("password", userJsonObject.optString("password"));
                                    startActivity(i);
                                }
                            } catch (JSONException e) {
                                showLoginError("Failed to parse response.");
                            }
                        } else {
                            showLoginError("Invalid username or password.");
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        if (e instanceof SocketTimeoutException) {
                            showLoginError("Invalid username or password.");
                        } else {
                            showLoginError("Network error: " + e.toString());
                        }
                    });
                }
            }).start();
        });
    }

    private void showLoginError(String errorMessage) {
        TextView login_error = findViewById(R.id.login_error);
        findViewById(R.id.correct).setVisibility(View.GONE);
        login_error.setText(errorMessage);
        login_error.setVisibility(View.VISIBLE);
        findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
    }

    private String escapeJsonString(String input) {
        if (input == null) {
            return null;
        }
        return input
                .replace("\\", "\\\\")  // Escape backslashes
                .replace("\"", "\\\"")  // Escape double quotes
                .replace("\b", "\\b")   // Escape backspace
                .replace("\f", "\\f")   // Escape form feed
                .replace("\n", "\\n")   // Escape newline
                .replace("\r", "\\r")   // Escape carriage return
                .replace("\t", "\\t");  // Escape tab
    }
}
