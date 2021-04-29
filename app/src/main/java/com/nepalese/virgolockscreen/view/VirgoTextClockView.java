package com.nepalese.virgolockscreen.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nepalese.virgolockscreen.R;
import com.nepalese.virgolockscreen.data.clockBean;

import java.util.Calendar;
import java.util.Date;

/**
 * @author nepalese on 2021/4/6 14:31
 * @usage
 */
public class VirgoTextClockView extends View {
    private static final String TAG = "VirgoTextClockView";

    private static final int MIN_WIDTH = 200;
    private static final String[] HOURS = {"一点","二点","三点","四点","五点","六点","七点","八点","九点","十点","十一点","十二点"};
    private static final String[] MINUTES = {"一分","二分","三分","四分","五分","六分","七分","八分","九分","十分",
            "十一分","十二分","十三分","十四分","十五分","十六分","十七分","十八分","十九分","二十分",
            "二十一分","二十二分","二十三分","二十四分","二十五分","二十六分","二十七分","二十八分","二十九分","三十分",
            "三十一分","三十二分","三十三分","三十四分","三十五分","三十六分","三十七分","三十八分","三十九分","四十分",
            "四十一分","四十二分","四十三分","四十四分","四十五分","四十六分","四十七分","四十八分","四十九分","五十分",
            "五十一分","五十二分","五十三分","五十四分","五十五分","五十六分","五十七分","五十八分","五十九分",""};

    private static final String[] SECONDS = {"一秒","二秒","三秒","四秒","五秒","六秒","七秒","八秒","九秒","十秒",
            "十一秒","十二秒","十三秒","十四秒","十五秒","十六秒","十七秒","十八秒","十九秒","二十秒",
            "二十一秒","二十二秒","二十三秒","二十四秒","二十五秒","二十六秒","二十七秒","二十八秒","二十九秒","三十秒",
            "三十一秒","三十二秒","三十三秒","三十四秒","三十五秒","三十六秒","三十七秒","三十八秒","三十九秒","四十秒",
            "四十一秒","四十二秒","四十三秒","四十四秒","四十五秒","四十六秒","四十七秒","四十八秒","四十九秒","五十秒",
            "五十一秒","五十二秒","五十三秒","五十四秒","五十五秒","五十六秒","五十七秒","五十八秒","五十九秒",""};

    private Paint mPaintMain;//当前指定的文字画笔
    private Paint mPaintSecond;//基础文字画笔
    private Paint mPaintClock;//中间数字时钟画笔
    private ValueAnimator mAnimator;

    private int mColorMain;//选中字体颜色
    private int mColorSecond;//基础色
    private int mColorBg;//背景颜色

    private int mWidth, mHeight;//宽高，相等
    private int mH, mM, mS;//当前的时分秒
    private int mH24;//24制小时
    private int mRadiusH, mRadiusM, mRadiusS;//三个同心圆的半径
    private String mWeek, mDate;//星期，日期

    private float mDegreeH, mDegreeM, mDegreeS;//时分秒旋转角度
    private float mTextSizeMain;//通用字体大小
    private float mTextSizeClock;//中间字体大小
    private float mOffset;//中间文字与中心线偏距
    private float mCenterHeight;//文字纵向居中高度

    public VirgoTextClockView(Context context) {
        this(context, null);
    }

