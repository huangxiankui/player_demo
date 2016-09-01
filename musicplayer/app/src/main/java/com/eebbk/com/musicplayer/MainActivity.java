package com.eebbk.com.musicplayer;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;

import com.eebbk.com.video.VideoActivity;

/**
 * 主界面
 */
public class MainActivity extends TabActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        //音乐
        intent = new Intent().setClass(this, ListsActivity.class);
        spec = tabHost.newTabSpec(getString(R.string.music)).setIndicator(getString(R.string.music), res.getDrawable(R.drawable.item)).setContent(intent);
        tabHost.addTab(spec);
        //视频
        intent = new Intent().setClass(this, VideoActivity.class);
        spec = tabHost.newTabSpec(getString(R.string.video)).setIndicator(getString(R.string.video), res.getDrawable(R.drawable.album)).setContent(intent);
        tabHost.addTab(spec);
        tabHost.setCurrentTab(0);
    }
}
