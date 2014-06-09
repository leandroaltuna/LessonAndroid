package com.example.fifthlesson.app.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fifthlesson.app.R;
import com.example.fifthlesson.app.models.Tweet;
import com.example.fifthlesson.app.utils.BitmapManager;
import com.example.fifthlesson.app.utils.DateUtils;

import java.util.ArrayList;

public class TweetAdapter extends ArrayAdapter<Tweet> {

    private Context context;
    private ArrayList<Tweet> tweets;

    public TweetAdapter(Context context, int viewResourceId, ArrayList<Tweet> tweets)
    {
        super(context, viewResourceId, tweets);

        this.context = context;
        this.tweets = tweets;
    }

    static class ViewHolder
    {
        public ImageView avatar;
        public TextView name;
        public TextView screenName;
        public TextView text;
        public TextView createdAt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if ( convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_tweet, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.screenName = (TextView) convertView.findViewById(R.id.screen_name);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.createdAt = (TextView) convertView.findViewById(R.id.created_at);

            convertView.setTag(viewHolder);
        }

        ViewHolder onlyHolder = (ViewHolder) convertView.getTag();

        BitmapManager.getInstance().loadBitmap(tweets.get(position).getProfileImageUrl(), onlyHolder.avatar);
        onlyHolder.name.setText(tweets.get(position).getName());
        onlyHolder.screenName.setText("@" + tweets.get(position).getScreenName());
        onlyHolder.text.setText(tweets.get(position).getText());
        onlyHolder.createdAt.setText(DateUtils.setDateFormat(tweets.get(position).getCreatedAt()));

//        return super.getView(position, convertView, parent);
        return convertView;
    }

}
