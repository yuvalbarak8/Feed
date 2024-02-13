package com.example.fakebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Layout;
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

import java.io.Serializable;
import java.util.List;

public class FeedAdapter extends BaseAdapter {

    List<Post> posts;
    Activity activity;

    private class ViewHolder {
        TextView name;
        TextView when;
        ImageView profile;
        ImageView img;
        Button comments;
        ListView commentList;
        LinearLayout comments_area;
        Button new_comment_btn;
        EditText new_comment_text;
        TextView error_empty_comment;
        Button like_btn;
    }

    public FeedAdapter(List<Post> posts, Activity activity) {
        this.activity = activity;
        this.posts = posts;
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
            viewHolder.when = convertView.findViewById(R.id.feed_post_when);
            viewHolder.profile = convertView.findViewById(R.id.feed_post_profile_img);
            viewHolder.img = convertView.findViewById(R.id.feed_post_img);
            viewHolder.comments = convertView.findViewById(R.id.comments_btn);
            viewHolder.commentList = convertView.findViewById(R.id.commentsListView);
            viewHolder.comments_area = convertView.findViewById(R.id.comments_area);
            viewHolder.new_comment_btn = convertView.findViewById(R.id.new_comment_btn);
            viewHolder.new_comment_text = convertView.findViewById(R.id.new_comment_text);
            viewHolder.error_empty_comment = convertView.findViewById(R.id.error_comment);
            viewHolder.like_btn = convertView.findViewById(R.id.like_btn);

            convertView.setTag(viewHolder);
        }

        Post p = posts.get(position);
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.name.setText(p.getContent());
        viewHolder.when.setText(p.getWhen_posted());
        viewHolder.profile.setImageResource(p.getProfile_image());
        viewHolder.img.setImageResource(p.getImage());
        viewHolder.like_btn.setOnClickListener(v -> {
            viewHolder.like_btn.setBackgroundColor(R.drawable.ic_launcher_background);
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
