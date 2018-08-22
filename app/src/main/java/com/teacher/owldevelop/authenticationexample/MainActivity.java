package com.teacher.owldevelop.authenticationexample;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeViews();

    }

    public void placeViews(){
        ConstraintLayout layout;
        layout = new ConstraintLayout(this);

        setContentView(layout);
    }
}
