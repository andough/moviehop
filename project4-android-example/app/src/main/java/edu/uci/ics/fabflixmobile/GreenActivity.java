package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class GreenActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green);

        Bundle bundle = getIntent().getExtras();
        //Toast.makeText(this, "Last activity was " + bundle.get("last_activity") + ".", Toast.LENGTH_LONG).show();

       String whereclause = bundle.getString("movieId");
        if (whereclause != null && !"".equals(whereclause)) {
            ((TextView) findViewById(R.id.movTitle)).setText(whereclause);
        }
        else
            ((TextView) findViewById(R.id.movTitle)).setText("no title");
        final ArrayList<Movie> movieList = new ArrayList<>();
        if (whereclause != null) {
            Log.d("movieid value: ", whereclause);


            //connectToTomcat(whereclause,movieList);

            final Map<String, String> params = new HashMap<String, String>();
            params.put("id",whereclause);
            final RequestQueue queue = NetworkManager.sharedManager(this).queue;

            final StringRequest queryRequest = new StringRequest(Request.Method.POST, "https://ec2-54-191-52-23.us-west-2.compute.amazonaws.com:8443/project2/api/single-movie",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONArray jsonArr = new JSONArray(response);
                                fillMovieList(jsonArr, movieList);
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
            //Intent goToIntent = new Intent(this, searchActivity.class);
            //startActivity(goToIntent);

        }

    }

    public void fillMovieList(JSONArray jsonArr, ArrayList<Movie> movieList)
    {
        Log.d("JsonArr",jsonArr.toString());
            try {
                JSONObject jsonobj = jsonArr.getJSONObject(0);
                Log.d("json obj ", jsonobj.toString());
                String title = jsonobj.getString("title");
                String year = jsonobj.getString("year");
                String director = jsonobj.getString("director");
                String genre = jsonobj.getString("genres");
                String star = jsonobj.getString("star");
                String id = jsonobj.getString("movieid");

                Movie m = new Movie(id,title,year,director,genre,star);
                Log.d("my movie: ", m.getTitle());
                movieList.add(m);

                ((TextView) findViewById(R.id.movTitle)).setText(m.getTitle());
                ((TextView) findViewById(R.id.movYear)).setText(m.getYear());
                ((TextView) findViewById(R.id.movDirector)).setText(m.getDirector());
                ((TextView) findViewById(R.id.movGenre)).setText(m.getGenre());
                ((TextView) findViewById(R.id.movStar)).setText(m.getStar());
            }
            catch(Exception e) {Log.d("fillArr err: ", e.toString());}


    }


    public void goToRed(View view) {
       // String msg = ((EditText) findViewById(R.id.green_2_red_message)).getText().toString();

        Intent goToIntent = new Intent(this, RedActivity.class);

       // goToIntent.putExtra("last_activity", "green");
        //goToIntent.putExtra("message", msg);

        startActivity(goToIntent);
    }

    public void goToBlue(View view) {
        //String msg = ((EditText) findViewById(R.id.green_2_blue_message)).getText().toString();

        Intent goToIntent = new Intent(this, BlueActivity.class);

       // goToIntent.putExtra("last_activity", "green");
        //goToIntent.putExtra("message", msg);

        startActivity(goToIntent);
    }

}
