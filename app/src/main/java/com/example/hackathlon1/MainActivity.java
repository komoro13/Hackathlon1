package com.example.hackathlon1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    ListView listView;
    ArrayAdapter<String> adapter;

    ImageButton addTimerBtn;
    ArrayList <Timer> timers = new ArrayList<>();

    EditText hours;
    EditText minutes;
    EditText seconds;
    Boolean lost;

    ArrayList<Timer> wonTimers = new ArrayList<>();
    LinearLayout bombL;

    TextView pointsTxView;
    TextView levelTxView;
    ImageView levelImage;
    EditText name;
    Button button;
    String[] tasks = {"wash my clothes", "go to gym", "study python"};

    int user_points = 50;

    SharedPreferences sharedPreferences;
    MediaPlayer mediaPlayer;
    SharedPreferences.Editor editor;
    ArrayList<String> taskL = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getPreferences(MODE_PRIVATE);
       editor = sharedPreferences.edit();
        user_points = Integer.parseInt(sharedPreferences.getString("points", "50"));

        pointsTxView = findViewById(R.id.user_points);
        levelTxView = findViewById(R.id.userLevel);
        addTimerBtn = findViewById(R.id.timerBtn);
        listView = findViewById(R.id.timersList);
        levelImage = findViewById(R.id.levelPhoto);
        pointsTxView.setText("Points: " + String.valueOf(user_points));
        setRightLevelPhoto();
        adapter = new ArrayAdapter<String>(this, R.layout.timerlayout, R.id.timerTxView,taskL);
        listView.setAdapter(adapter);
        bombL = findViewById(R.id.bombLayout);
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.defusing);
        lost = false;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(MainActivity.this, ((TextView) ((ViewGroup)   ((ViewGroup)  view  ).getChildAt(1))  .getChildAt(1)) .getText().toString(), Toast.LENGTH_SHORT).show();
                if (lost)
                    return;
                if (timers.get(getTimerIndexFromName(((TextView)((ViewGroup)view).getChildAt(0)).getText().toString())).started)
                    timers.get(getTimerIndexFromName(((TextView)((ViewGroup)view).getChildAt(0)).getText().toString())).stop();
                else
                    timers.get(getTimerIndexFromName(((TextView)((ViewGroup)view).getChildAt(0)).getText().toString())).start();


            }
        });

        final View custom_dialog = getLayoutInflater().inflate(R.layout.custom_dialog, null);

        hours = custom_dialog.findViewById(R.id.hours);
        minutes = custom_dialog.findViewById(R.id.minutes);
        seconds = custom_dialog.findViewById(R.id.seconds);
        name = custom_dialog.findViewById(R.id.name);


        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("New timer");
        dialog.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (hours.getText().toString().equals(""))
                    hours.setText("00");
                if(minutes.getText().toString().equals(""))
                    minutes.setText("00");
                if (seconds.getText().toString().equals(""))
                    seconds.setText("00");
                if (name.getText().toString().isEmpty())
                    name.setText("task");
                if (nameExists(name.getText().toString()) ==0)
                {
                    Toast.makeText(MainActivity.this, "The task name already exists!", Toast.LENGTH_SHORT).show();
                    return;
                }
                timers.add(new Timer(Integer.parseInt(hours.getText().toString()), Integer.parseInt(minutes.getText().toString()), Integer.parseInt(seconds.getText().toString()), name.getText().toString()));
                taskL.add(name.getText().toString());
                updateTimers();
                timerRunnable.run();
                hours.setText("");
                minutes.setText("");
                seconds.setText("");
                name.setText("");
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hours.setText("");
                minutes.setText("");
                seconds.setText("");
                name.setText("");
            }
        }).create();

        addTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lost)
                    return;
                if (custom_dialog.getParent()!=null)
                {
                    ((ViewGroup)custom_dialog.getParent()).removeView(custom_dialog);
                }
                dialog.setView(custom_dialog);
                dialog.show();
            }
        });
    }

    private void updateTimers()
    {
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);


    }
    //In your Activity.java
    private Handler timerHandler = new Handler();
    private boolean shouldRun = true;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (shouldRun) {
                /* Put your code here */
                //run again after 200 milliseconds (1/5 sec)
                updateTime();
                timerHandler.postDelayed(this, 200);
            }
        }
    };

    //In this example, the timer is started when the activity is loaded, but this need not to be the case
    @Override
    public void onResume() {
        super.onResume();
        /* ... */
        timerHandler.postDelayed(timerRunnable, 0);
    }

    //Stop task when the user quits the activity
    @Override
    public void onPause() {
        super.onPause();
        /* ... */
        shouldRun = false;
        timerHandler.removeCallbacksAndMessages(timerRunnable);
    }
    private void updateTime()
    {
        if (user_points<0)
            user_points =0;

        for (Timer timer:timers) {
            for (int j = 0; j < listView.getChildCount(); j++)
            {
                if (((TextView)((ViewGroup)listView.getChildAt(j)).getChildAt(0)).getText().toString().equals(timer.name))
                {
                    ((TextView) ((ViewGroup)   ((ViewGroup) listView.getChildAt(j) ).getChildAt(1))  .getChildAt(1)).setText(timer.timeString);
                    if (timer.finished&&lost==false)
                    {
                        user_points = user_points-10;
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.boom);
                            mediaPlayer.start();

                        }

                        lost = true;
                        bombL.setVisibility(View.VISIBLE);
                    }
                    else if(timer.stopped && wonTimer(timer)!=0)
                    {
                        wonTimers.add(timer);
                        (((pl.droidsonroids.gif.GifImageView)((ViewGroup)((ViewGroup) listView.getChildAt(j) ).getChildAt(1)) .getChildAt(0))).setVisibility(View.INVISIBLE);
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.defusing);
                            mediaPlayer.start();
                            user_points = user_points + 10;
                        }

                    }
                    editor.putString("points",String.valueOf(user_points));
                    pointsTxView.setText("Points: " + user_points);
                    setRightLevelPhoto();


                }
            }
        }
    }


    public int wonTimer(Timer timer)
    {
        for (Timer t:wonTimers)
        {
            if (t.name.equals(timer.name))
                return 0;
        }
        return -1;
    }

    public int nameExists(String name)
    {
        for (Timer t:timers)
        {
            if (t.name.equals(name))
                return 0;
        }
        return -1;
    }

    private void setRightLevelPhoto()
    {
        if (user_points<=20)
        {
            levelTxView.setText("Level 1: Goldfish");
            levelImage.setImageResource(R.drawable.goldfish);
        }
        else if(user_points <=40)
        {
            levelTxView.setText("Level 2: Ostrich");
            levelImage.setImageResource(R.drawable.ostrich);
        }
        else if (user_points <= 60)
        {
            levelTxView.setText("Level 3: Fox");
            levelImage.setImageResource(R.drawable.fox);
        }
        else if (user_points <= 80)
        {
            levelTxView.setText("Level 4: Elephant");
            levelImage.setImageResource(R.drawable.elephant);
        }

        else if(user_points<=100)
        {
            levelTxView.setText("Level 5: Dolphin");
            levelImage.setImageResource(R.drawable.dolphin);
        }

    }




    public int getTimerIndexFromName(String name)
    {
        for (int j = 0; j < timers.size(); j++)
        {
            if (timers.get(j).name.equals(name))
                return j;
        }
        return -1;
    }

    private void resetGame()
    {

    }

}