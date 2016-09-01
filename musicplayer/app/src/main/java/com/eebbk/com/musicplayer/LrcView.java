package com.eebbk.com.musicplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义绘画歌词，产生滚动效果
 */
public class LrcView extends TextView {
    private float width;
    private float high;
    private Paint CurrentPaint;
    private Paint NotCurrentPaint;
    private float TextHigh = 25;
    private float TextSize = 18;
    private int Index = 0;
    private List<LrcProcess.LrcContent> mSentenceEntities = new ArrayList<LrcProcess.LrcContent>();

    public void setSentenceEntities(List<LrcProcess.LrcContent> mSentenceEntities) {
        this.mSentenceEntities = mSentenceEntities;
    }

    public LrcView(Context context) {
        super(context);
        init();
    }

    public LrcView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setFocusable(true);
        // 高亮部分
        CurrentPaint = new Paint();
        CurrentPaint.setAntiAlias(true);
        CurrentPaint.setTextAlign(Paint.Align.CENTER);
        // 非高亮部分
        NotCurrentPaint = new Paint();
        NotCurrentPaint.setAntiAlias(true);
        NotCurrentPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * 绘制
     *
     * @throws Exception 没文件
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) {
            return;
        }
        CurrentPaint.setColor(Color.argb(210, 251, 248, 29));
        NotCurrentPaint.setColor(Color.argb(140, 255, 255, 255));
        CurrentPaint.setTextSize(24);
        CurrentPaint.setTypeface(Typeface.SERIF);
        NotCurrentPaint.setTextSize(TextSize);
        NotCurrentPaint.setTypeface(Typeface.DEFAULT);
        try {
            setText("");
            canvas.drawText(mSentenceEntities.get(Index).getLrc(), width / 2,
                    high / 2, CurrentPaint);
            float tempY = high / 2;
            // 画出本句之前的句子
            for (int i = Index - 1; i >= 0; i--) {
                // 向上推移
                tempY = tempY - TextHigh;
                canvas.drawText(mSentenceEntities.get(i).getLrc(), width / 2,
                        tempY, NotCurrentPaint);
            }
            tempY = high / 2;
            // 画出本句之后的句子
            for (int i = Index + 1; i < mSentenceEntities.size(); i++) {
                // 往下推移
                tempY = tempY + TextHigh;
                canvas.drawText(mSentenceEntities.get(i).getLrc(), width / 2,
                        tempY, NotCurrentPaint);
            }
        } catch (Exception e) {
            setText(R.string.no_lrc_file);
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.high = h;
    }

    public void SetIndex(int index) {
        this.Index = index;
    }
}
