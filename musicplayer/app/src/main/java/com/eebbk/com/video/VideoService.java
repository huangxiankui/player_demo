package com.eebbk.com.video;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hxk <br/>
 *         功能： 2016/8/10
 *         创建日期
 *         修改者：
 *         修改日期：
 *         修改内容。
 */

public class VideoService {
    private static List<Video> videos;
    private static Context context;
    private static Cursor cursor;

    public VideoService(Context context) {
        this.context = context;
    }

    /**
     * 获取视频信息
     */
    public static List<Video> getVideoList(int pageNow, int pageSize) {
        videos = new ArrayList<Video>();
        int offset = (pageNow - 1) * pageSize;
        GetFilesBySystem(pageSize, offset);
        return videos;
    }

    /**
     * 通过系统数据库搜索多媒体信息
     *
     * @param select 搜索多少条
     * @param offset 跳过多少条
     */
    private static void GetFilesBySystem(int select, int offset) {
        cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,//查询路径
                new String[]{MediaStore.Video.Media._ID, //查询内容
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.MIME_TYPE}, "_size>?", new String[]{1024 * 1024 + ""}, MediaStore.Video.Media.DEFAULT_SORT_ORDER + " limit " + select + " Offset " + offset);
        while (cursor.moveToNext()) {
            Video video = new Video();
            video.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)));
            video.setDisplay_name(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
            video.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
            video.setMime_type(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));
            video.setSize(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
            video.setData(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
            //获取视频的缩略图
            Bitmap bitmap = android.provider.MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)),
                    android.provider.MediaStore.Video.Thumbnails.MINI_KIND, null);
            video.setBitmap(bitmap);
            videos.add(video);
        }
    }

    /**
     * 返回所有记录数
     */
    public static int getCount() {
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        return cursor.getCount();
    }
}
