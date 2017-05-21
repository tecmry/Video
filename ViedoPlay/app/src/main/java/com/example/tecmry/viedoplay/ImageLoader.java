package com.example.tecmry.viedoplay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Tecmry on 2017/5/21.
 */

public class ImageLoader extends AsyncTask<String,Void,Bitmap> {
    private CircleView circleView;
    private SurfaceView surfaceView;
    public ImageLoader(CircleView circleView){
        this.circleView= circleView;
    }
    public ImageLoader(SurfaceView surfaceView){
        this.surfaceView = surfaceView;
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        URL url = null;
        Bitmap bitmap=null;
        InputStream inputStream =null;
        try {
            url = new URL(strings[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        circleView.setImageBitmap(bitmap);
    }



}
