package com.teacher.owldevelop.authenticationexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.v4.graphics.TypefaceCompatUtil.getTempFile;

public class MainActivity extends AppCompatActivity {

    EditText loginEdt, passwordEdt;
    Button loginBtn, registerBtn, googleBtn, facebookBtn;
    LoginManager facebookManager;
    private FirebaseAuth mAuth;
    CallbackManager callbackManager;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    CircleImageView avatar;
    int RC_SIGN_IN = 2046;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        mAuth = FirebaseAuth.getInstance();
        facebookManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        facebookManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Authentication canceled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });

        placeViews();

        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> permissions = new ArrayList<String>();
                permissions.add("email");
                facebookManager.logInWithReadPermissions(MainActivity.this,permissions);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin(loginEdt.getText().toString(), passwordEdt.getText().toString());
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(loginEdt.getText().toString(), passwordEdt.getText().toString());
            }
        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
              //  SetGoogleAvatar(account);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("googleFirebase", "Google sign in failed", e);
            }
        }
        else{
            if(requestCode == PICK_IMAGE_REQUEST){
             avatar.setImageBitmap(ImagePicker.getImageFromResult(MainActivity.this, resultCode, data));
            }
            else
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    }

    private void SetAvatar(FirebaseUser account) {
        if (account != null){
            String imgurl = account.getPhotoUrl().toString();
            Glide.with(this).load(imgurl).into(avatar);
            //    avatar.setImageBitmap(avatarBitmap);
            avatar.invalidate();
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("googleFirebase", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("googleFirebase", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("googleFirebase", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this,"Authentication Failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    FirebaseUser user;
    private void handleFacebookToken(final AccessToken accessToken) {
        mAuth.signInWithCredential(FacebookAuthProvider.getCredential(accessToken.getToken())).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Firebase", "createUserWithFacebook:success");
                    user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse("https://graph.facebook.com/" + accessToken.getUserId()+ "/picture?type=large"))
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("firebase", "User profile updated.");
                                        updateUI(user);
                                    }
                                }
                            });
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Firebase", "createUserWithFacebook:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Firebase", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Firebase", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signin(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Firebase", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Firebase", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        SetAvatar(currentUser);
    }

    private void placeViews(){
        ConstraintLayout layout;
        layout = new ConstraintLayout(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float scale = 1080.0f/dm.widthPixels;
        loginEdt = new EditText(this);
        loginEdt.setX(20*scale);
        loginEdt.setY(200*scale);
        loginEdt.setHint("login");

        passwordEdt = new EditText(this);
        passwordEdt.setX(20*scale);
        passwordEdt.setY(300*scale);
        passwordEdt.setHint("password");

        loginBtn = new Button(this);
        loginBtn.setText("SIGN IN");
        loginBtn.setX(10*scale);
        loginBtn.setY(500*scale);
        loginBtn.setWidth((int)(520*scale));

        registerBtn = new Button(this);
        registerBtn.setText("SIGN UP");
        registerBtn.setX(550*scale);
        registerBtn.setY(500*scale);
        registerBtn.setWidth((int)(520*scale));

        googleBtn = new Button(this);
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled},
                },
                new int[] {
                        Color.rgb(180,0,0),
                }
        );
        googleBtn.setTextColor(Color.WHITE);
        googleBtn.setBackgroundTintList(myColorStateList);
        googleBtn.setText("LOGIN WITH GOOGLE");
        googleBtn.setX(10*scale);
        googleBtn.setY(800*scale);
        googleBtn.setWidth((int)(1060*scale));

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        facebookBtn = new Button(this);
        myColorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled},
                },
                new int[] {
                        Color.rgb(0,0,180),
                }
        );
        facebookBtn.setTextColor(Color.WHITE);
        facebookBtn.setBackgroundTintList(myColorStateList);
        facebookBtn.setText("LOGIN WITH FACEBOOK");
        facebookBtn.setX(10*scale);
        facebookBtn.setY(950*scale);
        facebookBtn.setWidth((int)(1060*scale));

        avatar = new CircleImageView(this);
//        avatar.setScaleType(ImageView.ScaleType.FIT_XY);
        avatar.setLayoutParams(new FrameLayout.LayoutParams((int)(140*scale), (int)(140*scale)));
        avatar.setX(dm.widthPixels/2 - 70*scale);
        avatar.setY(0);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(ImagePicker.getPickImageIntent(MainActivity.this), PICK_IMAGE_REQUEST);
            }
        });

        layout.addView(loginEdt);
        layout.addView(passwordEdt);
        layout.addView(registerBtn);
        layout.addView(loginBtn);
        layout.addView(googleBtn);
        layout.addView(facebookBtn);
        layout.addView(avatar);
        setContentView(layout);
    }


}
