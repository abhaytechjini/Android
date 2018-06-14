package com.londonappbrewery.climapm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

public class ChangeCityController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.change_city_layout);
        final EditText editText = findViewById(R.id.queryET);
        ImageButton backButton = findViewById(R.id.backButton);
    }
}