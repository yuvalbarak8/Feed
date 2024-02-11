package com.example.fakebook;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

public class FeedAdapter extends BaseAdapter {

    List<Post> posts;
    Activity activity;

    private class ViewHolder{
        TextView name;
        TextView when;
        ImageView profile;
        ImageView img;
        Button comments;
    }

    public FeedAdapter(List<Post> posts, Activity activity) {
        this.activity=activity;
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

    // ...

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

            convertView.setTag(viewHolder);
        }

        Post p = posts.get(position);
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.name.setText(p.getName());
        viewHolder.when.setText(p.getWhen_posted());
        viewHolder.profile.setImageResource(p.getProfile_image());
        viewHolder.img.setImageResource(p.getImage());
        viewHolder.comments.setOnClickListener(v -> {
            Intent i = new Intent(activity,Comments_Page.class);
            i.putExtra("current_comments",(Serializable)p.getComments());
           activity.startActivity(i);
        });

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Clicked on image", Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }

}
