package com.tone.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClick1(View view){
        startActivity(new Intent(this,ScrollViewActivity.class));
    }

    public void onClick2(View view){
        startActivity(new Intent(this,ListViewActivity.class));
    }
    public void onClick3(View view){
        startActivity(new Intent(this,RecyclerViewActivity.class));
    }
}
