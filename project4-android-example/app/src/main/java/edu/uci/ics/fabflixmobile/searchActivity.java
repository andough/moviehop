package edu.uci.ics.fabflixmobile;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import java.util.*;
import android.widget.ListView;
import android.util.Log;

public class searchActivity extends ActionBarActivity{

    private static final String TAG = "ActionBarActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG,"onCreate Started.");

        ListView mListView = (ListView) findViewById(R.id.listView);

        Movie m1 = new Movie("1","m1","1001","d1","g1","s1");
        Movie m2 = new Movie("1","m2","1002","d1","g2","s2");
        Movie m3 = new Movie("1","m3","1003","d1","g3","s3");
        Movie m4 = new Movie("1","m4","1004","d1","g4","s4");
        Movie m5 = new Movie("1","m5","1005","d1","g5","s5");
        Movie m6 = new Movie("1","m6","1006","d1","g6","s6");
        Movie m7 = new Movie("1","m7","1007","d1","g7","s7");

        ArrayList<Movie> movieList = new ArrayList<Movie>();
        movieList.add(m1);
        movieList.add(m2);
        movieList.add(m3);
        movieList.add(m4);
        movieList.add(m5);
        movieList.add(m6);
        movieList.add(m7);

        MovieListAdapter adapter = new MovieListAdapter(this,R.layout.adapter_view_layout,movieList);
        mListView.setAdapter(adapter);
    }
}
