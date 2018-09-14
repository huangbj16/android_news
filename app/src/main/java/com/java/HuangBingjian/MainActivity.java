package com.java.HuangBingjian;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.java.HuangBingjian.mimaxapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

//    News single_news = new News("title", "content", "https://inews.gtimg.com/newsapp_bt/0/5127055360/1000", "qq", "新闻");

    private News[] news = new News[20];

    private List<News> newsList = new ArrayList<>();
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String[] channels = new String[]{"国内新闻", "国际新闻", "经济新闻", "金融频道", "体育新闻", "教育新闻", "游戏新闻", "娱乐频道", "科技频道", "台湾新闻", "英文频道"};
    private ArrayList<String> channelList = new ArrayList<>();
    final String TAG = "TabActivity";
    private TabLayout tabLayout;
    //private ViewPager viewPager;
    private String currentChannel;
    private MyClient myClient;
    private GridLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private SQLiteServer sqLiteServer;
    private SQLiteDatabase database;
    private NavigationView navView;
    private String recommendKeyword = "习近平";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqLiteServer = SQLiteServer.getInstance(getApplicationContext(), "news", null, 6);
        database = sqLiteServer.getWritableDatabase();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                System.out.println("start");
                refreshNews();
                recyclerView.scrollToPosition(0);
                //swipeRefreshLayout.setRefreshing(false);
            }
        });
        myClient = new MyClient();

        initNews();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        newsAdapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(newsAdapter);

        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                News singleNews = newsList.get(position);
                if(singleNews.content != null) {//null means visit from local without internet access
                    recommendKeyword = singleNews.title.substring(0, 2);
                    if (singleNews.visited == false) {
                        singleNews.visited = true;
                        if (sqLiteServer.updateData(database, singleNews.content, singleNews.title)) {
                            System.out.println("update visited news");
//                            System.out.println(sqLiteServer.checkVisited(database, singleNews.title));
                        } else
                            System.out.println("update failed" + singleNews.title);
                    }
//                Toast.makeText(MainActivity.this, singleNews.content, Toast.LENGTH_SHORT).show();
                    /////////////////open another activity for single news
                    Intent intent = new Intent(MainActivity.this, NewsPageActivity.class);
                    intent.putExtra("content", singleNews.convertToString());
                    startActivity(intent);
                    newsAdapter.notifyDataSetChanged();
                }
            }
        });

