package com.example.SnapNews;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deb Banerji on 25-Mar-16.
 */
public class Article {

    private String URL;
    private String title;
    private String summary;
    private int voteCount;
    private long postTime;

    private RequestQueue queue;


    public Article(String URL, String summary, String title) {
        this.URL = URL;
        voteCount = 0;
        postTime = System.currentTimeMillis();
        this.summary = summary;
        this.title = title;
    }


    public long getPostTime() {
        return postTime;
    }

    public String getTitle() {
        return title;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public String getURL() {
        return URL;
    }

    public String getSummary() {
        return summary;
    }

    public String generateSummary(String url) {
//        return "Summary of: " + URL;
        //return ((Long) postTime).toString();
        String parsedURL;
        parsedURL = url.replace(":", "%3A");
        parsedURL = parsedURL.replace("/", "%2F");
        String domain = "https://joanfihu-article-analysis-v1.p.mashape.com/link?entity_description=False&link=";
        parsedURL = domain + parsedURL;
        //return getJsonResponse(parsedURL);
        //return getJsonResponse("https://joanfihu-article-analysis-v1.p.mashape.com/link?entity_description=False&link=http%3A%2F%2Fwww.theverge.com%2F2014%2F11%2F26%2F7292895%2Fbest-black-friday-deals)");
        //return "";
        return getJsonResponse("https://joanfihu-article-analysis-v1.p.mashape.com/link?entity_description=False&link=https%3A%2F%2Fgoogle.com");
    }

    public String getJsonResponse(String url) {
        final String[] jsonResponse = {"test"};

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            jsonResponse[0] = "Response: " + response.getString("summary");
                        } catch (JSONException e) {
                            jsonResponse[0] = "That didn't work!";
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                jsonResponse[0] = "That didn't work!";
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("X-Mashape-Key", "Ss78zNCa4BmshysykBTh7J5hMpWip1dzAcbjsnJoq8p3EEmZkO");
                params.put("Accept", "application/json");
                return params;
            }
        };

        queue.add(request);
        return jsonResponse[0];
    }


    @Override
    public String toString() {
        return URL;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Article)) {
            return false;
        }

        return ((Article) other).URL.equals(this.URL);
    }

    @Override
    public int hashCode() {
        return URL.hashCode();
    }
}
