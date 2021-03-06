package com.example.e2tech.Activities;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e2tech.MainActivity;
import com.example.e2tech.Models.UserModel;
import com.example.e2tech.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText email, password, name, password2;
    private Button mRegister;
    private TextView existaccount;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    FirebaseDatabase database;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance("https://e2tech-default-rtdb.asia-southeast1.firebasedatabase.app/");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("????ng k??");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        email = findViewById(R.id.register_email);
        name = findViewById(R.id.register_name);
        password = findViewById(R.id.register_password);
        password2 = findViewById(R.id.register_password2);
        mRegister = findViewById(R.id.register_button);
        existaccount = findViewById(R.id.btn_signin);
        progressBar = findViewById(R.id.register_progressbar);

        mAuth = FirebaseAuth.getInstance();


        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = email.getText().toString().trim();
                String unameStr = name.getText().toString().trim();
                String passStr = password.getText().toString().trim();
                String pass2Str = password2.getText().toString().trim();
                String address = "";
                String age = "";
                String gender = "";
                String phone = "";
                String imgUrl = "https://i.stack.imgur.com/34AD2.jpg";

                Log.v("REGIS", "row 72");

                if (TextUtils.isEmpty(unameStr)) {
                    name.setError("Username is empty");
                    name.setFocusable(true);
                    return;
                }

                if (TextUtils.isEmpty(emailStr)) {
                    email.setError("Email is empty");
                    email.setFocusable(true);
                    return;
                }

                if (TextUtils.isEmpty(passStr)) {
                    password.setError("Password is empty");
                    password.setFocusable(true);
                    return;
                }

                if (TextUtils.isEmpty(pass2Str)) {
                    password2.setError("Password retype is empty");
                    password2.setFocusable(true);
                    return;
                }

                if (!passStr.equals(pass2Str)) {
                    password2.setError("Password retype is wrong");
                    password2.setFocusable(true);
                    return;
                }
                Log.v("REGIS", "row 101");


                if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                    email.setError("Invalid Email");
                    email.setFocusable(true);
                } else if (passStr.length() < 6) {
                    password.setError("Length Must be greater than 6 character");
                    password.setFocusable(true);
                } else {
                    Log.v("REGIS", "Before REGISTER");
                    registerUser(emailStr, passStr, unameStr, address, age, gender, phone, imgUrl);
                    Log.v("REGIS", "After REGISTER");

                }
            }
        });
        existaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }


    private void registerUser(String emaill, final String pass, final String uname, final String address,
                              final String age, final String gender, final String phone, final String imgUrl) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(emaill, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            Log.v("USER", "\nUSER ID: " + uid);

                            UserModel newUser = new UserModel(uname, emaill, pass, address, age, phone, gender, imgUrl);
                            newUser.setId(uid);

                            database.getReference().child("Users").child(uid).setValue(newUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
//                                                Toast.makeText(RegisterActivity.this, "Hi " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(loginIntent);
                                                finish();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(RegisterActivity.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Error Occured" + e.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
