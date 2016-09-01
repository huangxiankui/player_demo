package com.eebbk.com.videoadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.eebbk.com.musicplayer.R;
import com.eebbk.com.video.Video;

import java.util.List;

/**
 * @author hxk <br/>
 *         功能： 2016/8/9
 *         创建日期
 *         修改者：
 *         修改日期：
 *         修改内容。
 */

public class VideoAdapter extends BaseAdapter {
    //加载的视频信息列表
    private List<Video> videos;
    private Context context;
    private int item;
    private LayoutInflater layoutInflater;
    public VideoAdapter(Context context, List<Video> videos, int item) {
        this.context = context;
        this.videos = videos;
        this.item = item;
        //获取系统布局服务
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }
    public int getCount() {
        return videos.size();
    }
    public Object getItem(int position) {
        return videos.get(position);
    }
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        TextView textView;
        Video video;
        if(convertView == null)
        {
            convertView = layoutInflater.inflate(item,null);
            imageView = (ImageView)convertView.findViewById(R.id.itemImage);
            textView = (TextView)convertView.findViewById(R.id.itemContent);
            convertView.setTag(new DataWrapper(imageView,textView));
        }
        else
        {
            DataWrapper dataWrapper = (DataWrapper) convertView.getTag();
            imageView = dataWrapper.imageView;
            textView = dataWrapper.textView;
            video = videos.get(position);
            imageView.setImageBitmap(video.getBitmap());
            textView.setText(video.getTitle());
        }
        return convertView;
    }
    /**
     * 数据保存类
     */
    public final class DataWrapper{
        ImageView imageView;
        TextView textView;
        public DataWrapper(ImageView imageView,TextView textView)
        {
            this.imageView = imageView;
            this.textView = textView;
        }
    }
}
