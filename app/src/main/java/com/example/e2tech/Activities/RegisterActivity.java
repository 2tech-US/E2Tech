package com.example.e2tech.Activities;


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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText email, password, name, password2;
    private Button mRegister;
    private TextView existaccount;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    FirebaseDatabase database;
    FirebaseUser currentUser;
    private LoginButton btnloginFacebook;
    private CallbackManager callbackManager;
    private FirebaseAuth.AuthStateListener authStateListener;
    private SignInButton btnSignInGoogle;
    private GoogleSignInClient googleSignInClient;
    private int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance("https://e2tech-default-rtdb.asia-southeast1.firebasedatabase.app/");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
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
                String imgUrl = "";

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

        callbackManager = CallbackManager.Factory.create();
        btnloginFacebook = findViewById(R.id.btnLoginFacebook);
        btnloginFacebook.setReadPermissions("email", "public_profile");
        btnloginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FacebookAuth", "onSuccess" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("FacebookAuth", "onCancel");
                Toast.makeText(RegisterActivity.this, "Login cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Log.d("FacebookAuth", "onError" + e);
            }
        });
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Log.d("Auth", "onAuthStateChanged:sign_in:" + user.getUid());
                } else {
                    Log.d("Auth", "onAuthStateChanged:sign_out:");
                }
            }
        };

        btnSignInGoogle = findViewById(R.id.btnLoginGoogle);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            Log.v("USER", "\nUSER ID: " + uid);

                            GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

                            if (signInAccount != null) {
                                String email = signInAccount.getEmail();
                                String uname = signInAccount.getDisplayName();
                                String address = "";
                                String age = "";
                                String gender = "";
                                String phone = "";
                                String imgUrl = "";

                                UserModel newUser = new UserModel(uname, email, address, age, phone, gender, imgUrl);
                                newUser.setId(uid);

                                database.getReference().child("Users").child(uid).setValue(newUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegisterActivity.this, "Hi " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            for (UserInfo profile : user.getProviderData()) {
//                // Id of the provider (ex: google.com)
//                String providerId = profile.getProviderId();
//
//                if (providerId.equals("facebook.com")) {
                    callbackManager.onActivityResult(requestCode, resultCode, data);
//                } else if (providerId.equals("google.com")) {
//                    Log.d("Signed_in_user", "Google signed in");
                    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
                    if (requestCode == RC_SIGN_IN) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Toast.makeText(RegisterActivity.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                            firebaseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e) {
                            Log.w("Google_Sign_in", "Google sign in failed", e);
                        }
                    }
//                }
//            }
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
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
                                                Toast.makeText(RegisterActivity.this, "Hi " + user.getDisplayName(), Toast.LENGTH_LONG).show();
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

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            Log.v("USER", "\nUSER ID: " + uid);

                            String email = user.getEmail();
                            String uname = user.getDisplayName();
                            String address = "";
                            String age = "";
                            String gender = "";
                            String phone = "";
                            String imgUrl = "";

                            UserModel newUser = new UserModel(uname, email, address, age, phone, gender, imgUrl);
                            newUser.setId(uid);

                            database.getReference().child("Users").child(uid).setValue(newUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this, "Hi " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
