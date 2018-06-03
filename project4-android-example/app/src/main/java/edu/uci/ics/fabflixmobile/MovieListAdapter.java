package edu.uci.ics.fabflixmobile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import java.util.*;
import android.widget.TextView;
import android.util.Log;

import java.util.List;

public class MovieListAdapter extends ArrayAdapter<Movie> {
    private static final String TAG = "MovieListAdapter";
    private Context mContext;
     int mResource;

    public MovieListAdapter(Context context, int resource, ArrayList<Movie> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        String id = getItem(position).getId();
        String title = getItem(position).getTitle();
        String year = getItem(position).getYear();
        String director = getItem(position).getDirector();
        String genre = getItem(position).getGenre();
        String star = getItem(position).getStar();

        Movie m = new Movie(id,title, year, director, genre, star);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvMid = (TextView) convertView.findViewById(R.id.mid);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.t1);
        TextView tvYear = (TextView) convertView.findViewById(R.id.t2);
        TextView tvDirector = (TextView) convertView.findViewById(R.id.t3);
        TextView tvGenre = (TextView) convertView.findViewById(R.id.t4);
        TextView tvStar = (TextView) convertView.findViewById(R.id.t5);

        tvMid.setText(id);
        tvTitle.setText(title);
        tvYear.setText(year);
        tvDirector.setText(director);
        tvGenre.setText(genre);
        tvStar.setText(star);
        return convertView;
    }
}
