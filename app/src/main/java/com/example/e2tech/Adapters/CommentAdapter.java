package com.example.e2tech.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e2tech.Models.CommentModel;
import com.example.e2tech.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    Context context;
    List<CommentModel> commentModelList;
    SimpleDateFormat sfd;


    public CommentAdapter(Context context, List<CommentModel> comments) {
        this.context = context;
        this.commentModelList = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);

        sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvCommentUserName.setText(commentModelList.get(position).getEmail());
        holder.rbCommentRatingBar.setRating(commentModelList.get(position).getRating());
        holder.tvCommentUserReview.setText(commentModelList.get(position).getContent());

        String createAt = sfd.format(commentModelList.get(position).getCreatedAt().toDate());
        holder.tvCommentReviewDate.setText(createAt);
    }

    @Override
    public int getItemCount() {
        return commentModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommentUserName;
        TextView tvCommentUserReview;
        TextView tvCommentReviewDate;
        RatingBar rbCommentRatingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommentUserName = itemView.findViewById(R.id.tv_comment_username);
            rbCommentRatingBar = itemView.findViewById(R.id.comment_ratingbar);
            tvCommentUserReview = itemView.findViewById(R.id.comment_tv_reviewcontent);
            tvCommentReviewDate = itemView.findViewById(R.id.comment_tv_date);

        }
    }
}
