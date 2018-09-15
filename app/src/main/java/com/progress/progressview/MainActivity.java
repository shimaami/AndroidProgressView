package com.progress.progressview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressView mProgressView1, mProgressView2;
    private Random r = new Random(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressView1 = findViewById(R.id.progressView1);
        mProgressView2 = findViewById(R.id.progressView2);
        findViewById(R.id.btn_progress).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_progress) {
            float progress = r.nextFloat();
            mProgressView1.setProgress(progress);
            mProgressView2.setProgress(progress);
        }
    }

}
