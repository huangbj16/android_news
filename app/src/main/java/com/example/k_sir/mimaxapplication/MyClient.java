package com.example.k_sir.mimaxapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.*;

public class MyClient {
    public JSONArray search(String keyword){
        try {
            String params = URLEncoder.encode("option=search&keyword="+keyword, "utf8");
            URL url = new URL("http://59.66.130.33:8888/connect?"+params);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf8"));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }
            reader.close();
            JSONArray array = new JSONArray(stringBuilder.toString());
            return array;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray refresh(String channel){
        try {
            String params = URLEncoder.encode("option=refresh&keyword=" + channel, "utf8");
            URL url = new URL("http://59.66.130.33:8888/connect?" + params);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf8"));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            JSONArray array = new JSONArray(stringBuilder.toString());
            return array;
        }catch (java.net.ConnectException e){
            System.out.println("connectionFailed");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
