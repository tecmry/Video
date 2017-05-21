package com.example.tecmry.viedoplay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Video extends Activity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        View.OnClickListener {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ProgressBar mProgressbar;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private TextView vedioTiemTextView;
    private RelativeLayout Rl_parent;
    private String videoTimeString;
    private Boolean seekBarAutoFlag;
    private ImageView IV_start;
    private ImageView expand;
    private String url;
    private int total;
    private int process;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);
        initView();
        if (savedInstanceState!=null){
            int process = savedInstanceState.getInt("process");
            mediaPlayer.seekTo(process);
        }

    }
    /**
     * 用Intent获取Activity中的Url；
     * */
    private String getUrl(){
        Intent intent = getIntent();
        url = intent.getStringExtra("video_url");
        process = intent.getIntExtra("process",0);
        return url;
    }
    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.video_view);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar);
        seekBar = (SeekBar) findViewById(R.id.media_controller_progress);
        Rl_parent = (RelativeLayout) findViewById(R.id.video_inner_container);
        vedioTiemTextView = (TextView) findViewById(R.id.time);
        IV_start = (ImageView)findViewById(R.id.pause);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new CallBack());
        mProgressbar.setVisibility(View.GONE);
        IV_start.setImageResource(R.drawable.play);
    }



    private class CallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            //从这里调用Mediaplayer
            createMediaPlayer();
        }

        //对SurfaceView的大小进行调整
        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        //对SurfaceView进行销毁
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    private void createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
//        mediaPlayer.setOnErrorListener((MediaPlayer.OnErrorListener) this);
        //  mediaPlayer.setOnBufferingUpdateListener((MediaPlayer.OnBufferingUpdateListener) this);
        String main_url = getUrl();
        Uri uri = Uri.parse(main_url);
        try {
            mediaPlayer.setDataSource(this, uri);
            //设置加载方式
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * SurfaceView完成后监听事件的处理
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        // 当视频加载完毕以后，隐藏加载进度条
        mProgressbar.setVisibility(View.GONE);
        // 判断是否有保存的播放位置,防止屏幕旋转时，界面被重新构建，播放位置丢失。
        /**
         if (SyncStateContract.Constants.playPosition >= 0) {
         mediaPlayer.seekTo(SyncStateContract.Constants.playPosition);
         SyncStateContract.Constants.playPosition = -1;
         // surfaceHolder.unlockCanvasAndPost(Constants.getCanvas());
         }**/
        /**
         * 保持视频常驻
         * */

        mediaPlayer.setScreenOnWhilePlaying(true);
        surfaceHolder.setKeepScreenOn(true);
        // 设置控制条,放在加载完成以后设置，防止获取getDuration()错误
        seekBar.setProgress(0);
        seekBar.setMax(mediaPlayer.getDuration());
        videoTimeString = getShowTime(mediaPlayer.getDuration());
        total = mediaPlayer.getDuration();
        vedioTiemTextView.setText("00:00:00/" + videoTimeString);
        // 设置拖动监听事件
       seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
        seekBarAutoFlag = true;
        mediaPlayer.seekTo(process);
        int vWidth = mediaPlayer.getVideoWidth();
        int vHeight = mediaPlayer.getVideoHeight();

        int lw = Rl_parent.getWidth();
        int lh = Rl_parent.getHeight();
        System.out.println("宽"+lw);
        System.out.println("高"+ lh);

            float wRatio = (float) vWidth / (float) lw;
            float hRatio = (float) vHeight / (float) lh;
        float ratio = Math.max(wRatio, hRatio);
            vWidth = (int) Math.ceil((float) vWidth / ratio);
            vHeight = (int) Math.ceil((float) vHeight / ratio);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(vWidth, vHeight);
            surfaceView.setLayoutParams(lp);
 /**
 * 将button的监听器设置在onPrepare方法中避免出现State问题
 * */
       IV_start.setOnClickListener(this);
            thread.start();

    }
        @Override
        public void onClick (View view){
            switch (view.getId()) {
                case R.id.pause:

                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                       IV_start.setImageResource(R.drawable.play);
                    }else {
                        mediaPlayer.seekTo(process);
                        mediaPlayer.start();
                        IV_start.setImageResource(R.drawable.pause);
                    }
                    break;

            }
        }
        private Thread thread = new Thread() {

            public void run() {
                // TODO Auto-generated method stub
                super.run();
                try {
                    while (seekBarAutoFlag) {
                        if (null != Video.this.mediaPlayer
                                && Video.this.mediaPlayer.isPlaying()) {
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



        };

        private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                if (progress >= 0) {
                    // 如果是用户手动拖动控件，则设置视频跳转。
                    if (fromUser) {
                        mediaPlayer.seekTo(progress);
                    }
                    // 设置当前播放时间
                    vedioTiemTextView.setText(getShowTime(progress) + "/" + videoTimeString);
                }
            }


            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

        }
        @SuppressLint("SimpleDateFormat")
        public String getShowTime ( long milliseconds){
            // 获取日历函数
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliseconds);
            SimpleDateFormat dateFormat = null;
            // 判断是否大于60分钟，如果大于就显示小时。设置日期格式
            if (milliseconds / 60000 > 60) {
                dateFormat = new SimpleDateFormat("hh:mm:ss");
            } else {
                dateFormat = new SimpleDateFormat("mm:ss");
            }
            return dateFormat.format(calendar.getTime());
        }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("process",mediaPlayer.getCurrentPosition());
    }
    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            moveTaskToBack(false);
            doubleTouch();     //调用双击退出函数
        }
        return false;
    }
    private static Boolean isExit = false;
    private void doubleTouch() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            Intent intent = new Intent();
            intent.putExtra("PROCESS",mediaPlayer.getCurrentPosition());
            intent.putExtra("TOTAL",total);
            setResult(RESULT_OK,intent);
            finish();
            System.exit(0);
        }
    }
}
/**
 *
 * 出现（32,0） state4  竟然是因为layout出现了Error 但是不出现闪退
 * */

