package com.eebbk.com.video;

import android.graphics.Bitmap;

/**
 * @author hxk <br/>
 *         功能： 2016/8/10
 *         创建日期
 *         修改者：
 *         修改日期：
 *         修改内容。
 */

public class Video {
    private int id;
    //文件名
    private String display_name;
    private String title;
    //文件路径
    private int size;
    private String data;
    private String mime_type;
    //视频缩略图
    private Bitmap bitmap;
    private String name;
    private String path;

    public void setId(int id) {
        this.id = id;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public String getTitle() {
        return title;
    }

    public String getData() {
        return data;
    }

    public String getMime_type() {
        return mime_type;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
