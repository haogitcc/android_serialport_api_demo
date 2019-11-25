package com.fuwit.sensordemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    Button readerButton;
    ImageView logoImageview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findAllViewById();

        readerButton.setOnClickListener(this);
    }

    private void findAllViewById() {
        readerButton = findViewById(R.id.reader_button);
        logoImageview = findViewById(R.id.logo_imageview);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.reader_button:
                start(ReaderActivity.class);
                break;
        }
    }

    private void start(Class<ReaderActivity> token) {
        Intent intent = new Intent(this, token);
        startActivity(intent);
    }
}
