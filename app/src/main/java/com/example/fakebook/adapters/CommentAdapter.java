package com.example.fakebook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fakebook.R;
import com.example.fakebook.model.Comment;

import java.util.List;

public class CommentAdapter extends BaseAdapter {

    private List<Comment> comments;
    private LayoutInflater inflater;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.comments = comments;
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
   public LayoutInflater getInflater()
   {
    return this.inflater;
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
        viewHolder.comment_delete = convertView.findViewById(R.id.delete_comment_btn);
        viewHolder.comment_edit = convertView.findViewById(R.id.edit_comment_btn);
        viewHolder.text_edit_comment = convertView.findViewById(R.id.text_edit_comment);
        viewHolder.save_comment = convertView.findViewById(R.id.save_comment_btn);
        viewHolder.cancel_comment = convertView.findViewById(R.id.cancel_comment_btn);
        viewHolder.comment_delete.setOnClickListener(v->{
            comments.remove(comment);
            this.notifyDataSetChanged();
        });
        viewHolder.comment_edit.setOnClickListener(v->{
            viewHolder.commentTextView.setVisibility(View.GONE);
            viewHolder.comment_edit.setVisibility(View.GONE);
            viewHolder.comment_delete.setVisibility(View.GONE);
            viewHolder.text_edit_comment.setText(comment.getContent());
            viewHolder.text_edit_comment.setVisibility(View.VISIBLE);
            viewHolder.save_comment.setVisibility(View.VISIBLE);
            viewHolder.cancel_comment.setVisibility(View.VISIBLE);
        });
        viewHolder.cancel_comment.setOnClickListener(v->{
            viewHolder.commentTextView.setVisibility(View.VISIBLE);
            viewHolder.comment_edit.setVisibility(View.VISIBLE);
            viewHolder.comment_delete.setVisibility(View.VISIBLE);
            viewHolder.text_edit_comment.setVisibility(View.GONE);
            viewHolder.save_comment.setVisibility(View.GONE);
            viewHolder.cancel_comment.setVisibility(View.GONE);
        });
        viewHolder.save_comment.setOnClickListener(v->{
            viewHolder.commentTextView.setVisibility(View.VISIBLE);
            viewHolder.comment_edit.setVisibility(View.VISIBLE);
            viewHolder.comment_delete.setVisibility(View.VISIBLE);
            viewHolder.text_edit_comment.setVisibility(View.GONE);
            viewHolder.save_comment.setVisibility(View.GONE);
            viewHolder.cancel_comment.setVisibility(View.GONE);
            String new_comment = viewHolder.text_edit_comment.getText().toString();
            comment.setContent(new_comment);
            this.notifyDataSetChanged();
        });


        return convertView;
    }

    private static class ViewHolder {
        TextView commentTextView;
        Button comment_edit;
        Button comment_delete;
        Button save_comment;
        Button cancel_comment;
        EditText text_edit_comment;
    }
}
