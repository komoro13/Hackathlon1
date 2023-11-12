package com.example.hackathlon1;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.widget.LinearLayout;

import java.time.Duration;

public class Timer {
    public String name;
    int hours;
    int minutes;
    int seconds;
    long millis;

    String timeString;

    boolean finished;
    public boolean started = false;

    boolean stopped = false;


    CountDownTimer countDownTimer;

    public Timer(int hh, int mm, int ss,String name ) {
        this.hours = hh;
        this.minutes = mm;
        this.seconds = ss;
        this.millis = convertToMillis(this.hours, this.minutes, this.seconds);
        this.name = name;
        this.timeString = String.format("%02d:%02d:%02d", hh,mm,ss);

        countDownTimer = new CountDownTimer(millis, 1000 ) {
            @Override
            public void onTick(long l) {
                millis = l;
                updateTimeString();
            }

            @Override
            public void onFinish() {
                finished = true;
            }
        };
    }

    private long convertToMillis(int h, int m, int s)
    {
        long mills;
        mills = h*3600000;
        mills = mills + m*60000;
        mills = mills + s*1000;
        return mills;
    }


    public void stop()
    {
        countDownTimer.cancel();
        stopped = true;
    }

    public void start()
    {

        countDownTimer.start();
        started = true;
    }

    private String updateTimeString()
    {

        Duration duration = null;
        String time = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            duration = Duration.ofMillis(millis);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            long seconds = duration.getSeconds();

            long hh = seconds / 3600;
            long mm = (seconds %3600)/60;
            long ss = seconds% 60;

            time = String.format("%02d:%02d:%02d",hh,mm,ss);
        }
        this.timeString = time;
        return time;
    }


}
