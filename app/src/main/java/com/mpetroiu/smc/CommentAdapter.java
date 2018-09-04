package com.mpetroiu.smc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private static final String TAG = "CommentAdapter";

    private Context context;
    private ArrayList<String> names;
    private ArrayList<String> comments;

    public CommentAdapter(Context context, ArrayList<String> names, ArrayList<String> comments) {
        this.context = context;
        this.names = names;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comments_list, parent, false);
        return new CommentAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.name.setText(names.get(position));
        holder.comment.setText(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView name, comment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            name =  itemView.findViewById(R.id.commentName);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}
