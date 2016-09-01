package com.eebbk.com.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

/**
 * @author hxk <br/>
 *         listview显示界面
 *         创建日期2016/7/19
 *         修改者,修改日期,修改内容。
 */
public class ListsActivity extends Activity {
    private ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listmusic);
        listView = (ListView) this.findViewById(R.id.listAllMusic);
        listView.setVerticalScrollBarEnabled(false);
        listView.setFastScrollEnabled(false);
        List<Music> listMusic = MusicList.getMusicData(getApplicationContext());
        MusicAdapter adapter = new MusicAdapter(this, listMusic);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> args0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(ListsActivity.this, MusicActivity.class);
                intent.putExtra("id", arg2);
                startActivity(intent);
            }
        });
    }
}
