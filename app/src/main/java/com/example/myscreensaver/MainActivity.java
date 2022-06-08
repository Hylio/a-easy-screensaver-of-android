package com.example.myscreensaver;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView01;
    private ImageView mImageView01;

    private LayoutInflater mInflater01;


    /* 输入解锁的View */
    private View mView01;
    private EditText mEditText02;

    /* 更改密码 */
    private LayoutInflater mInflaterc;
    private View mViewc;
    private LayoutInflater mInflaterc2;
    private View mViewc2;
    private EditText pswEdit1;
    private EditText pswEdit2;

    /* menu选项identifier，用以识别事件 */
    static final private int MENU_ABOUT = Menu.FIRST;
    static final private int MENU_EXIT = Menu.FIRST+1;
    static final private int MENU_PSW = Menu.FIRST+2;
    private Handler mHandler01 = new Handler();
    private Handler mHandler02 = new Handler();
    private Handler mHandler03 = new Handler();
    private Handler mHandler04 = new Handler();

    /* 控制User静止与否的Counter */
    private int intCounter1, intCounter2;

    /* 控制FadeIn 和 FadeOut的Counter */
    private int intCounter3, intCounter4;

    /* 控制循序替换背景图ID的Counter */
    private int intDrawable = 0;

    /* 上一次User有动作的Time Stamp */
    private Date lastUpdateTime;

    /* 计算User一共几秒没有动作 */
    private long timePeriod;

    /* 静止超过n秒将自动进入屏幕保护 */
    private float fHoldStillSecond = (float)5;
    private boolean bIfRunScreenSaver;
    private boolean bFadeFlagout, bFadeFlagin = false;
    private boolean PSW_checker = false;
    private long intervalScreenSaver = 1000;
    private long intervalKeypadeSaver = 1000;
    private long intervalFade = 100;
    private int screenWidth, screenHeight;
    private String mypassword = "831143";


    /* 每5秒置换一次图片 */
    private int intSecondsToChange = 5;

    /* 设置ScreenSaver需要用的图片*/
    private static int[] screenDrawble = new int[]{
            R.drawable.anmi,
            R.drawable.golden,
            R.drawable.jdly,
            R.drawable.raingirl
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 必须在setContentView 之前调用全屏幕显示 */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                );
        setContentView(R.layout.activity_main);


        /* onCreate all widget */
        mTextView01 = (TextView)findViewById(R.id.myTextView1);
        mImageView01 = (ImageView)findViewById(R.id.myImageView1);

        /* 初始取得User触碰手机的时间 */
        lastUpdateTime = new Date(System.currentTimeMillis());

        /* 初始化Layout上的Widget可见性 */
        recoverOriginalLayout();
    }

    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        /* menu 群组ID*/
        int idGroup1 = 0;

        /* menu项顺序 */
        int orderMenuItem1 = Menu.NONE;
        int orderMenuItem2 = Menu.NONE + 1;
        int orderMenuItem3 = Menu.NONE + 2;

        /* 创建具有submenu的menu */
        menu.add(
                idGroup1, MENU_ABOUT, orderMenuItem1, R.string.app_about
        );

        /* 创建退出menu */
        menu.add(idGroup1, MENU_EXIT, orderMenuItem2, R.string.str_exit);
        menu.setGroupCheckable(idGroup1, true, true);

        /* 创建修改密码menu */
        menu.add(idGroup1, MENU_PSW, orderMenuItem3, R.string.psw_change);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case (MENU_ABOUT):
                new AlertDialog.Builder(
                        MainActivity.this
                ).setTitle(R.string.app_about).setIcon(
                        R.drawable.icon
                ).setMessage(
                        R.string.app_made
                ).setPositiveButton(R.string.str_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                break;
            case (MENU_EXIT):
                finish();
                break;
            case(MENU_PSW):
                mInflaterc = LayoutInflater.from(MainActivity.this);
                mViewc = mInflaterc.inflate(R.layout.changepsw, null);
                /* 在对话框中等待输入旧密码 */
                pswEdit1 = (EditText) mViewc.findViewById(R.id.myEditText3);
                /* 创建AlertDialog */
                new AlertDialog.Builder(this).setView(mViewc).setPositiveButton(
                        "ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mypassword.equals(pswEdit1.getText().toString())){
                                    PSW_checker = true;
                                    mInflaterc2 = LayoutInflater.from(MainActivity.this);
                                    mViewc2 = mInflaterc2.inflate(R.layout.changepsw2, null);
                                    pswEdit2 = (EditText) mViewc2.findViewById(R.id.myEditText4);
                                    /* 创建AlertDialog */
                                    new AlertDialog.Builder(MainActivity.this).setView(mViewc2).setPositiveButton(
                                            "ok",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mypassword = pswEdit2.getText().toString();
                                                    PSW_checker = false;
                                                }
                                            }
                                    ).show();
                                }
                                else{
                                    new AlertDialog.Builder(
                                            MainActivity.this
                                    ).setTitle(R.string.app_about).setIcon(
                                            R.drawable.icon
                                    ).setMessage(
                                            R.string.psw_wrong
                                    ).setPositiveButton(R.string.str_ok,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).show();
                                }
                            }
                        }
                ).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /* 监控user没有动作的运行线程 */
    private Runnable mTasks01 = new Runnable() {
        @Override
        public void run() {
            intCounter1++;
            Date timeNow = new Date(System.currentTimeMillis());

            /* 计算user静止不动的时间间隔 */
            timePeriod = (long) timeNow.getTime() - (long) lastUpdateTime.getTime();

            float timePeriodSecond = ((float) timePeriod / 1000);
            if (timePeriodSecond > fHoldStillSecond) {
                /* 如果超过时间静止不动 */
                if (!bIfRunScreenSaver) {
                    /* 启动运行线程2 */
                    mHandler02.postDelayed(mTasks02, intervalScreenSaver);
                    /* fade out */
                    if (intCounter1 % (intSecondsToChange) == 0) {
                        bFadeFlagout = true;
                        mHandler03.postDelayed(mTasks03, intervalFade);
                    } else {
                        /* 在fade out后立即fade in */
                        if (bFadeFlagout) {
                            bFadeFlagin = true;
                            mHandler04.postDelayed(mTasks04, intervalFade);
                        } else {
                            bFadeFlagin = false;
                            intCounter4 = 0;
                            mHandler04.removeCallbacks(mTasks04);
                        }
                        intCounter3 = 0;
                        bFadeFlagout = false;
                    }
                    bIfRunScreenSaver = true;
                } else {
                    /* screen saver 正在运行中 */
                    /*fade out */
                    if (intCounter1 % (intSecondsToChange) == 0) {
                        bFadeFlagout = true;
                        mHandler03.postDelayed(mTasks03, intervalFade);
                    } else {
                        /* 在fade out 后立即fade in*/
                        if (bFadeFlagout) {
                            bFadeFlagin = true;
                            mHandler04.postDelayed(mTasks04, intervalFade);
                        } else {
                            bFadeFlagin = false;
                            intCounter4 = 0;
                            mHandler04.removeCallbacks(mTasks04);
                        }
                        intCounter3 = 0;
                        bFadeFlagout = false;
                    }
                }
            } else {
                /* 当user没有动作的时间间隔未超过时间 */
                bIfRunScreenSaver = false;
                /* 恢复原来的Layout visible */
                recoverOriginalLayout();
            }

            Log.i(
                    "icon",
                    "Counter1:" + Integer.toString(intCounter1)+'/' + Float.toString(timePeriodSecond)
            );
            /* 反复运行线程1 */
            mHandler01.postDelayed(mTasks01, intervalKeypadeSaver);
        }
    };

    private Runnable mTasks02 = new Runnable() {
        @Override
        public void run() {
            if(bIfRunScreenSaver){
                intCounter2++;
                hideOriginalLayout();
                showScreenSaver();
                mHandler02.postDelayed(mTasks02,intervalScreenSaver);
            }
            else{
                mHandler02.removeCallbacks(mTasks02);
            }
        }
    };

    /* Fade out 特效 runnable */
    private Runnable mTasks03 = new Runnable() {
        @Override
        public void run() {
            if (bIfRunScreenSaver && bFadeFlagout){
                intCounter3++;

                /* 设置ImageView的透明度渐渐按下去 */
                mImageView01.setAlpha(255-intCounter3*28);
                Log.i("icon", "Fade out:" + Integer.toString(intCounter3));
                mHandler03.postDelayed(mTasks03, intervalFade);
            }
            else{
                mHandler03.removeCallbacks(mTasks03);
            }
        }
    };

    private Runnable mTasks04 = new Runnable() {
        @Override
        public void run() {
            if (bIfRunScreenSaver && bFadeFlagin){
                intCounter4++;

                /* 设置ImageView的透明度渐渐亮起来 */
                mImageView01.setAlpha(intCounter4*28);

                mHandler04.postDelayed(mTasks04, intervalFade);
                Log.i("icon", "Fade in:" + Integer.toString(intCounter4));
            }
            else{
                mHandler04.removeCallbacks(mTasks04);
            }
        }
    };


    /* 恢复原有的Layout可视性 */
    private void recoverOriginalLayout(){
        //actionbar.show();
        mTextView01.setVisibility(View.VISIBLE);
        mImageView01.setVisibility(View.GONE);
    }

    /* 隐藏原有应用程序里的布局配置组件 */
    private void hideOriginalLayout(){
        /* 欲隐藏的Widget */
        //actionbar.hide();
        mTextView01.setVisibility(View.INVISIBLE);
    }


    /* 开始ScreenSaver */
    private void showScreenSaver(){
        /* 屏幕保护之后要做的事 */
        if(intDrawable>3){
            intDrawable = 0;
        }
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        Bitmap bmp= BitmapFactory.decodeResource(getResources(), screenDrawble[intDrawable]);

        /* matrix 比例 */
        float scaleWidth = ((float) screenWidth)/bmp.getWidth();
        float scaleHeight = ((float) screenHeight)/bmp.getHeight();

        Matrix matrix = new Matrix();
        /* 使用Matrix.postScale 设置维度Resize */
        matrix.postScale(scaleWidth, scaleHeight);
        //matrix.postScale(screenWidth, screenHeight);
        Bitmap resizeBitmap = Bitmap.createBitmap(
                bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true
        );

        /* 新建Drawable放大图文件至全屏幕 */
        BitmapDrawable myNewBitmapDrawable = new BitmapDrawable(getResources(),resizeBitmap);
        mImageView01.setImageDrawable(myNewBitmapDrawable);

        /* 使Image可见 */
        mImageView01.setVisibility(View.VISIBLE);

        /* 每间隔设置秒数置换图片ID， 于下一个runnable2生效 */
        if(intCounter2%intSecondsToChange==0){
            intDrawable++;
        }
    }


    public void onUserWakeUpEvent(){
        if(bIfRunScreenSaver){
            try{
                /* LayoutInflater.from 取得此Activity的context */
                mInflater01 = LayoutInflater.from(MainActivity.this);

                /* 创建解锁密码使用View 的layout */
                mView01 = mInflater01.inflate(R.layout.unlock, null);

                /* 在对话框中唯一的EditText 等待输入解锁密码 */
                mEditText02 = (EditText) mView01.findViewById(R.id.myEditText2);

                /* 创建AlertDialog */
                new AlertDialog.Builder(this).setView(mView01).setPositiveButton(
                        "ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mypassword.equals(mEditText02.getText().toString())){
                                    resetScreenSaverListener();
                                }
                                else{
                                    new AlertDialog.Builder(
                                            MainActivity.this
                                    ).setTitle(R.string.app_about).setIcon(
                                            R.drawable.icon
                                    ).setMessage(
                                            R.string.psw_wrong
                                    ).setPositiveButton(R.string.str_ok,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).show();
                                }
                            }
                        }
                ).show();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void updateUserActionTime(){
        /* 取得点击按键时间时的系统 Time Millis */
        Date timeNow = new Date(System.currentTimeMillis());

        /* 重新计算点击按键距离上一次静止的时间间距 */
        timePeriod = (long)timeNow.getTime() - (long)lastUpdateTime.getTime();
        lastUpdateTime.setTime(timeNow.getTime());
    }

    public void resetScreenSaverListener(){
        /* 删除现有的Runnable */
        mHandler01.removeCallbacks(mTasks01);
        mHandler02.removeCallbacks(mTasks02);

        /* 取得点击按键事件时的系统Time Millis */
        Date timeNow = new Date(System.currentTimeMillis());

        /* 重新计算点击按键距离上一次静止的时间间隔 */
        timePeriod = (long)timeNow.getTime() - (long)lastUpdateTime.getTime();
        lastUpdateTime.setTime(timeNow.getTime());

        /* for Runnable2, 取消屏幕保护 */
        bIfRunScreenSaver = false;

        /* 重置Runnable1 与 Runnable2 的Counter */
        intCounter1 = 0;
        intCounter2 = 0;

        /* 恢复原来的 Layout Visible */
        recoverOriginalLayout();

        /* 重新postDelayed() 新的Runnable */
        mHandler01.postDelayed(mTasks01, intervalKeypadeSaver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (bIfRunScreenSaver && keyCode!=4){
            /* 当屏幕保护程序正在运行中，触动解除屏幕保护程序 */
            onUserWakeUpEvent();
        }
        else{
            /* 更新User未触动手机的时间戳记 */
            updateUserActionTime();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (bIfRunScreenSaver){
            /* 当屏幕保护程序正在运行中， 触动接触屏幕保护程序 */
            onUserWakeUpEvent();
        }
        else{
            /* 更新User 未触动手机的时间戳记 */
            updateUserActionTime();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume(){
        mHandler01.postDelayed(mTasks01, intervalKeypadeSaver);
        super.onResume();
    }

    @Override
    protected void onPause(){
        try{
            mHandler01.removeCallbacks(mTasks01);
            mHandler02.removeCallbacks(mTasks02);
            mHandler03.removeCallbacks(mTasks03);
            mHandler04.removeCallbacks(mTasks04);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        super.onPause();
    }
}
