package com.zelius.requestapi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.zelius.requestapi.model.TweetModel;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;

/**
 * Created by RequestTwitterAPI on 16/03/2016.
 */
public class DbTweets {

    public static abstract class TweetsEntry implements BaseColumns {
        public static final String TABLE_NAME = "tweets";
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String USERNAME = "username";
        public static final String PICTURE = "picture";
        public static final String TWEET = "tweet";
        public static final String RETWEETS = "retweets";
        public static final String FAVORITES = "favorites";
        public static final String DATE_CREATION = "date_creation";
    }

    private final static String TABLE_FIELDS = String.format(
            "%s INTEGER PRIMARY KEY, %s VARCHAR(64), %s VARCHAR(64), %s VARCHAR(1024), %s VARCHAR(144), %s INTEGER, %s INTEGER, %s INTEGER",
            TweetsEntry.ID, TweetsEntry.NAME, TweetsEntry.USERNAME, TweetsEntry.PICTURE, TweetsEntry.TWEET, TweetsEntry.RETWEETS, TweetsEntry.FAVORITES, TweetsEntry.DATE_CREATION
    );

    private Context m_Context;
    private DbHelper m_DbHelper;

    public DbTweets(Context context) {
        this.m_Context = context;
        this.m_DbHelper = new DbHelper(m_Context, TweetsEntry.TABLE_NAME, TABLE_FIELDS);
    }
    public long insertData(Status data) {
        try {
            SQLiteDatabase db = m_DbHelper.getWritableDatabase();

            Cursor cursor = db.query(TweetsEntry.TABLE_NAME, null, TweetsEntry.ID + "=" + data.getId(), null, null, null, null);

            ContentValues contentValues = new ContentValues();
            contentValues.put(TweetsEntry.NAME, data.getUser().getScreenName());
            contentValues.put(TweetsEntry.USERNAME, data.getUser().getName());
            contentValues.put(TweetsEntry.PICTURE, data.getUser().getProfileImageURL());
            contentValues.put(TweetsEntry.TWEET, data.getText());
            contentValues.put(TweetsEntry.RETWEETS, data.getRetweetCount());
            contentValues.put(TweetsEntry.FAVORITES, data.getFavoriteCount());
            contentValues.put(TweetsEntry.DATE_CREATION, data.getCreatedAt().getTime());

            // It will check if the tweet was already stored, if so, update it, or else, insert new one.

            if(cursor.moveToNext()){
                return db.update(TweetsEntry.TABLE_NAME, contentValues, TweetsEntry.ID + "=" + data.getId(), null);
            }else{
                contentValues.put(TweetsEntry.ID, data.getId());
                return db.insert(TweetsEntry.TABLE_NAME, null, contentValues);
            }
        } catch (Exception e) {
            Log.i("INSERT RESULT: ", e.getMessage());
        }
        return -1;
    }

    public List<TweetModel> selectData(String[] columns) {
        List<TweetModel> result = new ArrayList<>();
        try {
            SQLiteDatabase db = m_DbHelper.getReadableDatabase();

            Cursor cursor = db.query(TweetsEntry.TABLE_NAME, columns, null, null, null, null, TweetsEntry.DATE_CREATION + " DESC");

            while (cursor.moveToNext()) {
                TweetModel tweet = new TweetModel(
                        cursor.getString(0), // ID
                        cursor.getString(1), // NAME
                        cursor.getString(2), // USERNAME
                        cursor.getString(3), // PICTURE
                        cursor.getString(4), // TWEET
                        cursor.getInt(5), // RETWEETS
                        cursor.getInt(6), // FAVORITES
                        cursor.getLong(7) // DATE_CREATION
                );

                result.add(tweet);
            }
        } catch (Exception e) {
            Log.i("SELECT RESULT: ", e.getMessage());
        }
        return result;
    }
}
