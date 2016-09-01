package com.eebbk.com.musicplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hxk <br/>
 *         歌词
 *         创建日期2016/7/19
 *         修改者,修改日期,修改内容
 */
public class LrcProcess {
    private List<LrcContent> LrcList;
    private LrcContent mLrcContent;

    public LrcProcess() {
        mLrcContent = new LrcContent();
        LrcList = new ArrayList<LrcContent>();
    }

    /**
     * 读取歌词文件内容
     *
     * @param song_path 歌曲文件路径
     * @return 诗词文字内容
     * @throws FileNotFoundException 找不到文件
     * @throws IOException           找不到文件
     */
    public String readLRC(String song_path) {
        StringBuilder stringBuilder = new StringBuilder();
        File f = new File(song_path.replace(".mp3", ".lrc"));
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(f);
            isr = new InputStreamReader(fis, "GB2312");
            br = new BufferedReader(isr);
            String s = "";
            while ((s = br.readLine()) != null) {
                // 替换字符
                s = s.replace("[", "");
                s = s.replace("]", "@");
                // 分离"@"字符
                String splitLrc_data[] = s.split("@");
                if (splitLrc_data.length > 1) {
                    mLrcContent.setLrc(splitLrc_data[1]);
                    // 处理歌词取得歌曲时间
                    int LrcTime = TimeStr(splitLrc_data[0]);
                    mLrcContent.setLrc_time(LrcTime);
                    // 添加进列表数组
                    LrcList.add(mLrcContent);
                    // 创建对象
                    mLrcContent = new LrcContent();
                }
                br.close();
                isr.close();
                fis.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            stringBuilder.append(R.string.no_read_file);
        } catch (IOException e) {
            e.printStackTrace();
            stringBuilder.append(R.string.no_lrc_file);
        } finally {
//            try {
//                //关闭流
//                br.close();
//                isr.close();
//                fis.close();
//            }
//            catch(IOException e)
//            {
//                e.printStackTrace();
//            }
        }
        return stringBuilder.toString();
    }

    /**
     * 获得歌曲时间
     */
    public int TimeStr(String timeStr) {
        timeStr = timeStr.replace(":", ".");
        timeStr = timeStr.replace(".", "@");
        String timeData[] = timeStr.split("@");
        int minute = Integer.parseInt(timeData[0]);
        int second = Integer.parseInt(timeData[1]);
        int millisecond = Integer.parseInt(timeData[2]);
        int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
        return currentTime;
    }

    public List<LrcContent> getLrcContent() {
        return LrcList;
    }

    /**
     * 获取歌词和时间返回的类
     */
    public class LrcContent {
        private String Lrc;
        private int Lrc_time;

        public String getLrc() {
            return Lrc;
        }

        public void setLrc(String lrc) {
            Lrc = lrc;
        }

        public int getLrc_time() {
            return Lrc_time;
        }

        public void setLrc_time(int lrc_time) {
            Lrc_time = lrc_time;
        }
    }
}
