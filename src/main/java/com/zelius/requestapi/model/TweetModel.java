package com.zelius.requestapi.model;

import java.io.Serializable;

/**
 * Created by RequestTwitterAPI on 16/03/2016.
 */
public class TweetModel implements Serializable {

    private String id;
    private String name;
    private String username;
    private String picture;
    private String tweet;
    private int retweets;
    private int favorites;
    private long dateCreation;

    public TweetModel(String id, String name, String username, String picture, String tweet, int retweets, int favorites, long dateCreation) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.picture = picture;
        this.tweet = tweet;
        this.retweets = retweets;
        this.favorites = favorites;
        this.dateCreation = dateCreation;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPicture() {
        return picture;
    }

    public String getTweet() {
        return tweet;
    }

    public int getRetweets() {
        return retweets;
    }

    public int getFavorites() {
        return favorites;
    }

    public long getDateCreation() {
        return dateCreation;
    }
}
