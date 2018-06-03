package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import java.util.*;
import android.widget.ListView;
import android.util.Log;
import android.widget.TextView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.*;
import android.widget.AdapterView;
import android.widget.Toast;
import java.net.URLEncoder;

import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;



public class BlueActivity extends ActionBarActivity {

    private int pageNum;
    Pagination p;
    ArrayList<Movie> movieList ;
    ListView mListView;
    MovieListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue);
        pageNum = 0;
        Log.d("blueActivity","blue onCreate Started.");
        Bundle bundle = getIntent().getExtras();
        //Toast.makeText(this, "Last activity was " + bundle.get("last_activity") + ".", Toast.LENGTH_LONG).show();

        //String msg = bundle.getString("message");
        //if (msg != null && !"".equals(msg)) {
        //    ((TextView) findViewById(R.id.last_page_msg_container)).setText(msg);
        //}

    }


    public void goToRed(View view) {
        //String msg = ((EditText) findViewById(R.id.blue_2_red_message)).getText().toString();

        Intent goToIntent = new Intent(this, RedActivity.class);

       // goToIntent.putExtra("last_activity", "blue");
       // goToIntent.putExtra("message", msg);

        startActivity(goToIntent);
    }

    public void goToGreen(String movieId) {
        String msg = movieId;

        Intent goToIntent = new Intent(this, GreenActivity.class);

        goToIntent.putExtra("movieId", msg);
       // goToIntent.putExtra("message", msg);

        startActivity(goToIntent);
    }


    public void doSearch(View view){

        String whereclause = ((EditText) findViewById(R.id.whereclause)).getText().toString();
        if (whereclause != null) {
            Log.d("whereclause value: ", whereclause);
            p = new Pagination();
            //connectToTomcat(whereclause,movieList);
            //ArrayList<Movie> movieList = new ArrayList<Movie>();
            movieList = new ArrayList<Movie>();
            final Map<String, String> params = new HashMap<String, String>();
            StringTokenizer st = new StringTokenizer(whereclause);
            String encoded = "";
            while (st.hasMoreTokens())
                encoded = encoded.concat("+" + st.nextToken() + "* ");
            encoded.replace("+","%2B");
            encoded.replace(" ","%20");
            params.put("whereclause", encoded);

            final RequestQueue queue = NetworkManager.sharedManager(this).queue;

            final StringRequest queryRequest = new StringRequest(Request.Method.POST, "https://ec2-54-191-52-23.us-west-2.compute.amazonaws.com:8443/project2/api/fulltext",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONArray jsonArr = new JSONArray(response);
                                fillMovieList(jsonArr, movieList);
                                pageNum = 0;
                                if (p.pageCount() == -1)
                                    ((TextView) findViewById(R.id.pageId)).setText("page: " + pageNum + "/0");
                                else
                                    ((TextView) findViewById(R.id.pageId)).setText("page: " + pageNum + "/" + p.pageCount());
                            }
                            catch(Exception e) {Log.d("json error", e.toString());}
                            // Add the request to the RequestQueue.
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("security.error", error.toString());
                            //((TextView) findViewById(R.id.http_response)).setText("err response");
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() { Log.d("params: ", params.toString());return params; }  // HTTP POST Form Data
            };

            queue.add(queryRequest);
            mListView = (ListView) findViewById(R.id.lView);
            adapter = new MovieListAdapter(this, R.layout.adapter_view_layout,movieList);
            mListView.setAdapter(adapter);

            //p = new Pagination(movieList);
            //Intent goToIntent = new Intent(this, searchActivity.class);
            //startActivity(goToIntent);

        }
        registerClickCallback();

    }

    public void fillMovieList(JSONArray jsonArr, ArrayList<Movie> movieList)
    {
        Log.d("JsonArr",jsonArr.toString());
        for (int i = 0; i < jsonArr.length(); i++) {
            try {
                JSONObject jsonobj = jsonArr.getJSONObject(i);
                String title = jsonobj.getString("title");
                String year = jsonobj.getString("year");
                String director = jsonobj.getString("director");
                String genre = jsonobj.getString("genres");
                String star = jsonobj.getString("star");
                String id = jsonobj.getString("movieid");

                Movie m = new Movie(id, title, year, director, genre, star);
                p.add(m);
                if (i < 10)
                    movieList.add(m);
            }
            catch(Exception e) {Log.d("fillArr err: ", e.toString());}
        }
        //p = new Pagination(movieList);
    }

    public void connectToTomcat(String whereclause, final ArrayList<Movie> movieList) {
        final Map<String, String> params = new HashMap<String, String>();
        String encoded = whereclause;
        encoded.replace("+","%2B");
        encoded.replace(" ","%20");
        params.put("whereclause", "patrick");
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest queryRequest = new StringRequest(Request.Method.POST, "https://ec2-54-191-52-23.us-west-2.compute.amazonaws.com:8443/project2/api/fulltext",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("query response", "here's a msg");
                        try {
                            JSONArray jsonArr = new JSONArray(response);
                            fillMovieList(jsonArr, movieList);
                            ((TextView) findViewById(R.id.pageId)).setText("page: " + pageNum + "/" + p.pageCount());
                        }
                        catch(Exception e) {Log.d("json error", e.toString());}
                        // Add the request to the RequestQueue.
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());
                        //((TextView) findViewById(R.id.http_response)).setText("err response");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }  // HTTP POST Form Data
        };

        queue.add(queryRequest);
    }

    public void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.lView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent,View viewClicked, int position, long id)
            {
                LinearLayout linLayout = (LinearLayout) viewClicked;
                View element = linLayout.getChildAt(0);
                TextView textView = (TextView) element;
                String message = textView.getText().toString();
                //Toast.makeText(BlueActivity.this,message,Toast.LENGTH_LONG).show();
                goToGreen(message);
            }
        });
    }

    public void prevButton(View view)
    {
        if (pageNum > 0)
        {
            movieList.clear();
            movieList.addAll(p.getPage(pageNum - 1));
            pageNum--;
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            ((TextView) findViewById(R.id.pageId)).setText("page: " + pageNum + "/" + p.pageCount());
        }

    }

    public void nextButton(View view)
    {
        if (pageNum < p.pageCount() )
        {
            movieList.clear();
            movieList.addAll(p.getPage(pageNum + 1));
            pageNum++;
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            ((TextView) findViewById(R.id.pageId)).setText("page: " + pageNum + "/" + p.pageCount());
        }

    }
}
