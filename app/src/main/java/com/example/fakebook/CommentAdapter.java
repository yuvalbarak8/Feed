package com.example.fakebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends BaseAdapter {

    private List<Comment> comments;
    private LayoutInflater inflater;

    public CommentAdapter(Context context) {
        //this.comments = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        notifyDataSetChanged();
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.comment_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.commentTextView = convertView.findViewById(R.id.commentTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Comment comment = comments.get(position);
        viewHolder.commentTextView.setText(comment.getContent());

        return convertView;
    }

    private static class ViewHolder {
        TextView commentTextView;
    }
}
