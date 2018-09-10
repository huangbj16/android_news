package com.example.k_sir.mimaxapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SQLiteServer extends SQLiteOpenHelper {
    public static final String CREAT_BOOK = "create table news_table ("
            +"id integer primary key autoincrement,"
            +"title text,"
            +"description text,"
            +"imgUrl text,"
            +"link text,"
            +"hashValue integer unique,"
            +"visited integer,"
            +"channel text)";
    public static final String CREAT_MARK = "create table mark_table ("
            +"id integer primary key autoincrement,"
            +"title text,"
            +"description text,"
            +"imgUrl text,"
            +"link text,"
            +"hashValue integer unique,"
            +"channel text)";
    private Context mContext;
    private String table = "news_table";
    private String mark = "mark_table";

    private static SQLiteServer singleton = null;

    private SQLiteServer(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    public static SQLiteServer getInstance(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        if(singleton == null)
            singleton = new SQLiteServer(context, name, factory, version);
        return singleton;
    }

    public static SQLiteServer getInstance() {
        return singleton;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAT_BOOK);
        db.execSQL(CREAT_MARK);
//        System.out.println("databse created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists news_table");
        db.execSQL("drop table if exists mark_table");
        onCreate(db);
    }

    public boolean insertData(SQLiteDatabase database, String title, String description, String imgUrl, String link, boolean visited, String channel){
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("imgUrl", imgUrl);
        values.put("link", link);
        values.put("hashValue", (title+channel).hashCode());
        if(!visited)
            values.put("visited", 0);
        else
            values.put("visited", 1);
        values.put("channel", channel);
        long ret = database.insert(table, null, values);
        if (ret != -1)
            return true;
        else
            return false;
    }

    public boolean checkVisited(SQLiteDatabase database, String title){
        ArrayList<News> newsList = new ArrayList<>();
        String arg3 = "title = ?";
        String[] arg4 = new String[]{title};
        Cursor cursor = database.query(table, null, arg3, arg4, null, null, null);
        if(cursor.moveToFirst()){
            do{
//                System.out.println(cursor.getString(cursor.getColumnIndex("description")));
                if(cursor.getInt(cursor.getColumnIndex("visited")) == 1)//visited
                    return true;
            } while(cursor.moveToNext());
        }
        return false;
    }

    public boolean updateData(SQLiteDatabase database, String description, String title){
        ContentValues values = new ContentValues();
        values.put("description", description);
        values.put("visited", 1);
        String arg3 = "title = ?";
        String[] arg4 = new String[]{title};
        int ret = database.update(table, values, arg3, arg4);
        if(ret != 0)
            return true;
        else
            return false;
    }

    public ArrayList<News> queryData(SQLiteDatabase database, String channel){
//        System.out.println("query: " + channel);
        ArrayList<News> newsList = new ArrayList<>();
        String arg3 = "channel = ?";
        String[] arg4 = new String[]{channel};
        Cursor cursor = database.query(table, null, arg3, arg4, null, null, "id");
        if(cursor.moveToFirst()){
            do{
                boolean visited;
                if(cursor.getInt(cursor.getColumnIndex("visited")) == 1)
                    visited = true;
                else
                    visited = false;
                newsList.add(new News(cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("description")),
                        cursor.getString(cursor.getColumnIndex("imgUrl")),
                        cursor.getString(cursor.getColumnIndex("link")),
                        cursor.getString(cursor.getColumnIndex("channel")),
                        visited));
            } while(cursor.moveToNext());
        }
        cursor.close();
//        System.out.println("endquery");
        return newsList;
    }

    public boolean markNews(SQLiteDatabase database, String title, String imgUrl, String description, String link, String channel) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("imgUrl", imgUrl);
        values.put("link", link);
        values.put("hashValue", (title + channel).hashCode());
        values.put("channel", channel);
        long ret = database.insert(mark, null, values);
        if (ret != -1)
            return true;
        else
            return false;
    }

    public boolean checkMarked(SQLiteDatabase database, String title){
        ArrayList<News> newsList = new ArrayList<>();
        String arg3 = "title = ?";
        String[] arg4 = new String[]{title};
        Cursor cursor = database.query(mark, null, arg3, arg4, null, null, null);
        if(cursor.getCount() == 0)
            return false;
        else
            return true;
    }

    public ArrayList<News> queryMark(SQLiteDatabase database){
//        System.out.println("queryMark: ");
        ArrayList<News> newsList = new ArrayList<>();
        Cursor cursor = database.query(mark, null, null, null, null, null, "id");
        if(cursor.moveToFirst()){
            do{
                newsList.add(new News(cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("description")),
                        cursor.getString(cursor.getColumnIndex("imgUrl")),
                        cursor.getString(cursor.getColumnIndex("link")),
                        cursor.getString(cursor.getColumnIndex("channel")),
                        true));
            } while(cursor.moveToNext());
        }
        cursor.close();
        for (News news : newsList) {
//            System.out.println(news.channel + " " + news.visited + " " + news.title);
        }
//        System.out.println("endquery");
        return newsList;
    }

    public boolean deleteMark(SQLiteDatabase database, String title){
        if(database.delete(mark, "title = ?", new String[]{title}) != 0)
            return true;
        return false;
    }
}
