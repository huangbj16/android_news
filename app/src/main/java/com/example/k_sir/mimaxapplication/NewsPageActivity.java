package com.example.k_sir.mimaxapplication;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLData;

public class NewsPageActivity extends AppCompatActivity{

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

    public static String getNewContent(String htmltext){
        try {
            Document doc= Jsoup.parse(htmltext);
            Elements elements=doc.getElementsByTag("img");
            for (Element element : elements) {
                element.attr("width","100%").attr("height","auto");
            }
            return doc.toString();
        } catch (Exception e) {
            return htmltext;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.newspage_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.collect:
                Toast.makeText(this, "Add to Collection", Toast.LENGTH_SHORT).show();
                mark();
                break;
            case R.id.share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                shareToOther();
                break;
        }
        return true;
    }

    private void shareToOther(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        URI uri = null;
        try {
            uri = new URL(imageString).toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if(uri != null) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
        }
        else {
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, title.getText().toString());
        startActivity(Intent.createChooser(shareIntent, "分享"));
    }

    private void mark(){
        return;
    }
}
