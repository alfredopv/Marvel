package com.example.alfredo.marvel.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.alfredo.marvel.R;
import com.example.alfredo.marvel.VolleySingleton;
import com.example.alfredo.marvel.pojo.MarvelDude;

import java.util.List;

/**
 * Created by Alfredo on 02/02/2018.
 */

public class MarvelAdapter extends ArrayAdapter<MarvelDude> {

    private Context context;

    public MarvelAdapter(Context context, int resource, List<MarvelDude> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MarvelDude marvelDude = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.marvel_layout,parent,false);
        }

        TextView textView = (TextView)convertView.findViewById(R.id.collection);
        NetworkImageView networkImageView = (NetworkImageView) convertView.findViewById(R.id.imageView);

        textView.setText(marvelDude.name);
        //se hace porque la imagen es de un JSON
        RequestQueue requestQueue = VolleySingleton.getInstance(context).getRequestQueue();
        ImageLoader imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
        networkImageView.setImageUrl(marvelDude.url,imageLoader);

        return convertView;
    }
}
