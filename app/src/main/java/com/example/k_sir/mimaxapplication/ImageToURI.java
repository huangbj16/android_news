package com.example.k_sir.mimaxapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class ImageToURI {
    static Uri fromImageToURI(Context context, final String imgUrl){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = getImageFromNet(imgUrl);

            }
        }).start();
        return null;
    }

    private static Bitmap getImageFromNet(String url) {
        Log.d("Magic",""+url);
        HttpURLConnection conn = null;
        try {
            URL mURL = new URL(url);
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET"); //设置请求方法
            conn.setConnectTimeout(3000); //设置连接服务器超时时间
            conn.setReadTimeout(3000);  //设置读取数据超时时间

            conn.connect(); //开始连接

            int responseCode = conn.getResponseCode(); //得到服务器的响应码
            if (responseCode == 200) {
                //访问成功
                InputStream is = conn.getInputStream(); //获得服务器返回的流数据
                Bitmap bitmap = BitmapFactory.decodeStream(is); //根据流数据 创建一个bitmap对象
                return bitmap;

            } else {
                //访问失败
                Log.d("Magic", "访问失败===responseCode：" + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect(); //断开连接
            }
        }
        return null;
    }

    private Uri storeBitmap(Bitmap bitmap){
        if (bitmap == null)
            return null;
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/shareimage/";
        File dirfile = new File(dir);
        if (!dirfile.exists())
            dirfile.mkdir();

        String state = Environment.getExternalStorageState();
        //如果状态不是mounted，无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        //通过UUID生成字符串文件名
        String fileName = UUID.randomUUID().toString();
        try {
            File file = new File(dir + fileName + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            Uri uri = Uri.fromFile(file);
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }
}
