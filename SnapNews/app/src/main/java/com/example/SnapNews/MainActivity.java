package com.example.SnapNews;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.StackView;
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
                        if (lhs instanceof HashMap && rhs instanceof HashMap) {
                            boolean leftSeen = (Boolean) ((HashMap) lhs).get("seen");
                            boolean rightSeen = (Boolean) ((HashMap) rhs).get("seen");
                            if (leftSeen && !rightSeen) {
                                return 1;
                            }//checking if seen first
                            if (((Long) ((HashMap) lhs).get("postTime") - (Long) ((HashMap) rhs).get("postTime")) < 0) {
                                return 1;
                            }//checking post time next
                        }
                        return -1;
                    }
                });
                List<Object> singleList = new ArrayList<Object>();
                if (feedList[0].size() > 0) {
                    singleList.add(feedList[0].get(0));
                }
                feedAdapter[0] = new FeedListAdapter(
                        getBaseContext(), com.example.SnapNews.R.layout.feed_item, singleList);
                feedListView.setAdapter(feedAdapter[0]);
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });


        ImageButton dismissButton = (ImageButton) findViewById(com.example.SnapNews.R.id.dismissButton);
        assert dismissButton != null;

        dismissButton.setOnClickListener(new View.OnClickListener() {//setting seen value of first to true and updating database
            @Override
            public void onClick(View v) {

                if (feedList[0].size() > 0) {
                    final HashMap oldArticle = (HashMap) (feedList[0]).get(0);
                    Article updatedArticle = new Article((String) oldArticle.get("url"),
                            (String) oldArticle.get("summary"),
                            (String) oldArticle.get("title"), (String) oldArticle.get("pic"));
                    updatedArticle.see();
                    Iterator it = feed.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        if (((HashMap) pair.getValue()).containsValue(oldArticle.get("url"))) {
                            it.remove();
                        }
                    }
                    feed.put(((Long) updatedArticle.getPostTime()).toString(), updatedArticle);
                    articleBase.removeValue();
                    articleBase.child("ArticleList").setValue(feed);
                }
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
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                Snackbar.make(v, "Generating Summary...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                String parsedURL;
                parsedURL = URL.replace(":", "%3A");
                parsedURL = parsedURL.replace("/", "%2F");
                String domain = "https://joanfihu-article-analysis-v1.p.mashape.com/link?entity_description=False&link=";
                parsedURL = domain + parsedURL;

                System.out.println("BEFORE REQUEST");
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, parsedURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    summary[0] = response.getString("summary");

                                    summary[0] = summary[0].replace("[\"", "");
                                    summary[0] = summary[0].replace("\"]", "");
                                    summary[0] = summary[0].replace("\",\"", " ");
                                    summary[0] = summary[0].replace("\",\"", "");
                                    summary[0] = summary[0].replace("\\n", "");
                                    summary[0] = summary[0].replace("\\\"", "\"");
                                    //cleaning up summary
                                    if (!URL.equals("")) {
                                        System.out.println("we made it");
                                        addArticle[0] = new Article(URL, summary[0], (String) response.get("title"), (String) response.get("image"));

                                        Iterator it = feed.entrySet().iterator();
                                        while (it.hasNext()) {
                                            Map.Entry pair = (Map.Entry) it.next();
//                                            if (((HashMap) pair.getValue()).containsValue(addArticle[0])) {
                                            if (((HashMap) pair.getValue()).containsValue(addArticle[0].getURL())) {

                                                it.remove();
                                            }
                                        }
                                        feed.put(((Long) addArticle[0].getPostTime()).toString(), addArticle[0]);
                                        articleBase.removeValue();
                                        articleBase.child("ArticleList").setValue(feed);
                                    }
                                    ;

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
                        }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        System.out.println("ADDING HEADERS");
                        Map<String, String> params = new HashMap<>();
                        params.put("X-Mashape-Key", "F08kcpH96qmshzyQFMuNcnHUaatjp166OAPjsnzWSuaaWbvWAo");
                        params.put("Accept", "application/json");
                        return params;
                    }
                };
                queue.add(request);
            }
        });
    }
}
