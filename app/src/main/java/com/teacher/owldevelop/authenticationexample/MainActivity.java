package com.teacher.owldevelop.authenticationexample;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText loginEdt, passwordEdt;
    Button loginBtn, registerBtn, facebookBtn, googleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeViews();

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

        layout.addView(loginEdt);
        layout.addView(passwordEdt);
        layout.addView(registerBtn);
        layout.addView(loginBtn);
        layout.addView(googleBtn);
        layout.addView(facebookBtn);
        setContentView(layout);
    }
}
