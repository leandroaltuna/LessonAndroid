package com.example.fifthlesson.app;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fifthlesson.app.db.DBOperations;
import com.example.fifthlesson.app.list.TweetAdapter;
import com.example.fifthlesson.app.models.Tweet;
import com.example.fifthlesson.app.utils.ConstantsUtils;
import com.example.fifthlesson.app.utils.TwitterUtils;


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

            return TwitterUtils.getTimelineForSearchTerm(ConstantsUtils.MEJORANDROID_TERM);
//            DBOperations dbOperations = new DBOperations(TimelineActivity.this);
//            return dbOperations.getStatusUpdates();

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
