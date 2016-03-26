package com.example.firebasetester;

import android.content.Context;
import android.support.annotation.StringDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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
                    inflate(R.layout.feed_item, parent, false);
        }

        Object article = articles.get(position);

        if (article instanceof HashMap) {
            HashMap<String, Object> m = (HashMap) article;
            TextView titleText = (TextView) convertView.findViewById(R.id.titleText);
            titleText.setText((String) (m.get("title")));
            TextView summaryText = (TextView) convertView.findViewById(R.id.summaryText);
            summaryText.setText((String) (m.get("summary")));
            TextView timeText = (TextView) convertView.findViewById(R.id.timeText);
            Date resultDate = new Date((long) (m.get("postTime")));
            String stringDate = resultDate.toString();
            String[] splitDate = stringDate.split(" ");
            stringDate = splitDate[0] + " " + splitDate[1] + " " + splitDate[2] + ", " + splitDate[3] + " ";
            timeText.setText(stringDate);
        }


        return convertView;
    }
}
