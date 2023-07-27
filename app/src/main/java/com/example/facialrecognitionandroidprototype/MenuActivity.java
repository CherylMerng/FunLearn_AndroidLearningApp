package com.example.facialrecognitionandroidprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);
        if (button1 != null){
            button1.setOnClickListener(this);}
        if (button2 != null){
            button2.setOnClickListener(this);}
        if (button3 != null){
            button3.setOnClickListener(this);}
        if (button4 != null){
            button4.setOnClickListener(this);}
        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.logo);
        ImageView learn = findViewById(R.id.emotions);
        learn.setImageResource(R.drawable.emotions);
        learn.setOnClickListener(this);
        ImageView quiz = findViewById(R.id.quiz);
        quiz.setImageResource(R.drawable.quizz);
        quiz.setOnClickListener(this);
        ImageView video = findViewById(R.id.video);
        video.setImageResource(R.drawable.video);
        video.setOnClickListener(this);
    }

    //override onBackPressedButton
    @Override
    public void onBackPressed () {

    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        if (id == R.id.button1 || id == R.id.emotions){
            Intent intent = new Intent(this, LearnFacialExpressionActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.button2 || id == R.id.quiz){
            Intent intent = new Intent(this, FacialExpressionQuizActivity.class);
            intent.putExtra("url", getResources().getString(R.string.HOST_ADDRESS) + "child/quiz" );
            startActivity(intent);

        }else if (id == R.id.button3 || id == R.id.video){
            Intent intent = new Intent(this, WatchVideoActivity.class);
            intent.putExtra("url", getResources().getString(R.string.HOST_ADDRESS) + "child/watchvideo" );
            startActivity(intent);
        } else{
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }
}