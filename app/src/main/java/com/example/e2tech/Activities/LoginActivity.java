package com.example.e2tech.Activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e2tech.AdminActivity;
import com.example.e2tech.MainActivity;
import com.example.e2tech.Models.UserModel;
import com.example.e2tech.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
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
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password, name;
    private Button mlogin, btnLoginAdmin;
    private TextView tvRegister, tvForgetPassword;
    private ProgressBar progressBar;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private LoginButton btnloginFacebook;
    private CallbackManager callbackManager;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    private SignInButton btnSignInGoogle;
    private GoogleSignInClient googleSignInClient;
    FirebaseDatabase database;
    private int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Đăng nhập");
        actionBar.hide();
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        // initialising the layout items
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        tvRegister = findViewById(R.id.needs_new_account);
        tvForgetPassword = findViewById(R.id.forget_password);
        progressBar = findViewById(R.id.login_progressbar);

        mAuth = FirebaseAuth.getInstance();
        mlogin = findViewById(R.id.login_button);
        btnLoginAdmin = findViewById(R.id.btn_admin_login);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://e2tech-default-rtdb.asia-southeast1.firebasedatabase.app/");
        FacebookSdk.sdkInitialize(getApplicationContext());

        // checking if user is null or not
        if (mAuth != null) {
            currentUser = mAuth.getCurrentUser();
        }

        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emaill = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                // if format of email doesn't matches return null
                if (!Patterns.EMAIL_ADDRESS.matcher(emaill).matches()) {
                    email.setError("Invalid Email");
                    email.setFocusable(true);

                } else {
                    loginUser(emaill, pass);
                }
            }
        });

        // If new account then move to Registration Activity
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // Reset Your Password using email
        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showRecoverPasswordDialog();
                Intent intent = new Intent(LoginActivity.this, ForgetPassActivity.class);
                startActivity(intent);
            }
        });


        btnLoginAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emaill = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                // if format of email doesn't matches return null
                if (!Patterns.EMAIL_ADDRESS.matcher(emaill).matches() || !emaill.equals("thientoancubi@gmail.com")) {
                    email.setError("Invalid Email");
                    email.setFocusable(true);

                } else {
                    loginAdmin(emaill, pass);
                }

            }
        });

        callbackManager = CallbackManager.Factory.create();
        btnloginFacebook = findViewById(R.id.btnLoginFacebook);
        btnloginFacebook.setReadPermissions(Arrays.asList("email", "public_profile"));
        btnloginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FacebookAuth", "onSuccess" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("FacebookAuth", "onCancel");
                Toast.makeText(LoginActivity.this, "Login cancelled", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Log.d("Auth", "onAuthStateChanged:sign_out:");
                }
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(@Nullable AccessToken accessToken, @Nullable AccessToken accessToken1) {
                if (accessToken1 == null) {
                    mAuth.signOut();
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
                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();


                            GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

                            if (signInAccount != null) {
                                String email = signInAccount.getEmail();
                                String uname = signInAccount.getDisplayName();
                                String address = "";
                                String age = "";
                                String gender = "";
                                String phone = "";
                                String imgUrl = "";

                                imgUrl = signInAccount.getPhotoUrl().toString();
                                imgUrl += "?type=large";

                                UserModel newUser = new UserModel(uname, email, address, age, phone, gender, imgUrl);
                                newUser.setId(uid);

                                database.getReference().child("Users").child(uid).setValue(newUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(LoginActivity.this, "Hi " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(LoginActivity.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("Google_Sign_in", "Google sign in failed", e);
            }
        }
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

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
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

                            Log.d("UserEmail", email);

                            if (user.getPhotoUrl() != null) {
                                imgUrl = user.getPhotoUrl().toString();
                                imgUrl += "?type=large";
                            }


                            UserModel newUser = new UserModel(uname, email, address, age, phone, gender, imgUrl);
                            newUser.setId(uid);

                            database.getReference().child("Users").child(uid).setValue(newUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Hi " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailet = new EditText(this);//write your registered email
        emailet.setHint("Email");
        emailet.setMinEms(16);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailet);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String emaill = emailet.getText().toString().trim();
                beginRecovery(emaill);//send a mail on the mail to recover password
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginRecovery(String emaill) {
        // send reset password email
        mAuth.sendPasswordResetEmail(emaill).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Done sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Error Occured", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Error Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginUser(String emaill, String pass) {

        progressBar.setVisibility(View.VISIBLE);
        // sign in with email and password after authenticating
        mAuth.signInWithEmailAndPassword(emaill, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user.isEmailVerified()) {
                        progressBar.setVisibility(View.GONE);
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        user.sendEmailVerification();
                        Toast.makeText(LoginActivity.this, "Check your email to verify your account", Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginAdmin(String emaill, String pass) {

        progressBar.setVisibility(View.VISIBLE);
        // sign in with email and password after authenticating
        mAuth.signInWithEmailAndPassword(emaill, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user.isEmailVerified()) {
                        progressBar.setVisibility(View.GONE);
//                        Toast.makeText(LoginActivity.this, "Registered User " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(LoginActivity.this, AdminActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        user.sendEmailVerification();
                        Toast.makeText(LoginActivity.this, "Check your email to verify your account", Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
