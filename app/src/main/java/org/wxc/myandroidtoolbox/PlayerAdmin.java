package org.wxc.myandroidtoolbox;

import android.media.MediaPlayer;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by wxc on 2016/11/30.
 */

public class PlayerAdmin {
    private static final String TAG = "PlayerAdmin";

    private static PlayerAdmin sInstance;
    private MediaPlayer s1Player;
    private MediaPlayer s2Player;
    private boolean finishing;

    private boolean readyToPauseS1;
    private boolean readyToPauseS2;
    private boolean s1Waiting;
    private boolean s2Waiting;
    private boolean isInCalling;

    private PlayerAdmin() {

        s1Player = MediaPlayer.create(APP.getContext(), R.raw.high_prio);
        s2Player = MediaPlayer.create(APP.getContext(), R.raw.mid_prio);
        s1Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                new Thread("s1Player"){
                    @Override
                    public void run() {
                        if (readyToPauseS1) {
                            readyToPauseS1 = false;
                            return;
                        }
                        s1Waiting = true;
                        for (int i = 0; i < 3; i++) {
                            try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (readyToPauseS1) {
                                s1Waiting = false;
                                readyToPauseS1 = false;
                                return;
                            }
                        }

                        if (isFinishing() || !s1Waiting) {
                            return;
                        }
                        s1Waiting = false;
                        s1Player.start();
                    }
                }.start();
            }
        });
        s2Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                new Thread("s2Player"){
                    @Override
                    public void run() {
                        if(readyToPauseS2) {
                            readyToPauseS2 = false;
                            return;
                        }
                        s2Waiting = true;
                        for(int i=0;i<6;i++) {
                            try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(readyToPauseS2) {
                                s2Waiting = false;
                                readyToPauseS2 = false;
                                return;
                            }
                        }

                        if(isFinishing() || !s2Waiting) {
                            return;
                        }
                        s2Waiting = false;

                        s2Player.start();
                    }
                }.start();
            }
        });
    }

    public static PlayerAdmin getInstance() {
        if(sInstance == null) {
            synchronized (PlayerAdmin.class) {
                if(sInstance == null) {
                    sInstance = new PlayerAdmin();
                }
            }
        }
        return sInstance;
    }


    public void repeatS1() {
        if(isInCalling) {
            return;
        }
        if(s1Waiting || s1Player.isPlaying()) {
            return;
        }

        if(s2Waiting || s2Player.isPlaying()) {
            s2Waiting = false;
            if(s2Player.isPlaying()) {
                s2Player.seekTo(0);
                s2Player.pause();
            }
        }
        s1Player.start();
    }

    public void repeatS2() {
        if(isInCalling) {
            return;
        }
        if(s2Waiting || s2Player.isPlaying()) {
            return;
        }

        if(s1Waiting || s1Player.isPlaying()) {
            s1Waiting = false;
            if(s1Player.isPlaying()) {
                s1Player.seekTo(0);
                s1Player.pause();
            }
        }
        s2Player.start();
    }

    public void stopAlarm() {
        if(isInCalling) {
            return;
        }
        preparePause();
    }

    private void preparePause() {
        if(s1Waiting || s1Player.isPlaying()) {
            readyToPauseS1 = true;
        }
        if (s2Waiting || s2Player.isPlaying()) {
            readyToPauseS2 = true;
        }
    }

    public void inCall() {
        isInCalling = true;
        offCall();
        repeatS1();
    }

    public void offCall() {
        if(!isInCalling) {
            Log.e(TAG, "offCall: 没有来电响铃");
            return;
        }
        totalPause();
        isInCalling = false;
    }

    private void totalPause() {
        preparePause();
        s1Waiting = false;
        if(s1Player.isPlaying()) {
            s1Player.seekTo(0);
            s1Player.pause();
        }
        s2Waiting = false;
        if(s2Player.isPlaying()) {
            s2Player.seekTo(0);
            s2Player.pause();
        }
    }


    public boolean isFinishing() {
        readyToPauseS1 = true;
        readyToPauseS2 = true;
        return finishing;
    }

    public void setFinishing() {
        finishing = true;
    }
}
