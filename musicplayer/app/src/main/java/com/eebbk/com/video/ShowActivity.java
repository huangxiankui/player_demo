package com.eebbk.com.video;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eebbk.com.musicplayer.R;

import java.io.File;

import android.widget.SeekBar.OnSeekBarChangeListener;
import android.view.View.OnTouchListener;

/**
 * @author hxk <br/>
 *         功能：SurfacView视频播放界面
 *         创建日期   2016/8/10
 *         修改者：
 *         修改日期：
 *         修改内容。
 */
public class ShowActivity extends Activity implements View.OnClickListener {
    private static final int POPUPWINDOW_SHOW = 0x11;
    private static final int PROGRESS_CHANGED = 0x12;
    private ImageButton play;
    private String path;
    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private int position;
    // 播放进度条
    private SeekBar seekbar;
    //进度条控制栏
    private LinearLayout layout_prograss;
    // 显示时间
    private TextView showTime, allTime;
    private View seekbarview;
    private LinearLayout videoBack;
    private PopupWindow popupWindow;
    // 获取播放的位置
    private int currentposition;
    // 点击屏幕次数
    private int count;
    // 第一次点击
    private int firClick;
    // 第二次点击
    private int secClick;
    // 通过flag判断是否全屏
    private boolean popFlag;
    private boolean flag;
    // 按钮隐藏时间
    private int hint = 5000;
    // 是否正在播放
    private boolean Playing = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_show);
        layout_prograss = (LinearLayout) findViewById(R.id.layout_prograss);
        seekbarview = this.getLayoutInflater().inflate(R.layout.video_seekbar, null);
        // 得到video_seekbar布局文件上的ID
        videoBack = (LinearLayout) seekbarview.findViewById(R.id.videoback);
        seekbar = (SeekBar) seekbarview.findViewById(R.id.seekbar);
        showTime = (TextView) seekbarview.findViewById(R.id.showtime);
        allTime = (TextView) seekbarview.findViewById(R.id.alltime);
        play = (ImageButton) seekbarview.findViewById(R.id.video_bu_bofang);
        mediaPlayer = new MediaPlayer();
        surfaceView = (SurfaceView) findViewById(R.id.videoSurfaceView);
        //虽然该方法过时，但避免视频播放时，出现有声音没图像问题
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().setFixedSize(176, 144);
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.setClickable(true);
        play.setOnClickListener(this);
        surfaceView.setOnClickListener(new SurfaceViewOnClickListener());
        surfaceView.getHolder().addCallback(new SurfaceViewCallBack());
        popupWindow = new PopupWindow(seekbarview, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //接收视频文件地址
        Bundle bundle = getIntent().getExtras();
        path = bundle.getString("path");
        //进度条监听
        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress >= 0) {
                    if (fromUser) {
                        int videoLength = seekbar.getProgress();
                        mediaPlayer.seekTo(videoLength);
                    }
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        /**
         * 设置全屏播放
         */
        surfaceView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    count++;
                    if (count == 1) {
                        new countClear().start();
                        firClick = (int) System.currentTimeMillis();
                        if (!popFlag) {
                            popupWindow.showAtLocation(seekbarview, Gravity.BOTTOM, 0, 0);
                            popFlag = true;
                            handler.removeMessages(POPUPWINDOW_SHOW);
                            handler.sendEmptyMessageDelayed(POPUPWINDOW_SHOW, hint);
                        }
                    } else if (count == 2) {
                        secClick = (int) System.currentTimeMillis();
                        if (secClick - firClick < 1000) {
                            flag = !flag;
                            count = 0;
                        }
                        if (flag) {
                            surfaceView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                            surfaceView.getHolder().setFixedSize(480, 760);
                            if (!popFlag) {
                                popupWindow.showAtLocation(seekbarview, Gravity.BOTTOM, 0, 0);
                                popFlag = true;
                                handler.removeMessages(POPUPWINDOW_SHOW);
                                handler.sendEmptyMessageDelayed(POPUPWINDOW_SHOW, hint);
                            }
                        } else {
                            surfaceView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (LinearLayout.LayoutParams.MATCH_PARENT - 100)));
                            surfaceView.getHolder().setFixedSize(480, 760);
                            if (!popFlag) {
                                popupWindow.showAtLocation(seekbarview, Gravity.BOTTOM, 0, 0);
                                popFlag = true;
                                handler.removeMessages(POPUPWINDOW_SHOW);
                                handler.sendEmptyMessageDelayed(POPUPWINDOW_SHOW, hint);
                            }
                        }
                        count = 0;
                        firClick = 0;
                        secClick = 0;
                    }
                }
                return true;
            }
        });
    }

    public void onClick(View v) {
        if (v == play) {
            // 如果正在播放
            if (Playing) {
                play.setImageResource(R.drawable.pause);
                mediaPlayer.pause();
                Playing = false;
            } else {
                play.setImageResource(R.drawable.play);
                mediaPlayer.start();
                Playing = true;
            }
            handler.removeMessages(0x11);
            handler.sendEmptyMessageDelayed(0x11, hint);
        }

    }

    /**
     * 屏幕不可见后surfaceview被销毁
     */
    private final class SurfaceViewCallBack implements SurfaceHolder.Callback {
        public void surfaceCreated(SurfaceHolder holder) {
            // play(position);
        }

        //等surfaceView创建好后开始播放视频
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            File file = new File(path);
            if (file.exists()) {
                play(position);
            } else {
                Toast.makeText(getApplicationContext(), R.string.videonofind, Toast.LENGTH_SHORT).show();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    position = mediaPlayer.getCurrentPosition();
                    mediaPlayer.stop();
                }
            }
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == POPUPWINDOW_SHOW) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popFlag = false;
                }
            }
            if (msg.what == PROGRESS_CHANGED) {
                if (mediaPlayer != null) {
                    currentposition = mediaPlayer.getCurrentPosition();
                }
                seekbar.setProgress(currentposition);
                // 视频播放当前时间
                currentposition = currentposition / 1000;
                int minute = currentposition / 60;
                int hour = minute / 60;
                int second = currentposition % 60;
                minute = minute % 60;
                showTime.setText(String.format("%02d:%02d:%02d", hour, minute, second));
                //进度条更新
                sendEmptyMessage(PROGRESS_CHANGED);
            }
            if (msg.what == 0x14) {
                mediaPlayer.release();
            }
        }
    };

    /**
     * 点击屏幕，获取视频时间
     */
    private static class SurfaceViewOnClickListener implements View.OnClickListener {
        public void onClick(View v) {
        }
    }

    /**
     * 播放
     */
    private void play(int position) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new PrepareListener(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final class PrepareListener implements MediaPlayer.OnPreparedListener {
        private int position;

        public PrepareListener(int position) {
            this.position = position;
        }

        public void onPrepared(MediaPlayer mp) {
            //视频播放总时间
            int n = mediaPlayer.getDuration();
            seekbar.setMax(n);
            n = n / 1000;
            int m = n / 60;
            int h = m / 60;
            int s = n % 60;
            m %= 60;
            allTime.setText(String.format("%02d:%02d:%02d", h, m, s));
            mediaPlayer.start();
            if (position > 0) {
                mediaPlayer.seekTo(position);
            }
            handler.sendEmptyMessage(PROGRESS_CHANGED);
        }
    }

    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        popupWindow.dismiss();
        super.onDestroy();
    }

    protected void onPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                position = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            }
        }
        super.onPause();
    }

    protected void onResume() {
        if ((position > 0) && (path != null)) {
            play(position);
            position = 0;
        }
        super.onResume();
    }

    class countClear extends Thread {
        public void run() {
            try {
                sleep(1000);
                count = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
