package com.example.SnapNews;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.example.SnapNews.R;

import java.util.ArrayList;
import java.util.List;

public class Comments extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List comments = new ArrayList();

        comments.add(new Comment("ThatGuyYouMetThatOneTime151", "Omg This article is amazing"));
        comments.add(new Comment("ThatOtherGuyYouMetThatOtherTime721", "And so is this app!"));
        comments.add(new Comment("SomeRandomPerson251", "Ikr, it's totally rad!! It's not weird because \"totally rad\" seems like a perfectly plausible thing for real people to say! "));
        comments.add(new Comment("AnotherRandomPerson386", "I know one of the developers :)"));
        comments.add(new Comment("YetAnotherRandomPerson493", "REALLLYYYY?!?!?!!?!!"));
        comments.add(new Comment("SomeRandomPerson251", "Which one?"));
        comments.add(new Comment("SomebodyElse649", "Is it the devilishly handsome one?"));

        CommentListAdapter commentListAdapter = new CommentListAdapter(
                this, com.example.SnapNews.R.layout.feed_item, comments);
        final ListView feedListView = (ListView) findViewById(R.id.commentListView);
        feedListView.setAdapter(commentListAdapter);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
