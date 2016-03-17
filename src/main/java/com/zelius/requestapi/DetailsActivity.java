package com.zelius.requestapi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.zelius.requestapi.R;
import com.zelius.requestapi.model.TweetModel;
import com.bumptech.glide.Glide;

import java.util.Date;

/**
 * Created by RequestTwitterAPI on 15/03/2016.
 */
public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_OBJ = "ExtraObj";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TweetModel tweet = (TweetModel) getIntent().getSerializableExtra(EXTRA_OBJ);
        LoadData(tweet);
    }

    private void LoadData(TweetModel tweet){
        ImageView imgIcon = (ImageView) findViewById(android.R.id.icon);
        TextView txtUsuario = (TextView) findViewById(R.id.txtUsuario);
        TextView txtTweet = (TextView) findViewById(R.id.txtTweet);
        TextView txtRetweets = (TextView) findViewById(R.id.txtRetweets);
        TextView txtFavs = (TextView) findViewById(R.id.txtFavs);

        Glide.with(DetailsActivity.this)
                .load(tweet.getPicture())
                .asBitmap()
                .into(imgIcon);

        txtUsuario.setText(Html.fromHtml(String.format("<b>%s</b> @%s - %sd",
                tweet.getName(),
                tweet.getUsername(),
                ReturnDateInDays(tweet.getDateCreation()))));

        txtTweet.setText(tweet.getTweet());
        txtRetweets.setText("Retweets: " + tweet.getRetweets());
        txtFavs.setText("Favorites: " + tweet.getFavorites());

    }

    public static long ReturnDateInDays(long date){
        return (new Date().getTime() - date)  / (1000 * 60 * 60 * 24);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
