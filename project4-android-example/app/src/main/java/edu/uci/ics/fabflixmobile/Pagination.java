package edu.uci.ics.fabflixmobile;
import java.util.*;
import android.util.Log;


public class Pagination {
    public int TOTAL_NUM_ITEMS = 0;
    public final int ITEMS_PER_PAGE = 10;
    private ArrayList<Movie> movieList;

    public Pagination()
    {
        movieList = new ArrayList<Movie>();
    }

    public Pagination(ArrayList<Movie> mL)
    {
        movieList = new ArrayList<Movie>();
        movieList.addAll(mL);
        TOTAL_NUM_ITEMS = movieList.size();
        Log.d("constructor: ",movieList.get(0).getTitle());
    }

    public void setList(ArrayList<Movie> mL)
    {
        movieList = new ArrayList<Movie>();
        movieList.addAll(mL);
        TOTAL_NUM_ITEMS = movieList.size();
        Log.d("constructor: ",movieList.get(0).getTitle());
    }

    public void add(Movie m)
    {
        movieList.add(m);
        TOTAL_NUM_ITEMS ++;
    }

    public int pageCount() {
        int remainingItems=TOTAL_NUM_ITEMS % ITEMS_PER_PAGE;

        if(remainingItems>0)
        {
            return TOTAL_NUM_ITEMS / ITEMS_PER_PAGE;
        }
        return (TOTAL_NUM_ITEMS / ITEMS_PER_PAGE)-1;

    }

    public ArrayList<Movie> getPage(int page)
    {
        int index = 10 * page;
        int pageSize = TOTAL_NUM_ITEMS - index;
        ArrayList<Movie> current = new ArrayList<Movie>();

        if (pageSize > 10)
            pageSize = ITEMS_PER_PAGE;
        for (int i = 0; i < pageSize; i++)
        {
            current.add(movieList.get(index + i));
            Log.d("added",movieList.get(index + i).getTitle());
        }

        return current;
    }
}
