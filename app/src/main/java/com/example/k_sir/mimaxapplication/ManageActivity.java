package com.example.k_sir.mimaxapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class ManageActivity extends AppCompatActivity {
    RecyclerView chosenView, notChosenView;
    GridLayoutManager chosenManager, notChosenManager;
    ChannelAdapter chosenAdapter, notChosenAdapter;
    ArrayList<String> chosenList = new ArrayList<>();
    ArrayList<String> notChosenList = new ArrayList<>();
    private String[] allChannels = new String[]{"国内新闻", "国际新闻", "经济新闻", "金融频道", "体育新闻", "教育新闻", "游戏新闻", "娱乐频道", "科技频道", "台湾新闻", "英文频道"};
    Intent intent;
    Button returnButton;
    Bundle bundle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
//        assign channels
        intent = getIntent();
        bundle = intent.getExtras();
        chosenList = bundle.getStringArrayList("chosenList");
        for (int i = 0; i < allChannels.length; i++) {
            if(!chosenList.contains(allChannels[i]))
                notChosenList.add(allChannels[i]);
        }
        System.out.println(chosenList);
        System.out.println(notChosenList);

        chosenView = (RecyclerView) findViewById(R.id.chosen_recycle);
        notChosenView = (RecyclerView) findViewById(R.id.not_chosen_recycle);
        chosenManager = new GridLayoutManager(this, 4);
        notChosenManager = new GridLayoutManager(this, 4);
        chosenView.setLayoutManager(chosenManager);
        notChosenView.setLayoutManager(notChosenManager);
        chosenAdapter = new ChannelAdapter(chosenList);
        notChosenAdapter = new ChannelAdapter(notChosenList);
        chosenView.setAdapter(chosenAdapter);
        notChosenView.setAdapter(notChosenAdapter);

        chosenAdapter.setOnItemClickListener(new ChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String channel = chosenList.get(position);
//                Toast.makeText(ManageActivity.this, channel, Toast.LENGTH_SHORT).show();
                chosenList.remove(position);
                notChosenList.add(channel);
                chosenAdapter.notifyDataSetChanged();
                notChosenAdapter.notifyDataSetChanged();
                //change to the other list
            }
        });

        notChosenAdapter.setOnItemClickListener(new ChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String channel = notChosenList.get(position);
//                Toast.makeText(ManageActivity.this, channel, Toast.LENGTH_SHORT).show();
                //change to the other list
                notChosenList.remove(position);
                chosenList.add(channel);
                chosenAdapter.notifyDataSetChanged();
                notChosenAdapter.notifyDataSetChanged();
            }
        });

        returnButton = findViewById(R.id.confirm_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chosenList.size() > 0) {
                    bundle.putStringArrayList("returnList", chosenList);
                    intent.putExtras(bundle);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                else {
                    Toast.makeText(ManageActivity.this, "At least one channel is required", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        layoutManager = new GridLayoutManager(this, 1);
//        recyclerView.setLayoutManager(layoutManager);
//        newsAdapter = new NewsAdapter(newsList);
//        recyclerView.setAdapter(newsAdapter);
//
//        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                News singleNews = newsList.get(position);
////                Toast.makeText(MainActivity.this, singleNews.content, Toast.LENGTH_SHORT).show();
//                /////////////////open another activity for single news
//                Intent intent = new Intent(MainActivity.this, NewsPageActivity.class);
//                intent.putExtra("content", singleNews.convertToString());
//                startActivity(intent);
//            }
//        });
    }
}
