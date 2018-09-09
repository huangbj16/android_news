package com.example.k_sir.mimaxapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private Toolbar toolbar;
    private SearchView searchView;
    List<News> newsList = new ArrayList<>();
    NewsAdapter newsAdapter;
    GridLayoutManager layoutManager;
    RecyclerView recyclerView;
    MyClient myClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);

        newsList.add(new News("abc", "", "", "", ""));
        recyclerView = (RecyclerView) findViewById(R.id.search_recycle);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        newsAdapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(newsAdapter);

        myClient = new MyClient();

        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                News singleNews = newsList.get(position);
//                Toast.makeText(MainActivity.this, singleNews.content, Toast.LENGTH_SHORT).show();
                /////////////////open another activity for single news
                Intent intent = new Intent(SearchActivity.this, NewsPageActivity.class);
                intent.putExtra("content", singleNews.convertToString());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_toolbar, menu);

        //设置搜索输入框的步骤

        //1.查找指定的MemuItem
        MenuItem menuItem = menu.findItem(R.id.action_search);

      /*  //2.设置SearchView v4 包方式
        View view = SearchViewCompat.newSearchView(this);
//        menuItem.setActionView(view);
        MenuItemCompat.setActionView(menuItem, view);*/

        //2.设置SearchView v7包方式
        View view = MenuItemCompat.getActionView(menuItem);
        if (view != null) {
            searchView = (SearchView) view;
            //4.设置SearchView 的查询回调接口
            searchView.setOnQueryTextListener(this);

            //在搜索输入框没有显示的时候 点击Action ,回调这个接口，并且显示输入框
//            searchView.setOnSearchClickListener();
            //当自动补全的内容被选中的时候回调接口
//            searchView.setOnSuggestionListener();

            //可以设置搜索的自动补全，或者实现搜索历史
//            searchView.setSuggestionsAdapter();

        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Toast.makeText(SearchActivity.this, "Submit" + s, Toast.LENGTH_SHORT).show();
        searchView.clearFocus();
        searchNews(s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Toast.makeText(SearchActivity.this, "" + s, Toast.LENGTH_SHORT).show();
        searchNews(s);
        return true;
    }

    private void searchNews(final String keyword){
        System.out.println("search");
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("search start");
                final JSONArray array = myClient.search(keyword);
                System.out.println("request finished");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                if(array != null){
                    runOnUiThread(new Runnable() {// return back to main thread UI
                        @Override
                        public void run() {
                            System.out.println("now in UIThread");
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
                                    News singleNews = new News(title, content, imgURL, resource, "search channel");
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
