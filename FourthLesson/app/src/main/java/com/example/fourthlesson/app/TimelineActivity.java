package com.example.fourthlesson.app;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fourthlesson.app.list.TweetAdapter;
import com.example.fourthlesson.app.models.Tweet;
import com.example.fourthlesson.app.utils.ConstantsUtils;
import com.example.fourthlesson.app.utils.TwitterUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class TimelineActivity extends ActionBarActivity {

    ListView lvTimeline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        lvTimeline = (ListView) findViewById(R.id.lv_timeline);
        new GetTimelineTask().execute();
    }

    private void updateListView(ArrayList<Tweet> tweets)
    {
        lvTimeline.setAdapter(new TweetAdapter(this, R.layout.row_tweet, tweets));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class GetTimelineTask extends AsyncTask<Object, Void, ArrayList<Tweet>>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(TimelineActivity.this);
            progressDialog.setTitle(getResources().getString(R.string.label_tweet_search_loader));
            progressDialog.show();
        }

        @Override
        protected ArrayList<Tweet> doInBackground(Object... objects) {

            ArrayList<Tweet> tweets = new ArrayList<Tweet>();

            try
            {
                String timeline = TwitterUtils.getTimelineForSearchTerm(ConstantsUtils.MEJORANDROID_TERM);

                JSONObject jsonResponse = new JSONObject(timeline);
                JSONArray jsonArray = jsonResponse.getJSONArray("statuses");
                JSONObject tweetJsonObject;

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    tweetJsonObject = (JSONObject) jsonArray.get(i);

                    Tweet tweet = new Tweet();

                    tweet.setName(tweetJsonObject.getJSONObject("user").getString("name"));
                    tweet.setScreenName(tweetJsonObject.getJSONObject("user").getString("screen_name"));
                    tweet.setProfileImageUrl(tweetJsonObject.getJSONObject("user").getString("profile_image_url"));
                    tweet.setText(tweetJsonObject.getString("text"));
                    tweet.setCreatedAt(tweetJsonObject.getString("created_at"));

                    tweets.add(i, tweet);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return tweets;
        }

        @Override
        protected void onPostExecute(ArrayList<Tweet> timeline) {
            super.onPostExecute(timeline);

            progressDialog.dismiss();

            if ( timeline.isEmpty() )
            {
                Toast.makeText(TimelineActivity.this, getResources().getString(R.string.label_tweets_not_found), Toast.LENGTH_SHORT).show();
            }
            else
            {
                updateListView(timeline);
                Toast.makeText(TimelineActivity.this, getResources().getString(R.string.label_tweets_downloaded), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
