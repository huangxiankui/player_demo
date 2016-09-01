package com.eebbk.com.musicplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

/**
 * @author hxk <br/>
 *         音乐播放界面
 *         日期 2016/7/19
 */
public class MusicActivity extends Activity implements SensorEventListener {

    public static ImageButton imageBtnPlay;
    public static LrcView lrc_view;
    public static Boolean isLoop = true;
    private TextView textName;
    private TextView textSinger;
    private TextView textStartTime;
    private TextView textEndTime;
    private ImageButton imageBtnLast;
    private ImageButton imageBtnRewind;
    private ImageButton imageBtnForward;
    private ImageButton imageBtnNext;
    private ImageButton imageBtnLoop;
    private ImageButton imageBtnRandom;
    //音乐播放进度条
    private SeekBar seekBar;
    // 音量管理者
    private AudioManager audioManager;
    // 最大音量
    private int maxVolume;
    // 当前音量
    private int currentVolume;
    private SeekBar seekBarVolume;
    private List<Music> lists;
    private Boolean isPlaying = false;
    private static int id = 1;
    private static int currentId = 2;
    private static Boolean replaying = false;
    private MyProgressBroadCastReceiver receiver;
    private MyCompletionListener completionListener;
    private SensorManager sensorManager;
    private boolean mRegisteredSensor;
    //重力感应 摇歌
    private static final int SHAKE_THRESHOLD = 3000;
    private long lastUpdate = 0;
    private double last_x = 0;
    private double last_y = 4.50;
    private double last_z = 9.50;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.music);
        textName = (TextView) this.findViewById(R.id.music_name);
        textSinger = (TextView) this.findViewById(R.id.music_singer);
        textStartTime = (TextView) this.findViewById(R.id.music_start_time);
        textEndTime = (TextView) this.findViewById(R.id.music_end_time);
        seekBar = (SeekBar) this.findViewById(R.id.music_seekBar);
        imageBtnLast = (ImageButton) this.findViewById(R.id.music_lasted);
        imageBtnRewind = (ImageButton) this.findViewById(R.id.music_rewind);
        imageBtnPlay = (ImageButton) this.findViewById(R.id.music_play);
        imageBtnForward = (ImageButton) this.findViewById(R.id.music_foward);
        imageBtnNext = (ImageButton) this.findViewById(R.id.music_next);
        imageBtnLoop = (ImageButton) this.findViewById(R.id.music_loop);
        seekBarVolume = (SeekBar) this.findViewById(R.id.music_volume);
        imageBtnRandom = (ImageButton) this.findViewById(R.id.music_random);
        lrc_view = (LrcView) findViewById(R.id.LyricShow);
        imageBtnLast.setOnClickListener(new MyListener());
        imageBtnRewind.setOnClickListener(new MyListener());
        imageBtnPlay.setOnClickListener(new MyListener());
        imageBtnForward.setOnClickListener(new MyListener());
        imageBtnNext.setOnClickListener(new MyListener());
        imageBtnLoop.setOnClickListener(new MyListener());
        imageBtnRandom.setOnClickListener(new MyListener());
        //sensor传感器 摇歌
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lists = MusicList.getMusicData(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 获得最大音量
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 获得当前音量
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBarVolume.setMax(maxVolume);
        seekBarVolume.setProgress(currentVolume);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, AudioManager.FLAG_ALLOW_RINGER_MODES);
            }
        });
        //电话状态监听
        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(new MobliePhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE);
        //seekBar状态改变监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(seekBar.getProgress());
                Intent intent = new Intent("com.eebbk.com.musicplayer.seekBar");
                intent.putExtra("seekBarPosition", seekBar.getProgress());
                sendBroadcast(intent);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
        completionListener = new MyCompletionListener();
        IntentFilter filter = new IntentFilter("com.eebbk.com.musicplayer");
        this.registerReceiver(completionListener, filter);
    }

    /**
     * 状态监听类
     * 日期 2016/7/19
     */
    private class MobliePhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                // 无任何状态时
                case TelephonyManager.CALL_STATE_IDLE:
                    Intent intent = new Intent(MusicActivity.this,
                            MusicService.class);
                    intent.putExtra("play", "playing");
                    intent.putExtra("id", id);
                    startService(intent);
                    isPlaying = true;
                    imageBtnPlay.setImageResource(R.drawable.pause1);
                    replaying = true;
                    break;
                // 接起电话时
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // 电话进来时
                case TelephonyManager.CALL_STATE_RINGING:
                    Intent intent2 = new Intent(MusicActivity.this,
                            MusicService.class);
                    intent2.putExtra("play", "pause");
                    startService(intent2);
                    isPlaying = false;
                    imageBtnPlay.setImageResource(R.drawable.play1);
                    replaying = false;
                    break;
                default:
                    break;
            }
        }
    }

    protected void onStart() {
        super.onStart();
        receiver = new MyProgressBroadCastReceiver();
        IntentFilter filter = new IntentFilter("com.eebbk.com.musicplayer.progress");
        this.registerReceiver(receiver, filter);
        id = getIntent().getIntExtra("id", 1);
        if (id == currentId) {
            Music m = lists.get(id);
            textName.setText(m.getTitle());
            textSinger.setText(m.getSinger());
            textEndTime.setText(toTime((int) m.getTime()));
            Intent intent = new Intent(MusicActivity.this, MusicService.class);
            intent.putExtra("play", "replaying");
            intent.putExtra("id", id);

            startService(intent);
            if (replaying == true) {
                imageBtnPlay.setImageResource(R.drawable.pause1);
                isPlaying = true;
            } else {
                imageBtnPlay.setImageResource(R.drawable.play1);
                isPlaying = false;
            }
        } else {
            Music m = lists.get(id);
            textName.setText(m.getTitle());
            textSinger.setText(m.getSinger());
            textEndTime.setText(toTime((int) m.getTime()));
            imageBtnPlay.setImageResource(R.drawable.pause1);
            Intent intent = new Intent(MusicActivity.this, MusicService.class);
            intent.putExtra("play", "play");
            intent.putExtra("id", id);
            startService(intent);
            isPlaying = true;
            replaying = true;
            currentId = id;
        }
    }

    protected void onResume() {
        super.onResume();
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            Sensor sensor = sensors.get(0);
            mRegisteredSensor = sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    protected void onPause() {
        if (mRegisteredSensor) {
            sensorManager.unregisterListener(this);
            mRegisteredSensor = false;
        }
        super.onPause();
    }

    protected void onDestroy() {
        this.unregisterReceiver(receiver);
        this.unregisterReceiver(completionListener);
        super.onDestroy();
    }

    public class MyProgressBroadCastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position", 0);
            int total = intent.getIntExtra("total", 0);
            int progress = position * 100 / total;
            textStartTime.setText(toTime(position));
            seekBar.setProgress(progress);
            seekBar.invalidate();
        }
    }

    private class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == imageBtnLast) {
                // 第一首
                id = 0;
                Music m = lists.get(0);
                textName.setText(m.getTitle());
                textSinger.setText(m.getSinger());
                textEndTime.setText(toTime((int) m.getTime()));
                imageBtnPlay.setImageResource(R.drawable.pause1);
                Intent intent = new Intent(MusicActivity.this, MusicService.class);
                intent.putExtra("play", "first");
                intent.putExtra("id", id);
                startService(intent);
                isPlaying = true;
            } else if (v == imageBtnRewind) {
                // 前一首
                int id = MusicService._id - 1;
                if (id >= lists.size() - 1) {
                    id = lists.size() - 1;
                } else if (id <= 0) {
                    id = 0;
                }
                Music m = lists.get(id);
                textName.setText(m.getTitle());
                textSinger.setText(m.getSinger());
                textEndTime.setText(toTime((int) m.getTime()));
                imageBtnPlay.setImageResource(R.drawable.pause1);
                Intent intent = new Intent(MusicActivity.this, MusicService.class);
                intent.putExtra("play", "rewind");
                intent.putExtra("id", id);
                startService(intent);
                isPlaying = true;
            } else if (v == imageBtnPlay) {
                // 正在播放
                if (isPlaying == true) {
                    Intent intent = new Intent(MusicActivity.this, MusicService.class);
                    intent.putExtra("play", "pause");
                    startService(intent);
                    isPlaying = false;
                    imageBtnPlay.setImageResource(R.drawable.play1);
                    replaying = false;
                } else {
                    Intent intent = new Intent(MusicActivity.this, MusicService.class);
                    intent.putExtra("play", "playing");
                    intent.putExtra("id", id);
                    startService(intent);
                    isPlaying = true;
                    imageBtnPlay.setImageResource(R.drawable.pause1);
                    replaying = true;
                }
            } else if (v == imageBtnForward) {
                // 下一首
                int id = MusicService._id + 1;
                if (id >= lists.size() - 1) {
                    id = lists.size() - 1;
                } else if (id <= 0) {
                    id = 0;
                }
                Music m = lists.get(id);
                textName.setText(m.getTitle());
                textSinger.setText(m.getSinger());
                textEndTime.setText(toTime((int) m.getTime()));
                imageBtnPlay.setImageResource(R.drawable.pause1);
                Intent intent = new Intent(MusicActivity.this,
                        MusicService.class);
                intent.putExtra("play", "forward");
                intent.putExtra("id", id);
                startService(intent);
                isPlaying = true;
            } else if (v == imageBtnNext) {
                // 最后一首
                int id = lists.size() - 1;
                Music m = lists.get(id);
                textName.setText(m.getTitle());
                textSinger.setText(m.getSinger());
                textEndTime.setText(toTime((int) m.getTime()));
                imageBtnPlay.setImageResource(R.drawable.pause1);
                Intent intent = new Intent(MusicActivity.this,
                        MusicService.class);
                intent.putExtra("play", "last");
                intent.putExtra("id", id);
                startService(intent);
                isPlaying = true;
            } else if (v == imageBtnLoop) {
                if (isLoop == true) {
                    // 顺序播放
                    imageBtnLoop.setBackgroundResource(R.drawable.play_loop_spec);
                    isLoop = false;
                } else {
                    // 单曲播放
                    imageBtnLoop.setBackgroundResource(R.drawable.play_loop_sel);
                    isLoop = true;
                }
            } else if (v == imageBtnRandom) {
                imageBtnRandom.setImageResource(R.drawable.play_random_sel);
            }
        }
    }

    private class MyCompletionListener extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Music m = lists.get(MusicService._id);
            textName.setText(m.getTitle());
            textSinger.setText(m.getSinger());
            textEndTime.setText(toTime((int) m.getTime()));
            imageBtnPlay.setImageResource(R.drawable.pause1);
        }
    }

    /**
     * 时间格式转换
     *
     * @param time
     */
    public String toTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    /**
     * 处理精准度改变
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // 每200毫秒检测一次
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                double x = event.values[SensorManager.DATA_X];
                double y = event.values[SensorManager.DATA_Y];
                double z = event.values[SensorManager.DATA_Z];
                float speed = (float) (Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000);
                //检测到摇晃后执行的代码
                if (speed > SHAKE_THRESHOLD) {
                    if (MusicService.playing == true) {
                        Intent intent = new Intent(MusicActivity.this, MusicService.class);
                        intent.putExtra("play", "pause");
                        startService(intent);
                        isPlaying = false;
                        imageBtnPlay.setImageResource(R.drawable.play1);
                        replaying = false;
                    } else {
                        Intent intent = new Intent(MusicActivity.this, MusicService.class);
                        intent.putExtra("play", "playing");
                        intent.putExtra("id", id);
                        startService(intent);
                        isPlaying = true;
                        imageBtnPlay.setImageResource(R.drawable.pause1);
                        replaying = true;
                    }
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }
}
