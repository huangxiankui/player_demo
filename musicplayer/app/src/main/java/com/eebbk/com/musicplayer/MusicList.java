package com.eebbk.com.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 音频文件的获取.
 */
public class MusicList {
    public static List<Music> getMusicData(Context context) {
        List<Music> musicList = new ArrayList<Music>();
        ContentResolver cr = context.getContentResolver();
        if (cr != null) {
            //获取媒体库中的音乐
            Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            //没有音乐，先不做处理
            if (null == cursor) {
                return null;
            } else {
                if (cursor.moveToFirst()) {
                    do {
                        Music m = new Music();
                        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        String sbr = name.substring(name.length() - 3, name.length());
                        if (sbr.equals("mp3")) {
                            m.setTitle(title);
                            m.setSize(size);
                            m.setTime(time);
                            m.setUrl(url);
                            m.setName(name);
                            musicList.add(m);
                        } else if (sbr.equals("wma")) {
                            m.setTitle(title);
                            m.setSize(size);
                            m.setTime(time);
                            m.setUrl(url);
                            m.setName(name);
                            musicList.add(m);
                        } else if (sbr.equals("wav")) {
                            m.setTitle(title);
                            m.setSize(size);
                            m.setTime(time);
                            m.setUrl(url);
                            m.setName(name);
                            musicList.add(m);
                        }

                    } while (cursor.moveToNext());
                }
            }
        }
        return musicList;
    }

}
