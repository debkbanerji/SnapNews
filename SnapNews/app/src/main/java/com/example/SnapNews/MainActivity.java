package com.example.SnapNews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(com.example.SnapNews.R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(com.example.SnapNews.R.id.toolbar);
        setSupportActionBar(toolbar);


        final EditText enteredURL = (EditText) findViewById(com.example.SnapNews.R.id.enterURL);

        enteredURL.setMovementMethod(new ScrollingMovementMethod());

        final Firebase articleBase = new Firebase("https://brilliant-fire-8390.firebaseio.com/");
        final Map<String, Object> feed = new TreeMap<String, Object>();

        final List[] feedList = new List[1];
        feedList[0] = new ArrayList<Object>(feed.values());

        final FeedListAdapter[] feedAdapter = new FeedListAdapter[1];
        feedAdapter[0] = new FeedListAdapter(
                this, com.example.SnapNews.R.layout.feed_item, feedList[0]);

//        final ListView feedListView = (ListView) findViewById(com.example.SnapNews.R.id.feedListView);
        final ListView feedListView = (ListView) findViewById(com.example.SnapNews.R.id.feedListView);
        feedListView.setAdapter(feedAdapter[0]);

        articleBase.child("ArticleList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, HashMap> newFeed = (Map<String, HashMap>) snapshot.getValue();
                if (newFeed != null) {
                    for (Map.Entry<String, HashMap> entry : newFeed.entrySet()) {
                        feed.put(entry.getKey(), entry.getValue());
                    }
                } //Code for populating Feed
                feedList[0] = new ArrayList<Object>(feed.values());
                //Collections.reverse(feedList[0]);
                Collections.sort(feedList[0], new Comparator() {
                    @Override
                    public int compare(Object lhs, Object rhs) {
                        if (lhs instanceof Article && rhs instanceof Article) {
                            if ((((Article) lhs).getPostTime() - ((Article) rhs).getPostTime()) < 0) {
                                return 1;
                            }
                        }
                        return -1;
                    }
                });
                feedAdapter[0] = new FeedListAdapter(
                        getBaseContext(), com.example.SnapNews.R.layout.feed_item, feedList[0]);
                feedListView.setAdapter(feedAdapter[0]);
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });


        ImageButton shareButton = (ImageButton) findViewById(com.example.SnapNews.R.id.shareButton);
        assert shareButton != null;

        final RequestQueue queue = Volley.newRequestQueue(this);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Article[] addArticle = new Article[1];

                final String URL = enteredURL.getText().toString();
                final String[] summary = {""};
                enteredURL.setText("");




                String parsedURL;
                parsedURL = URL.replace(":","%3A");
                parsedURL = parsedURL.replace("/","%2F");
                String domain = "https://joanfihu-article-analysis-v1.p.mashape.com/link?entity_description=False&link=";
                parsedURL = domain + parsedURL;

                System.out.println("BEFORE REQUEST");
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, parsedURL, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        summary[0] = "\n" + response.getString("summary");

                                        summary[0] = summary[0].replace("[\"", "");
                                        summary[0] = summary[0].replace("\"]", "");
                                        summary[0] = summary[0].replace("\",\"", " ");
                                        summary[0] = summary[0].replace("\",\"", "");
                                        summary[0] = summary[0].replace("\\n", "");
                                        summary[0] = summary[0].replace("\\\"", "\"");
                                        //cleaning up summary
                                        if (!URL.equals("")) {
                                            System.out.println("we made it");
                                            addArticle[0] = new Article(URL, summary[0], (String) response.get("title"));

                                            Iterator it = feed.entrySet().iterator();
                                            while (it.hasNext()) {
                                                Map.Entry pair = (Map.Entry) it.next();
                                                if (((HashMap) pair.getValue()).containsValue(addArticle[0])) {
                                                    it.remove();
                                                }
                                            }
                                            feed.put(((Long) addArticle[0].getPostTime()).toString(), addArticle[0]);
                                            articleBase.removeValue();
                                            articleBase.child("ArticleList").setValue(feed);
                                        };

                                        System.out.println("LEHHGOOO " + summary[0]);
                                    } catch (JSONException e) {
                                        //System.out.println("WHUT");
                                        summary[0] = "That didn't work!";
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    summary[0] = "That didn't work!";
                                    volleyError.printStackTrace();
                                }
                            })
                {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        System.out.println("ADDING HEADERS");
                        Map<String, String>  params = new HashMap<>();
                        params.put("X-Mashape-Key", "RrzHM7Pua0mshdnzHG0MVXQlTenEp1fTeqzjsntEdavMWDQirq");
                        params.put("Accept", "application/json");
                        return params;
                    }
                };

                queue.add(request);




            }


            /**
            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_settings) {
                    return true;
                }
                return super.onOptionsItemSelected(item);
            }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.menu_main, menu);
                return true;
            }**/


        });
    }
}
