package com.example.k_sir.mimaxapplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class News implements Serializable{
    public String title, content, imgUrl, resource, channel;
    News(String title, String content, String imgUrl, String resource, String channel){
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.resource = resource;
        this.channel = channel;
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

    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("title", title);
            obj.put("content", content);
            obj.put("imgUrl", imgUrl);
            obj.put("resource", resource);
            obj.put("channel", channel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }
}