//        recyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println(v.getId());
//            }
//        });

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }

        for (String channel : channels) {
            channelList.add(channel);
        }
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        for (String channel : channelList) {
            tabLayout.addTab(tabLayout.newTab().setText(channel));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //change channel and news
//                System.out.println(tab.getText());
                currentChannel = tab.getText().toString();
                swipeRefreshLayout.setRefreshing(true);
                refreshNews();
                recyclerView.scrollToPosition(0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        navView = (NavigationView) findViewById(R.id.nav_view);
//        navView.setCheckedItem(R.id.nav_colletion);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, CollectionActivity.class);
                startActivity(intent);
                return true;
            }
        });
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, data);
//        final ListView listView = (ListView) findViewById(R.id.list_view);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String name = data[position];
//                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private static String processingImgUrl(String imgUrl){
        if(!imgUrl.startsWith("http"))
            imgUrl = "http://www.people.com.cn" + imgUrl;
        return imgUrl;
    }

    private String processingContent(String content){
//        System.out.println(content);
        return content;
    }

    private void refreshNews() {
//        System.out.println("refresh");
        new Thread(new Runnable() {
            @Override
            public void run() {
//                System.out.println("refresh start");
                final JSONArray array = myClient.refresh(currentChannel);
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
                                    String content = processingContent(obj.getString("description"));
                                    String imgURL = processingImgUrl(obj.getString("imgUrl"));
                                    String resource = obj.getString("link");
                                    News singleNews = new News(title, content, imgURL, resource, currentChannel, false);
//                                    if(sqLiteServer.insertData(database, title, null, imgURL, resource, false, currentChannel))
//                                        System.out.println("insertSuccess");
//                                    else {
//                                        System.out.println("insertFailed");
//                                        System.out.println("check if visited");
//                                        if(sqLiteServer.checkVisited(database, title))
//                                            singleNews.visited = true;
//                                    }
                                    newsList.add(singleNews);
                                    if(newsList.size() == 50)
                                        break;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            for (int i = newsList.size() - 1; i >= 0; i--) {//insert according to date
                                News singleNews = newsList.get(i);
                                if(sqLiteServer.insertData(database, singleNews.title, null, singleNews.imgUrl, singleNews.resource, false, currentChannel))
                                    System.out.println("insertSuccess");
                                else {
//                                    System.out.println("insertFailed");
//                                    System.out.println("check if visited");
                                    if(sqLiteServer.checkVisited(database, singleNews.title))
                                        singleNews.visited = true;
                                }
                            }
                            newsAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
//                            storeNewsListInCache(MainActivity.this, newsList, currentChannel);
                        }
                    });
                }
                else{//connection failed
//                    System.out.println("uselocaldata");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            System.out.println(currentChannel);
                            newsList.clear();
                            ArrayList<News> tempList = sqLiteServer.queryData(database, currentChannel);
//                            newsList = tempList;
//                            for (News news1 : newsList) {
//                                System.out.println(news1.title + " " + news1.channel);
//                            }
                            for (int i = tempList.size() - 1; i >= 0; i--) {
                                newsList.add(tempList.get(i));
                                if(newsList.size() == 50)
                                    break;
                            }
                            newsAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
//                    System.out.println("localquerysuccess");
                }
            }
        }).start();
    }



    private void initNews(){
//        System.out.println("init");
        currentChannel = "国内新闻";
//        newsList = loadNewsListFromCache(MainActivity.this, currentChannel);
        refreshNews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
//            case R.id.upload:
////                Toast.makeText(this, "backup", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.search:
//                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
                transferToSearch();
                break;
            case R.id.manage:
//                Toast.makeText(this, "manage", Toast.LENGTH_SHORT).show();
                manageChannel();
                break;
            case R.id.recommend:
                gotoRecommendation();
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    private void gotoRecommendation(){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, RecommendActivity.class);
        intent.putExtra("recommend word", recommendKeyword);
        startActivity(intent);
    }

    private void transferToSearch(){
//        System.out.println("transfer");
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void manageChannel(){
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("chosenList", channelList);
        Intent intent = new Intent(MainActivity.this, ManageActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
//        Intent intent = new Intent(MainActivity.this, NewsPageActivity.class);
//        intent.putExtra("content", singleNews.convertToString());
//        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            channelList = bundle.getStringArrayList("returnList");
            currentChannel = channelList.get(0);

            refreshChannels();

            refreshNews();
        }
    }

    private void refreshChannels(){
        tabLayout.removeAllTabs();
        for (String s : channelList) {
            tabLayout.addTab(tabLayout.newTab().setText(s));
        }
    }

//    public static void storeNewsListInCache(final Context context, final List<News> newsList, final String storeChannel){
//        System.out.println("store " + storeChannel);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                File path = context.getCacheDir();
//                File cache = new File(path, storeChannel);
//                if(cache.exists())
//                    cache.delete();
//                try {
//                    ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(cache));
//                    outputStream.writeObject(newsList);
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//    }
//
//    public List<News> loadNewsListFromCache(final Context context, final String loadChannel){
//        System.out.println("load " + loadChannel);
//        final List<News> returnList = new ArrayList<>();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                File path = context.getCacheDir();
//                File[] fileList = path.listFiles();
//                for (File file : fileList) {
//                    System.out.println(file.getName());
//                }
//                File cache = new File(path, loadChannel);
//                if(cache.exists()) {
//                    try {
//                        System.out.println("exist");
//                        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(cache));
//                        final List<News> tempList = (List<News>) inputStream.readObject();
//                        if(tempList != null){
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    for (News news1 : tempList) {
//                                        returnList.add(news1);
//                                    }
//                                    System.out.println("finish");
//                                }
//                            });
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//        System.out.println("after thread");
//        return returnList;
//    }
}
