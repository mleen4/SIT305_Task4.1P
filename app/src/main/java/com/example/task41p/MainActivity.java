package com.example.task41p;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Chronometer chronometer;
    boolean running;
    long pauseValue;
    String previousSession;
    String previousTask;

    private static final String PAUSE_VALUE = "pause value";
    private static final String IS_RUNNING = "is running";
    private static final String PREVIOUS_TASK = "previous task";
    private static final String PREVIOUS_SESSION = "previous session";


    SharedPreferences sharedPreferences;
    
    
    TextView lastSessionTextView;
    ImageButton playButton;
    EditText taskInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton = findViewById(R.id.startButton);
        lastSessionTextView = findViewById(R.id.lastSessionText);
        chronometer = findViewById(R.id.chronometer);

        //Taken from https://stackoverflow.com/questions/4152569/how-to-change-format-of-chronometer
        // User Md. Naushad Alam
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                cArg.setText(hh+":"+mm+":"+ss);
            }
        });
        taskInput = findViewById(R.id.inputTask);

        sharedPreferences = getSharedPreferences("com.example.task41p", MODE_PRIVATE);

        previousSession = sharedPreferences.getString(PREVIOUS_SESSION, "0");
        previousTask = sharedPreferences.getString(PREVIOUS_TASK, "...");
        lastSessionTextView.setText("Last Session working on " + previousTask + " was " + previousSession);
        if(savedInstanceState!=null)
        {
//            previousSession = savedInstanceState.getLong(PAUSE_VALUE);
            pauseValue = savedInstanceState.getLong(PAUSE_VALUE);
            running = savedInstanceState.getBoolean(IS_RUNNING);

            if(running)
            {
                StartTimer(playButton);
            }
        }
    }

    public void StartTimer(View v)
    {
        if(!running)
        {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseValue);
            chronometer.start();
            running = true;
        }
        else
        {

            chronometer.setBase(SystemClock.elapsedRealtime() - pauseValue);
            chronometer.start();
        }
    }

    public void PauseTimer(View v)
    {
        if(running)
        {
            chronometer.stop();
            pauseValue = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }
    public void StopTimer(View v)
    {

        pauseValue = SystemClock.elapsedRealtime() - chronometer.getBase();
        previousSession = chronometer.getText().toString();
        pauseValue = 0;
        previousTask = taskInput.getText().toString();
        Toast.makeText(getApplicationContext(), previousSession, Toast.LENGTH_LONG).show();
        SaveState();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        running = false;
    }


    public void SaveState()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREVIOUS_SESSION, previousSession);
        editor.putString(PREVIOUS_TASK, previousTask);
        editor.apply();
    }

    public void ShowTime(View view)
    {
        Toast.makeText(getApplicationContext(), "The time was - " + Long.toString(pauseValue), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if(running)
        {
            pauseValue = SystemClock.elapsedRealtime() - chronometer.getBase();
        }
        outState.putLong(PAUSE_VALUE, pauseValue);
        outState.putBoolean(IS_RUNNING, running);

        super.onSaveInstanceState(outState);
    }

}