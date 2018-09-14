package com.java.HuangBingjian;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.java.HuangBingjian.mimaxapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecommendActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private SearchView searchView;
    List<News> newsList = new ArrayList<>();
    NewsAdapter newsAdapter;
    GridLayoutManager layoutManager;
    RecyclerView recyclerView;
    MyClient myClient;
    SQLiteServer sqLiteServer;
    SQLiteDatabase database;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        Intent intent = getIntent();
        String keyword = intent.getStringExtra("recommend word");



//        newsList.add(new News("abc", "", "", "", ""));
        recyclerView = (RecyclerView) findViewById(R.id.recommend_recycle);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        newsAdapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(newsAdapter);

        myClient = new MyClient();

        searchNews(keyword);

        sqLiteServer = SQLiteServer.getInstance();
        database = sqLiteServer.getWritableDatabase();

        toolbar = (Toolbar) findViewById(R.id.recommend_toolbar);
        setSupportActionBar(toolbar);

        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                News singleNews = newsList.get(position);
                if(singleNews.content != null) {//null means visit from local without internet access
                    if (singleNews.visited == false) {
                        singleNews.visited = true;
                        if (sqLiteServer.updateData(database, singleNews.content, singleNews.title)) {
                            System.out.println("update visited news");
//                            System.out.println(sqLiteServer.checkVisited(database, singleNews.title));
                        } else
                            System.out.println("update failed" + singleNews.title);
                    }
                    Intent intent = new Intent(RecommendActivity.this, NewsPageActivity.class);
                    intent.putExtra("content", singleNews.convertToString());
                    startActivity(intent);
                    newsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void searchNews(final String keyword){
//        System.out.println("search");
        new Thread(new Runnable() {
            @Override
            public void run() {
//                System.out.println("search start");
                final JSONArray array = myClient.search(keyword);
//                System.out.println("request finished");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                if(array != null){
                    runOnUiThread(new Runnable() {// return back to main thread UI
                        @Override
                        public void run() {
//                            System.out.println("now in UIThread");
                            //do http things to load refreshing news
                            //newsAdapter.notifyDataSetChanged();
                            newsList.clear();
                            for (int i = array.length()-1; i >= 0; i--) {
                                try {
                                    JSONObject obj = (JSONObject) array.get(i);
                                    String title = obj.getString("title");
                                    String content = obj.getString("description");
                                    String imgURL = obj.getString("imgUrl");
                                    String resource = obj.getString("link");
                                    boolean visited = false;
                                    News singleNews = new News(title, content, imgURL, resource, "search channel", false);
                                    newsList.add(singleNews);
                                    if(newsList.size() == 50)
                                        break;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            newsAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }
}
