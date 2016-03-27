package com.example.SnapNews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FeedListAdapter extends ArrayAdapter<HashMap> {

    private List articles;

    public FeedListAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        articles = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(com.example.SnapNews.R.layout.feed_item, parent, false);
        }

        Object article = articles.get(position);

        if (article instanceof HashMap) {
            HashMap<String, Object> m = (HashMap) article;
            TextView titleText = (TextView) convertView.findViewById(com.example.SnapNews.R.id.titleText);
            titleText.setText((String) (m.get("title"))+(m.get("seen")).toString()); //debug statement
            //titleText.setText((String) (m.get("title")));
            TextView summaryText = (TextView) convertView.findViewById(com.example.SnapNews.R.id.summaryText);
            summaryText.setText((String) (m.get("summary")));
            TextView timeText = (TextView) convertView.findViewById(com.example.SnapNews.R.id.timeText);
            Date resultDate = new Date((long) (m.get("postTime")));
            String stringDate = resultDate.toString();
            String[] splitDate = stringDate.split(" ");
            stringDate = splitDate[0] + " " + splitDate[1] + " " + splitDate[2] + ", " + splitDate[3] + " ";
            timeText.setText(stringDate);
        }


        return convertView;
    }
}
