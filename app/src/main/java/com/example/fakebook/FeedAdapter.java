package com.example.fakebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Layout;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FeedAdapter extends BaseAdapter {

    List<Post> posts;
    Activity activity;
    private String user;
    private Context context;

    private class ViewHolder {
        TextView time;
        TextView name;
        TextView username;
        ImageView profile;
        ImageView img;
        Button comments;
        ListView commentList;
        LinearLayout comments_area;
        Button new_comment_btn;
        EditText new_comment_text;
        TextView error_empty_comment;
        Button like_btn;
        Button edit_btn;
        EditText edit_post_text;
        Button cancel_edit;
        Button save_edit;
        Button delete_post_btn;
    }

    public FeedAdapter(List<Post> posts, Activity activity, String user, Context context) {
        this.activity = activity;
        this.posts = posts;
        this.user = user;
        this.context = context;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_post_layout, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.feed_post_name);
            viewHolder.username = convertView.findViewById(R.id.feed_username);
            viewHolder.profile = convertView.findViewById(R.id.feed_post_profile_img);
            viewHolder.img = convertView.findViewById(R.id.feed_post_img);
            viewHolder.comments = convertView.findViewById(R.id.comments_btn);
            viewHolder.commentList = convertView.findViewById(R.id.commentsListView);
            viewHolder.comments_area = convertView.findViewById(R.id.comments_area);
            viewHolder.new_comment_btn = convertView.findViewById(R.id.new_comment_btn);
            viewHolder.new_comment_text = convertView.findViewById(R.id.new_comment_text);
            viewHolder.error_empty_comment = convertView.findViewById(R.id.error_comment);
            viewHolder.like_btn = convertView.findViewById(R.id.like_btn);
            viewHolder.cancel_edit = convertView.findViewById(R.id.cancel_edit);
            viewHolder.save_edit = convertView.findViewById(R.id.save_edit);
            viewHolder.edit_post_text = convertView.findViewById(R.id.editPost);
            viewHolder.edit_btn = convertView.findViewById(R.id.edit_btn);
            viewHolder.time = convertView.findViewById(R.id.time);
            viewHolder.delete_post_btn = convertView.findViewById(R.id.delete_post_btn);
            convertView.setTag(viewHolder);
        }
        Post p = posts.get(position);
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.name.setText(p.getContent());
        viewHolder.username.setText(p.getUsername());
        viewHolder.time.setText(p.getDate());
        viewHolder.profile.setImageBitmap(p.getProfile_image());
        if(p.getPost_image()==null) {
            viewHolder.img.setVisibility(View.GONE);
        }
        else {
            viewHolder.img.setVisibility(View.VISIBLE);
            viewHolder.img.setImageBitmap(p.getPost_image());
        }
        viewHolder.like_btn.setOnClickListener(v -> {
            if(viewHolder.like_btn.getBackground()==null) {
                viewHolder.like_btn.setBackgroundColor(R.drawable.ic_launcher_background);
            }
            else {
                viewHolder.like_btn.setBackground(null);
            }
        });
        // gone edit and delete from who is not the user
        if(!Objects.equals(p.getUsername(), user))
        {
            viewHolder.edit_btn.setVisibility(View.GONE);
            viewHolder.delete_post_btn.setVisibility(View.GONE);
        }
        // click on edit button
        viewHolder.edit_btn.setOnClickListener(v->{
            viewHolder.edit_post_text.setText(p.getContent());
            viewHolder.edit_post_text.setVisibility(View.VISIBLE);
            viewHolder.edit_btn.setVisibility(View.GONE);
            viewHolder.name.setVisibility(View.GONE);
            viewHolder.save_edit.setVisibility(View.VISIBLE);
            viewHolder.cancel_edit.setVisibility(View.VISIBLE);
            viewHolder.img.setVisibility(View.GONE);
        });
        // when cancel the edit
        viewHolder.cancel_edit.setOnClickListener(v->{
            viewHolder.edit_post_text.setVisibility(View.GONE);
            viewHolder.edit_btn.setVisibility(View.VISIBLE);
            viewHolder.name.setVisibility(View.VISIBLE);
            viewHolder.save_edit.setVisibility(View.GONE);
            viewHolder.cancel_edit.setVisibility(View.GONE);
            if(p.getPost_image()!=null) {
                viewHolder.img.setVisibility(View.VISIBLE);
            }
        });
        // when save the changes
        viewHolder.save_edit.setOnClickListener(v->{
            String new_text = viewHolder.edit_post_text.getText().toString();
            p.setContent(new_text);
            // edit to server
            new Thread(() -> {
                OkHttpClient client = new OkHttpClient();

                // Construct the URL with the appropriate path parameter
                String url = "http://" + context.getResources().getString(R.string.ip) + ":" +
                        context.getResources().getString(R.string.port) +
                        "/api/users/" + p.getUser_Id() + "/posts/" + p.getId();

                // Prepare the JSON request body
                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.put("text", new_text);
                    requestBody.put("img","");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());

                Request request = new Request.Builder()
                        .url(url)
                        .patch(body) // Use PUT method
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String responseBody = response.body().string();

                    // Handle response

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();


            //
            this.notifyDataSetChanged();
            viewHolder.edit_post_text.setVisibility(View.GONE);
            viewHolder.edit_btn.setVisibility(View.VISIBLE);
            viewHolder.name.setVisibility(View.VISIBLE);
            viewHolder.save_edit.setVisibility(View.GONE);
            viewHolder.cancel_edit.setVisibility(View.GONE);
        });
        // delete post
        viewHolder.delete_post_btn.setOnClickListener(v->{
            posts.remove(p);
            // delete from server
            new Thread(() -> {
                OkHttpClient client = new OkHttpClient();

                // Construct the URL with the appropriate path parameter
                String url = "http://" + context.getResources().getString(R.string.ip) + ":" +
                        context.getResources().getString(R.string.port) +
                        "/api/users/:"+p.getUser_Id()+"/posts/" + p.getId();

                Request request = new Request.Builder()
                        .url(url)
                        .delete() // Use DELETE method
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String responseBody = response.body().string();

                    // Handle response

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            //
            this.notifyDataSetChanged();
        });

        CommentAdapter commentAdapter = new CommentAdapter(this.activity, p.getComments());
        viewHolder.commentList.setAdapter(commentAdapter);
        viewHolder.comments.setOnClickListener(v -> {
            if(viewHolder.comments_area.getVisibility()==View.GONE) {
                viewHolder.comments_area.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.comments_area.setVisibility(View.GONE);
            }
        });
        viewHolder.new_comment_btn.setOnClickListener(v->
        {
            String text = viewHolder.new_comment_text.getText().toString();
            if(!text.equals("")) {
                p.add_comment(new Comment(text));
                commentAdapter.notifyDataSetChanged();
                viewHolder.error_empty_comment.setVisibility(View.GONE);
                viewHolder.new_comment_text.setText("");
            }
            else {
                viewHolder.error_empty_comment.setVisibility(View.VISIBLE);
            }
        });
        return convertView;
    }

}
