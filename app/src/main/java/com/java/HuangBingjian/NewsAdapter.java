package com.java.HuangBingjian;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.java.HuangBingjian.mimaxapplication.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> implements View.OnClickListener{
    private Context mContext;
    private List<News> newsList;
    public ViewHolder viewHolder;
    @Override
    public void onClick(View v) {
        if(onItemClickListener != null){
            onItemClickListener.onItemClick(v, (int)v.getTag());
        }
    }

    public static interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    private OnItemClickListener onItemClickListener = null;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView newsImage;
        TextView newsTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            newsImage = (ImageView) itemView.findViewById(R.id.news_image);
            newsTitle = (TextView) itemView.findViewById(R.id.news_title);
        }
    }

    public NewsAdapter(List<News> newsList){
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(mContext == null){
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_item, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder viewHolder, int i) {
//        System.out.println("onBindViewHolder");
        News news = newsList.get(i);
        viewHolder.newsTitle.setText(news.title);
        viewHolder.itemView.setTag(i);
        Glide.with(mContext).load(news.imgUrl).into(viewHolder.newsImage);
        if(news.visited)
            viewHolder.newsTitle.setTextColor(Color.rgb(127, 127, 127));
        else
            viewHolder.newsTitle.setTextColor(Color.rgb(0,0,0));
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;//connect two listener
    }
}
