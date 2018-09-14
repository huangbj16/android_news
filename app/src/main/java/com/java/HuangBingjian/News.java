package com.java.HuangBingjian;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class News{
    public String title, content, imgUrl, resource, channel;
    boolean visited;
    News(String title, String content, String imgUrl, String resource, String channel, boolean visited){
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.resource = resource;
        this.channel = channel;
        this.visited = visited;
    }
    public String convertToString(){
        JSONObject obj = null;
        try {
            obj = new JSONObject();
            obj.put("title", title);
            obj.put("content", content);
            obj.put("imgUrl", imgUrl);
            obj.put("resource", resource);
            obj.put("channel", channel);
            return obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
