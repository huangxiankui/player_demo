package com.eebbk.com.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.eebbk.com.musicplayer.R;
import com.eebbk.com.videoadapter.VideoAdapter;

import java.util.List;

/**
 * @author hxk <br/>
 *         功能： 视频listview显示界面
 *         创建日期 2016/8/10
 *         修改者：
 *         修改日期：
 *         修改内容。
 */
public class VideoActivity extends Activity {
    //主体显示界面条目
    private static ListView listView;
    //数据list
    private static List<Video> videos;
    private static VideoService videoService;
    //ListView加载下一页时的页脚提示
    private static View foot;
    private static VideoAdapter adapter;
    //当前页
    private static int pageNow = 1;
    //每页显示数目
    private static int pageSize = 4;
    //判断是否全部加载完成
    private static boolean loadFinish = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);
        listView = (ListView) findViewById(R.id.videoListView);
        listView.setOnItemClickListener(new videoOnItemClickListener());
        //设置滚动监听事件
        listView.setOnScrollListener(new onScrollListener());
        //下拉刷新
        foot = getLayoutInflater().inflate(R.layout.activity_foot, null);
        Context context = getApplicationContext();
        videoService = new VideoService(context);
        show();
    }

    /**
     * ListView滚动监听事件
     */
    private final class onScrollListener implements AbsListView.OnScrollListener {
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int lastItemId = listView.getLastVisiblePosition();
            if ((lastItemId + 1) == totalItemCount) {
                if (totalItemCount > 0) {
                    pageNow++;
                    if (pageNow <= getPage() && loadFinish) {
                        listView.addFooterView(foot);
                        loadFinish = false;
                        new Thread(new Runnable() {
                            public void run() {
                                List<Video> listVideos = VideoService.getVideoList(pageNow, pageSize);
                                handler.sendMessage(handler.obtainMessage(11, listVideos));
                            }
                        }).start();
                    }
                }
            }
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                videos.addAll((List<Video>) msg.obj);
                //适配器通知ListView数据已经发生改变
                adapter.notifyDataSetChanged();
                loadFinish = true;

            }
        };

        public void onScrollStateChanged(AbsListView arg0, int arg1) {
            //先不做处理
        }
    }

    public int getPage() {
        int pageCount = VideoService.getCount();
        int num = pageCount % pageSize == 0 ? pageCount / pageSize : pageCount / pageSize + 1;
        return num;
    }

    /**
     * 加载listView的信息
     * 采用自定义适配器
     */
    private void show() {
        videos = VideoService.getVideoList(pageNow, pageSize);
        adapter = new VideoAdapter(getApplicationContext(), videos, R.layout.activity_item);
        //在setAdapter之前设置footer
        listView.addFooterView(foot);
        listView.setAdapter(adapter);
        //第一次不显示
        listView.removeFooterView(foot);
    }

    /**
     * listview点击、传地址
     */
    private class videoOnItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lview = (ListView) parent;
            Video video = (Video) lview.getItemAtPosition(position);
            String Path = video.getData();
            Bundle bundle = new Bundle();
            bundle.putString("path", Path);
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), ShowActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    protected void onResume() {
        super.onResume();
    }
}
