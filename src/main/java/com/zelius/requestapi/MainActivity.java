package com.zelius.requestapi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zelius.requestapi.R;
import com.zelius.requestapi.adapters.TweetsListAdapter;
import com.zelius.requestapi.db.DbTweets;
import com.zelius.requestapi.model.TweetModel;
import com.bumptech.glide.Glide;

import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by RequestTwitterAPI on 15/03/2016.
 */
public class MainActivity extends AppCompatActivity {

    private final static String TWITTER_AUTH_CONSUMER_KEY = "consumer_key";
    private final static String TWITTER_AUTH_CONSUMER_SECRET = "consumer_secret";
    private final static String TWITTER_AUTH_ACCESS_TOKEN = "access_token";
    private final static String TWITTER_AUTH_ACCESS_TOKEN_SECRET = "access_token_secret";

    private final static String TWITTER_QUERY = "from:";
    private final static String TWITTER_QUERY2 = "SocialBaseBR";

    Twitter m_Twitter;
    ListView m_ListView;
    ImageView m_AccountIcon;
    TextView m_TxtTitle;

    Snackbar m_Snackbar;
    FloatingActionButton m_FAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_AccountIcon = (ImageView) findViewById(android.R.id.icon);
        m_ListView = (ListView) findViewById(android.R.id.list);
        m_TxtTitle = (TextView) findViewById(android.R.id.text1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        m_FAB = (FloatingActionButton) findViewById(R.id.fab);
        m_FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskWaitForSnackbar snackStore = new TaskWaitForSnackbar();
                snackStore.execute("Obtendo tweets de " + TWITTER_QUERY2 + "...");

                TaskRequestTwitterAPI taskRequest = new TaskRequestTwitterAPI();
                taskRequest.execute();
            }
        });

        PrepareTwitterAPI();
    }

    /* Setup ConfigurationBuilder to authenticate and get permission for queries and get API Instance.
     */
    private void PrepareTwitterAPI() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(TWITTER_AUTH_CONSUMER_KEY)
                .setOAuthConsumerSecret(TWITTER_AUTH_CONSUMER_SECRET)
                .setOAuthAccessToken(TWITTER_AUTH_ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(TWITTER_AUTH_ACCESS_TOKEN_SECRET);
        TwitterFactory tf = new TwitterFactory(cb.build());

        m_Twitter = tf.getInstance();
    }

    // region Store/Load Tweets from Database

    private void StoreTweetsOnDB(QueryResult result) {
        DbTweets dbTweets = new DbTweets(MainActivity.this);

        for (Status tweet : result.getTweets()) {
            dbTweets.insertData(tweet);
        }

        TaskWaitForSnackbar snackStore = new TaskWaitForSnackbar();
        snackStore.execute("Tweets inseridos no banco!");
    }

    private void LoadTweetsFromDB() {
        DbTweets dbTweets = new DbTweets(MainActivity.this);
        final List<TweetModel> tweets = dbTweets.selectData(null);

        // Glide here is used for asynchronous picture loading
        Glide.with(MainActivity.this)
                .load(tweets.get(0).getPicture())
                .asBitmap()
                .into(m_AccountIcon);

        m_TxtTitle.setText(tweets.get(0).getName());

        // I created a custom ListAdapter and a Model for Tweets, to keep some consistency.
        TweetsListAdapter adapter = new TweetsListAdapter(MainActivity.this, android.R.layout.simple_list_item_2, tweets);
        m_ListView.setAdapter(adapter);
        m_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TweetModel item = tweets.get(position);
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

                // Passing an object through PutExtra since TweetModel implements Serializable
                intent.putExtra(DetailsActivity.EXTRA_OBJ, item);

                startActivity(intent);
            }
        });
    }

    // endregion

    // region Async Requests to Twitter

    private class TaskRequestTwitterAPI extends AsyncTask<String, Void, QueryResult> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            m_ListView.setAdapter(null);
        }

        @Override
        protected QueryResult doInBackground(String... params) {
            try {
                Query query = new Query(TWITTER_QUERY + TWITTER_QUERY2);
                return m_Twitter.search(query);
            } catch (TwitterException e) {
                this.exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(QueryResult result) {
            if (this.exception != null) {
                System.out.println("ERRO: " + this.exception.getMessage());
                return;
            }

            StoreTweetsOnDB(result);
            LoadTweetsFromDB();
        }
    }

    private class TaskWaitForSnackbar extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                while(true){
                    if(m_Snackbar == null) break;
                    else if(!m_Snackbar.isShown()) break;
                }
                Thread.sleep(200);
            }catch (Exception e){}

            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            m_Snackbar = Snackbar.make(m_FAB, result, Snackbar.LENGTH_LONG).setAction("Action", null);
            m_Snackbar.show();
        }
    }

    // endregion

    // region Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getResources().getString(R.string.action_about));
            builder.setMessage(getResources().getString(R.string.about_message));
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // endregion
}


