package com.example.e2tech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e2tech.Adapters.CommentAdapter;
import com.example.e2tech.Models.CommentModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class CommentFragment extends Fragment {

    // TODO: Rename and change types of parameters
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    RecyclerView commentRecycleView;
    CommentAdapter commentAdapter;
    List<CommentModel> commentModelList;

    int[] rating;
    ProgressBar[] progressBars;

    public CommentFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static CommentFragment newInstance() {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_comment, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rating = new int[5];
        progressBars = new ProgressBar[5];

        int[] progressBarsID = {R.id.progress_1,R.id.progress_2,R.id.progress_3,R.id.progress_4,R.id.progress_5};

        for(int i = 0 ; i < 5 ;i++) {
            rating[i] = 0;
            progressBars[i] = root.findViewById(progressBarsID[i]);
        }

        this.settingCommentAdapter(root);

        String productId;
        if (getArguments() != null) {
            productId = getArguments().getString("productId");
            db.collection("Products").document(productId).collection("comment")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                DocumentSnapshot.ServerTimestampBehavior behavior = DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;

                                CommentModel commentModel = documentSnapshot.toObject(CommentModel.class);

                                commentModel.setCreatedAt((Timestamp) documentSnapshot.get("createAt",behavior));
                                commentModelList.add(commentModel);
                                rating[commentModel.getRating()-1]++;
                            }
                            commentAdapter.notifyDataSetChanged();
                            if(!task.getResult().isEmpty()) {
                                for (int i = 0; i < 5; i++) {
                                    progressBars[i].setProgress(rating[i] * 100 / task.getResult().size());
                                }
                            }

                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

        return root;
    }


    private void settingCommentAdapter(View root) {
        commentModelList = new ArrayList<>();
        commentRecycleView = root.findViewById(R.id.comment_usercomment_recyclerview);
        commentRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        commentAdapter = new CommentAdapter(getActivity(), commentModelList);
        commentRecycleView.setAdapter(commentAdapter);
    }
}