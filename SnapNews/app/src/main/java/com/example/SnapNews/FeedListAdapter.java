package com.example.SnapNews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FeedListAdapter extends ArrayAdapter<HashMap> {

    private List articles;

    public FeedListAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        articles = objects;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
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
            //titleText.setText((String) (m.get("title"))+(m.get("seen")).toString()); //debug statement
            titleText.setText((String) (m.get("title")));
            TextView summaryText = (TextView) convertView.findViewById(com.example.SnapNews.R.id.summaryText);
            summaryText.setText((String) (m.get("summary")));
            TextView timeText = (TextView) convertView.findViewById(com.example.SnapNews.R.id.timeText);
            Date resultDate = new Date((long) (m.get("postTime")));
            String stringDate = resultDate.toString();
            String[] splitDate = stringDate.split(" ");
            stringDate = "Shared: " + splitDate[0] + " " + splitDate[1] + " " + splitDate[2] + ", " + splitDate[3] + " ";
            timeText.setText(stringDate);


            String url = (String) m.get("pic");
            ImageView iv = (ImageView) convertView.findViewById(R.id.image);

            URL realURL = null;
            try {
                realURL = new URL(url);
                Bitmap imageBitMap = getRemoteImage(realURL);
                iv.setImageBitmap(imageBitMap);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }


        return convertView;
    }

    public static Bitmap getRemoteImage(final URL aURL) {
        try {
            final URLConnection conn = aURL.openConnection();
            conn.connect();
            final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            final Bitmap bm = BitmapFactory.decodeStream(bis);
            bis.close();
            return bm;
        } catch (IOException e) {}
        return null;
    }
}
