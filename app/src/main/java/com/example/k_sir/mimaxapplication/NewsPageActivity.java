package com.example.k_sir.mimaxapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

public class NewsPageActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    String contentString = null;
    TextView title;
    ImageView imageView;
    Toolbar toolbar;
    JSONObject obj;
    WebView webView;
    String titleString = "Default";
    String imageString = "http://rss.people.com.cn/img/2014peoplelogo/rss_logo.gif";
    String description = "Default";
    String resource = "Default";
    String channel = "Default";
    SQLiteServer server;
    SQLiteDatabase database;
    boolean marked = false;
    boolean originalMarked;
    private String[] mPermissionList = new String[]{Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i("jxd", "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i("jxd", "onPermissionsDenied:" + requestCode + ":" + perms.size());
    }

    class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_page);

        Intent intent = getIntent();
        contentString = intent.getStringExtra("content");

        server = SQLiteServer.getInstance();
        database = server.getWritableDatabase();

        try {
            obj = new JSONObject(contentString);
            titleString = obj.getString("title");
            description = obj.getString("content");
            imageString = obj.getString("imgUrl");
            resource = obj.getString("resource");
            channel = obj.getString("channel");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        toolbar = (Toolbar) findViewById(R.id.news_page_toolbar);
        setSupportActionBar(toolbar);

        if(server.checkMarked(database, titleString))
            marked = true;
        originalMarked = marked;
        setResult(Activity.RESULT_OK);

        initWebView(description);

        title = (TextView) findViewById(R.id.news_page_title);
        title.setText(titleString);
        imageView = (ImageView) findViewById(R.id.news_page_image);
        Glide.with(this).load(imageString).into(imageView);


    }

    private void initWebView(String description) {
        webView = (WebView) findViewById(R.id.news_page_content);
        WebSettings settings = webView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
        settings.setJavaScriptEnabled(true);//支持js
        webView.loadDataWithBaseURL(null, getNewContent(description), "text/html", "utf-8", null);
        webView.setWebViewClient(new MyWebClient());
    }

    private static String processingImgUrl(String imgUrl){
        if(!imgUrl.startsWith("http"))
            imgUrl = "http://www.people.com.cn" + imgUrl;
        return imgUrl;
    }

    public static String getNewContent(String htmltext){
        try {
//            System.out.println(htmltext);
            Document doc= Jsoup.parse(htmltext);
//            System.out.println(doc.toString());
            Elements elements=doc.getElementsByTag("img");
            for (Element element : elements) {
                element.attr("width","100%").attr("height","auto");
                element.attr("style", "");
                String imgUrl = element.attr("src");
                element.attr("src", processingImgUrl(imgUrl));
            }
//            System.out.println(doc.toString());
            return doc.toString();
        } catch (Exception e) {
            return htmltext;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.newspage_toolbar, menu);
        if(marked)
            menu.findItem(R.id.collect).setIcon(R.drawable.ic_check_circle_pink_700_24dp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.collect:
//                Toast.makeText(this, "Add to Collection", Toast.LENGTH_SHORT).show();
                if(!marked) {
                    if (mark()) {
                        item.setIcon(R.drawable.ic_check_circle_pink_700_24dp);
                        marked = true;
                    }
                }
                else{
                    if(server.deleteMark(database, titleString)) {
                        item.setIcon(R.drawable.ic_library_add_white_24dp);
                        marked = false;
                    }
                }
                if(originalMarked != marked)//changed
                    setResult(99);
                else
                    setResult(Activity.RESULT_OK);//not changed
                break;
            case R.id.share:
//                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                shareToOther();
                break;
        }
        return true;
    }

    private void shareToOther(){
        if(!EasyPermissions.hasPermissions(NewsPageActivity.this, mPermissionList)){
            EasyPermissions.requestPermissions(NewsPageActivity.this, "permission requested", 1, mPermissionList);
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
//        System.out.println(imageString);
        Uri uri = storeImg(imageString);
//        System.out.println(uri);
        if(uri != null) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
        }
        else {
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, "标题：" + titleString + "\n" + "链接：" + resource + "\n");
        startActivity(Intent.createChooser(shareIntent, "分享"));
    }

    private boolean mark(){
        if(server.markNews(database, titleString, imageString, description, resource, channel))
            return true;
        else
            return false;
    }


    private Uri storeImg(String url){
        return storeBitmap(returnBitmap(url));
    }

    private Bitmap returnBitmap(String url) {

        if (url == null)
            return null;

        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        final List<Bitmap> tmplist = new ArrayList<>();
        final URL tmpUrl = fileUrl;
        Thread tmp = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) tmpUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    Log.d("get connection", "connection");

                    tmplist.add(BitmapFactory.decodeStream(is));
                    Log.d("get", "get bitmap");

                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        tmp.start();
        try {
            tmp.join();
            return tmplist.get(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

    }
    private Uri storeBitmap(Bitmap bitmap){
//        System.out.println(bitmap);
        if (bitmap == null)
            return null;
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/news/";
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
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
