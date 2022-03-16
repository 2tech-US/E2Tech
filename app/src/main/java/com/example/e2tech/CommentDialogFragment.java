package com.example.e2tech;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// TODO: 3/17/2022 : Integrate with Products collection (not ProductsPopulars)

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
    FirebaseAuth mAuth;

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

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigateUp();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rating == 0)
                    Toast.makeText(getContext(), "You Must Select Rating Star First", Toast.LENGTH_SHORT).show();
                else {
                    String reviewContent = userReview.getText().toString();
                    String productId = getArguments() != null ? getArguments().getString("productId") : null;

                    mDocRef = db.collection("PopularProducts").document(productId).collection("comment").document();

                    saveComment(rating, reviewContent);
                    navController.navigateUp();
                }
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

    private void saveComment(int rating, String content) {
        FirebaseUser user = mAuth.getCurrentUser();

        Map<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put("rating", rating);
        dataToSave.put("content", content);
        dataToSave.put("email", user != null ? user.getEmail() : null);
        dataToSave.put("createAt", Timestamp.now());
        dataToSave.put("name", user != null ? user.getDisplayName() : null);

        mDocRef.set(dataToSave).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("SAVE_COMMENT", "Document have been save");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("SAVE_COMMENT", "Document was not save");
            }
        });
    }

}