    public VirgoTextClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoTextClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.VirgoTextClockView);
        mColorMain = typedArray.getColor(R.styleable.VirgoTextClockView_vtcTextColorMain, Color.WHITE);
        mColorSecond =  typedArray.getColor(R.styleable.VirgoTextClockView_vtcTextColorSec, Color.GRAY);
        mColorBg =  typedArray.getColor(R.styleable.VirgoTextClockView_vtcColorBg, Color.BLACK);

        mTextSizeMain = typedArray.getDimension(R.styleable.VirgoTextClockView_vtcTextSizeMain, 25f);
        mTextSizeClock = typedArray.getDimension(R.styleable.VirgoTextClockView_vtcTextSizeClock, 35f);

        mRadiusH = typedArray.getDimensionPixelSize(R.styleable.VirgoTextClockView_vtcRadiusH, 140);
        mRadiusM = typedArray.getDimensionPixelSize(R.styleable.VirgoTextClockView_vtcRadiusM, 220);
        mRadiusS = typedArray.getDimensionPixelSize(R.styleable.VirgoTextClockView_vtcRadiusS, 320);
        mOffset =  typedArray.getDimensionPixelSize(R.styleable.VirgoTextClockView_vtcOffset, 8);
        typedArray.recycle();

        mPaintMain = new Paint();
        mPaintMain.setAntiAlias(true);
        mPaintMain.setColor(mColorMain);
        mPaintMain.setTextSize(mTextSizeMain);
        mPaintMain.setStyle(Paint.Style.FILL);

        mPaintSecond = new Paint();
        mPaintSecond.setAntiAlias(true);
        mPaintSecond.setColor(mColorSecond);
        mPaintSecond.setTextSize(mTextSizeMain);
        mPaintSecond.setStyle(Paint.Style.FILL);

        mPaintClock = new Paint();
        mPaintClock.setAntiAlias(true);
        mPaintClock.setColor(mColorMain);
        mPaintClock.setTextSize(mTextSizeClock);
        mPaintClock.setStyle(Paint.Style.FILL);

        initAnimator();
        getCurTime();
    }

    //线性旋转动画
    private void initAnimator() {
        mAnimator = ValueAnimator.ofFloat(6f, 0f);//由6降到1
        mAnimator.setDuration(150);
        mAnimator.setInterpolator(new LinearInterpolator());//插值器设为线性
    }

    private void getCurTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        mH24 = calendar.get(Calendar.HOUR_OF_DAY);
        mH = calendar.get(Calendar.HOUR);//12制[0-11]
        mM = calendar.get(Calendar.MINUTE);
        mS = calendar.get(Calendar.SECOND);

        parseWeek(calendar.get(Calendar.DAY_OF_WEEK));
        parseDate(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        calculateDegree();
    }

    private void calculateDegree() {
        //逆时针旋转
        mDegreeH = -360 / 12f * (mH - 1);
        mDegreeM = -360 / 60f * (mM - 1);
        mDegreeS = -360 / 60f * (mS - 1);
    }

    private void parseWeek(int week){
        switch (week){
            case 1:
                mWeek = "星期日";
                break;
            case 2:
                mWeek = "星期一";
                break;
            case 3:
                mWeek = "星期二";
                break;
            case 4:
                mWeek = "星期三";
                break;
            case 5:
                mWeek = "星期四";
                break;
            case 6:
                mWeek = "星期五";
                break;
            case 7:
                mWeek = "星期六";
                break;
        }
    }

    private void parseDate(int month, int day) {
        mDate = foramtTime(month, day, "/");
    }

    //规范数字时钟
    private String foramtTime(int h, int m, String tap){
        String sH, sM;
        if(h<10){
            sH = "0"+h;
        }else {
            sH = String.valueOf(h);
        }

        if(m<10){
            sM = "0"+m;
        }else {
            sM = String.valueOf(m);
        }

        return sH + tap + sM;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getRealSize(widthMeasureSpec);
        mHeight = getRealSize(heightMeasureSpec);

        mHeight = mWidth = Math.min(mWidth, mHeight);
        mCenterHeight = (mHeight+getFontHeight(mTextSizeMain))/2f;
        setMeasuredDimension(mWidth, mHeight);
    }

    private int getRealSize(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.UNSPECIFIED) {
            result = MIN_WIDTH;
        } else {
            result = size;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景
        canvas.drawColor(mColorBg);

        //中心数字时间，星期,日期
        drawTimeInfo(canvas);

        //时针圈
        drawHour(canvas);

        //分针圈
        drawMinute(canvas);

        //秒针圈
        drawSecond(canvas);
    }

    private void drawTimeInfo(Canvas canvas){
        String time = foramtTime(mH24, mM, "\t:\t");
        canvas.drawText(time, (mWidth-mPaintClock.measureText(time))/2f,
                (mHeight - getFontHeight(mTextSizeClock))/2f + mOffset, mPaintClock);

        String date = mDate + "\t" + mWeek;
        canvas.drawText(date, (mWidth-mPaintMain.measureText(date))/2f,
                (mHeight + getFontHeight(mTextSizeMain))/2f + mOffset, mPaintMain);
    }

    private void drawHour(Canvas canvas) {
        canvas.save();
        canvas.rotate(mDegreeH, mWidth/2f, mHeight/2f);

        //时针圈: 360/12 = 30度
        for(int i = 0; i< HOURS.length; i++){
            canvas.save();
            canvas.rotate(30*i, mWidth/2f, mHeight/2f);

            if(i+1==mH ||(i==11&&mH==0)){
                //当前时
                canvas.drawText(HOURS[i], mRadiusH + mWidth/2f, mCenterHeight, mPaintMain);
            }else{
                canvas.drawText(HOURS[i], mRadiusH + mWidth/2f, mCenterHeight, mPaintSecond);
            }
            canvas.restore();
        }
        canvas.restore();
    }

    private void drawMinute(Canvas canvas) {
        canvas.save();
        canvas.rotate(mDegreeM, mWidth/2f, mHeight/2f);

        //分针圈: 360/60 = 6度
        for(int i = 0; i< MINUTES.length; i++){
            canvas.save();
            canvas.rotate(6*i, mWidth/2f, mHeight/2f);

            if(i+1==mM){
                //当前分钟
                canvas.drawText(MINUTES[i], mRadiusM + mWidth/2f, mCenterHeight, mPaintMain);
            }else{
                canvas.drawText(MINUTES[i], mRadiusM + mWidth/2f, mCenterHeight, mPaintSecond);
            }
            canvas.restore();
        }
        canvas.restore();
    }

    private void drawSecond(Canvas canvas) {
        canvas.save();
        canvas.rotate(mDegreeS, mWidth/2f, mHeight/2f);

        //秒针圈: 360/60 = 6度
        for(int i = 0; i< SECONDS.length; i++){
            canvas.save();
            canvas.rotate(6*i, mWidth/2f, mHeight/2f);

            if(i+1==mS){
                //当前秒
                canvas.drawText(SECONDS[i], mRadiusS + mWidth/2f, mCenterHeight, mPaintMain);
            }else{
                canvas.drawText(SECONDS[i], mRadiusS + mWidth/2f, mCenterHeight, mPaintSecond);
            }
            canvas.restore();
        }
        canvas.restore();
    }

    //获取指定大小文字的高度
    private float getFontHeight(float textSize) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    private final Runnable clockTask = new Runnable() {
        @Override
        public void run() {
            updateClock();
            handler.postDelayed(clockTask, 1000);
        }
    };

    private void updateClock(){
        getCurTime();
        float hd = mDegreeH;
        float md = mDegreeM;
        float sd = mDegreeS;

        mAnimator.removeAllUpdateListeners();
        mAnimator.addUpdateListener(animation -> {
            float av = (float) animation.getAnimatedValue();

            if (mM == 0 && mS == 0) {
                mDegreeH = hd + av * 5;//时圈旋转角度是分秒的5倍，线性的旋转30°
            }

            if (mS == 0) {
                mDegreeM = md + av;//线性的旋转6°
            }

            mDegreeS = sd + av;//线性的旋转6°
            invalidate();
        });

        mAnimator.start();
    }

    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAnimator.removeAllUpdateListeners();
        stopClock();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(visibility!=VISIBLE){
            stopClock();
        }else{
            startClock();
        }
    }

    /////////////////////////////////////////////api//////////////////////////////////////////////
    public void startClock(){
        stopClock();
        handler.post(clockTask);
    }

    public void stopClock() {
        handler.removeCallbacks(clockTask);
    }

    public void setmColorMain(@ColorInt int mColorMain) {
        this.mColorMain = mColorMain;
        mPaintMain.setColor(mColorMain);
        mPaintClock.setColor(mColorMain);
    }

    public void setmColorSecond(@ColorInt int mColorSecond) {
        this.mColorSecond = mColorSecond;
        mPaintSecond.setColor(mColorSecond);
    }

    public void setmColorBg(@ColorInt int mColorBg) {
        this.mColorBg = mColorBg;
    }

    public void setmRadiusH(int mRadiusH) {
        this.mRadiusH = mRadiusH;
    }

    public void setmRadiusM(int mRadiusM) {
        this.mRadiusM = mRadiusM;
    }

    public void setmRadiusS(int mRadiusS) {
        this.mRadiusS = mRadiusS;
    }


    public void setmTextSizeMain(float mTextSizeMain) {
        this.mTextSizeMain = mTextSizeMain;
        mPaintMain.setTextSize(mTextSizeMain);
        mPaintSecond.setTextSize(mTextSizeMain);
    }

    public void setmTextSizeClock(float mTextSizeClock) {
        this.mTextSizeClock = mTextSizeClock;
        mPaintClock.setTextSize(mTextSizeClock);
    }

    public void setmOffset(float mOffset) {
        this.mOffset = mOffset;
    }

    public void setConfig(clockBean bean){
        this.setmColorMain(Color.parseColor(bean.getColorSelect()));
        this.setmColorSecond(Color.parseColor(bean.getColorDefault()));

        this.setmTextSizeMain(bean.getSizeClock());
        this.setmTextSizeClock(bean.getSizeCenter());
        this.setmOffset(bean.getOffset());
        this.setmRadiusH(bean.getrHour());
        this.setmRadiusM(bean.getrMinute());
        this.setmRadiusS(bean.getrSecond());
    }
}
