package com.java.HuangBingjian;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.java.HuangBingjian.mimaxapplication.R;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {
    private List<News> newsList = new ArrayList<>();
    private NewsAdapter newsAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private SQLiteServer sqLiteServer;
    private SQLiteDatabase database;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        sqLiteServer = SQLiteServer.getInstance();
        database = sqLiteServer.getWritableDatabase();

        newsList = sqLiteServer.queryMark(database);

        recyclerView = (RecyclerView) findViewById(R.id.collection_recycle);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        newsAdapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(newsAdapter);

        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                News singleNews = newsList.get(position);
                Intent intent = new Intent(CollectionActivity.this, NewsPageActivity.class);
                intent.putExtra("content", singleNews.convertToString());
                startActivityForResult(intent, 10);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.collection_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collection_toolbar, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10 && resultCode == 99){
//            System.out.println("mark changed");
            newsList.clear();
            List<News> tempList = sqLiteServer.queryMark(database);
            for (News news : tempList) {
                newsList.add(news);
            }
            newsAdapter.notifyDataSetChanged();
        }
    }
}
