package com.eebbk.com.musicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.view.animation.AnimationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MusicService extends Service implements Runnable {
    public static Boolean playing = false;
    // 当前播放位置
    public static int _id = 1;
    public static Boolean isRun = true;
    public LrcProcess mLrcProcess;
    private MediaPlayer player;
    private List<Music> lists;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        lists = MusicList.getMusicData(getApplicationContext());
        SeekBarBroadcastReceiver receiver = new SeekBarBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.eebbk.com.musicplayer.seekBar");
        this.registerReceiver(receiver, filter);
        new Thread(this).start();
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String play = intent.getStringExtra("play");
        _id = intent.getIntExtra("id", 1);
        if (play.equals("play")) {
            if (null != player) {
                player.release();
                player = null;
            }
            playMusic(_id);
        } else if (play.equals("pause")) {
            if (null != player) {
                player.pause();
            }
        } else if (play.equals("playing")) {
            if (player != null) {
                player.start();
            } else {
                playMusic(_id);
            }
        } else if (play.equals("replaying")) {

        } else if (play.equals("first")) {
            int id = intent.getIntExtra("id", 0);
            playMusic(id);
        } else if (play.equals("rewind")) {
            int id = intent.getIntExtra("id", 0);
            playMusic(id);
        } else if (play.equals("forward")) {
            int id = intent.getIntExtra("id", 0);
            playMusic(id);
        } else if (play.equals("last")) {
            int id = intent.getIntExtra("id", 0);
            playMusic(id);
        }
        return 0;
    }

    private void playMusic(int id) {
        //  初始化歌词配置
        mLrcProcess = new LrcProcess();
        // 读取歌词文件
        mLrcProcess.readLRC(lists.get(_id).getUrl());
        // 传回处理后的歌词文件
        lrcList = mLrcProcess.getLrcContent();
        MusicActivity.lrc_view.setSentenceEntities(lrcList);
        // 切换带动画显示歌词
        MusicActivity.lrc_view.setAnimation(AnimationUtils.loadAnimation(MusicService.this, R.anim.alpha_z));
        // 启动线程
        mHandler.post(mRunnable);
        //初始化歌词配置
        if (null != player) {
            player.release();
            player = null;
        }
        if (id >= lists.size() - 1) {
            _id = lists.size() - 1;
        } else if (id <= 0) {
            _id = 0;
        }
        Music m = lists.get(_id);
        String url = m.getUrl();
        Uri myUri = Uri.parse(url);
        player = new MediaPlayer();
        player.reset();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(getApplicationContext(), myUri);
            player.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                // 下一首
                if (MusicActivity.isLoop == true) {
                    player.reset();
                    Intent intent = new Intent("com.eebbk.com.musicplayer.completion");
                    sendBroadcast(intent);
                    _id = _id + 1;
                    playMusic(_id);
                } else {
                    // 单曲播放
                    player.reset();
                    Intent intent = new Intent("com.eebbk.com.musicplayer.completion");
                    sendBroadcast(intent);
                    playMusic(_id);
                }
            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (null != player) {
                    player.release();
                    player = null;
                }
                Music m = lists.get(_id);
                String url = m.getUrl();
                Uri myUri = Uri.parse(url);
                player = new MediaPlayer();
                player.reset();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    player.setDataSource(getApplicationContext(), myUri);
                    player.prepare();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.start();
                return false;
            }
        });
    }

    private class SeekBarBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            int seekBarPosition = intent.getIntExtra("seekBarPosition", 0);
            player.seekTo(seekBarPosition * player.getDuration() / 100);
            player.start();
        }
    }

    public void run() {
        while (isRun) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (null != player) {
                int position = player.getCurrentPosition();
                int total = player.getDuration();
                Intent intent = new Intent("com.eebbk.com.musicplayer.progress");
                intent.putExtra("position", position);
                intent.putExtra("total", total);
                sendBroadcast(intent);
            }
            if (null != player) {
                if (player.isPlaying()) {
                    playing = true;
                } else {
                    playing = false;
                }
            }
        }
    }

    Handler mHandler = new Handler();
    // 歌词滚动线程
    Runnable mRunnable = new Runnable() {
        public void run() {
            MusicActivity.lrc_view.SetIndex(LrcIndex());
            MusicActivity.lrc_view.invalidate();
            mHandler.postDelayed(mRunnable, 100);
        }
    };

    // 创建对象
    private List<LrcProcess.LrcContent> lrcList = new ArrayList<LrcProcess.LrcContent>();
    // 初始化歌词检索值
    private int index = 0;
    // 初始化歌曲播放时间的变量
    private int CurrentTime = 0;
    // 初始化歌曲总时间的变量
    private int CountTime = 0;

    /**
     * 歌词同步处理
     */
    public int LrcIndex() {
        int i = 0;
        if (player.isPlaying()) {
            // 获得歌曲播放进度时间
            CurrentTime = player.getCurrentPosition();
            // 获得歌曲总时间长度
            CountTime = player.getDuration();
        }
        if (CurrentTime < CountTime) {
            for (; i < lrcList.size(); i++) {
                if (i < lrcList.size() - 1) {
                    if (CurrentTime < lrcList.get(i).getLrc_time() && i == 0) {
                        index = i;
                    }
                    if (CurrentTime > lrcList.get(i).getLrc_time()
                            && CurrentTime < lrcList.get(i + 1).getLrc_time()) {
                        index = i;
                    }
                }
                if (i == lrcList.size() - 1
                        && CurrentTime > lrcList.get(i).getLrc_time()) {
                    index = i;
                }
            }
        }
        return index;
    }
}

