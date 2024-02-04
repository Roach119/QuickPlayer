package com.example.quickplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView currentTimetv,titleTv,totalTimeTv;
    SeekBar seekBar;
    ImageView pauseplay, next,prev,bigicon;
    ArrayList<AudioModel> songlist;
    AudioModel currentsong;
    MediaPlayer mediaPlayer= MyMediaPlayer.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleTv=findViewById(R.id.song_title);
        currentTimetv=findViewById(R.id.currenttime);
        totalTimeTv=findViewById(R.id.totaltime);
        seekBar=findViewById(R.id.seek_bar);
        pauseplay=findViewById(R.id.pause_play);
        next=findViewById(R.id.next);
        prev=findViewById(R.id.previous);
        bigicon=findViewById(R.id.bigicon);

        titleTv.setSelected(true);
        songlist=(ArrayList<AudioModel>) getIntent().getSerializableExtra("List");

        setResourcesWithMusic();


        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null)
                {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimetv.setText(convertTMMSS(mediaPlayer.getCurrentPosition()+""));
                }
                if(mediaPlayer.isPlaying())
                {
                    pauseplay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                }else {
                    pauseplay.setImageResource(R.drawable.baseline_play_circle_outline_24);
                }


                new Handler().postDelayed(this,100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null&& fromUser)
                {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void setResourcesWithMusic(){
        currentsong=songlist.get(MyMediaPlayer.currentIndex);

        titleTv.setText(currentsong.getTitle());
        totalTimeTv.setText(convertTMMSS(currentsong.getDuration()));

        pauseplay.setOnClickListener(v->pausePlay());
        next.setOnClickListener(v->playNext());
        prev.setOnClickListener(v->playPrev());

        playMusic();

    }

    private void playMusic(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentsong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void playNext(){
        if(MyMediaPlayer.currentIndex==songlist.size()-1)
        {
            return;
        }
        MyMediaPlayer.currentIndex +=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void playPrev(){
        if(MyMediaPlayer.currentIndex==0)
        {
            return;
        }
        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pausePlay(){
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }





    public static String convertTMMSS(String duration)
    {
        Long millis=Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis)% TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis)% TimeUnit.MINUTES.toSeconds(1));
    }

}