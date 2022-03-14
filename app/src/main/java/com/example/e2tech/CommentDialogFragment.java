package com.example.e2tech;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommentDialogFragment extends DialogFragment implements View.OnClickListener {

    ImageView[] ivRatings = new ImageView[5];
    List<Integer> rates = new ArrayList<>(Arrays.asList(R.id.iv_comment_rating_1,
            R.id.iv_comment_rating_2, R.id.iv_comment_rating_3, R.id.iv_comment_rating_4,
            R.id.iv_comment_rating_5));

    int rating = 0;


    EditText userReview;

    Button btnCancel;
    Button btnSend;

    NavController navController;

    FirebaseFirestore db;
    private DocumentReference mDocRef;

    public CommentDialogFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_comment_dialog, container, false);

        navController = NavHostFragment.findNavController(this);


        for (int i = 0; i < ivRatings.length; i++) {
            ivRatings[i] = root.findViewById(rates.get(i));
            ivRatings[i].setOnClickListener(this);
        }


        userReview = root.findViewById(R.id.et_comment_review);

        btnCancel = root.findViewById(R.id.btn_comment_cancel);
        btnSend = root.findViewById(R.id.btn_comment_send);

        String productId = getArguments() != null ? getArguments().getString("productId") : null;
        String userEmail = getArguments() != null ? getArguments().getString("userEmail") : null;

        db = FirebaseFirestore.getInstance();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigateUp();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reviewContent = userReview.getText().toString();
                String productId = getArguments() != null ? getArguments().getString("productId") : null;
                String userEmail = getArguments() != null ? getArguments().getString("userEmail") : null;

                mDocRef = db.collection("PopularProducts").document(productId).collection("comment").document();

                saveComment(userEmail,rating,reviewContent);

                navController.navigateUp();
            }
        });

        return root;

    }

    @Override
    public void onClick(View view) {
        rating = rates.indexOf(view.getId()) + 1;
        handleRating(rating);
    }

    private void handleRating(int rating) {
        for (int i = 0; i < rating; i++) {
            ivRatings[i].setImageResource(R.drawable.ic_baseline_star_24_yellow);
        }

        for (int i = rating; i < ivRatings.length; i++) {
            ivRatings[i].setImageResource(R.drawable.ic_baseline_star_24_grey);

        }

    }

    private void saveComment(String email ,int rating, String content) {
        Map<String,Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put("rating",rating);
        dataToSave.put("content",content);
        dataToSave.put("email",email);
        dataToSave.put("createAt",Timestamp.now());


        mDocRef.set(dataToSave).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("SAVE_COMMENT","Document have been save");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("SAVE_COMMENT","Document was not save");
            }
        });
    }

}