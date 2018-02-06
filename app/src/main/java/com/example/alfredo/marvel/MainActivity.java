package com.example.alfredo.marvel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.alfredo.marvel.adapters.MarvelAdapter;
import com.example.alfredo.marvel.pojo.MarvelDude;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private MarvelAdapter marvelAdapter;

    private RequestQueue mQueue;

    private int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.lista);

        /*
        ituneArrayAdapter = new ItuneArrayAdapter(this, R.layout.marvel_layout, new ArrayList<Itune>());



        listView.setAdapter(ituneArrayAdapter);//el adaptador es para poder interfacer y controlar los datos desde ahi
        new ProcesaJson(ituneArrayAdapter).execute("https://itunes.apple.com/search?term=jack+johnson");
*/
        marvelAdapter = new MarvelAdapter(this,R.layout.marvel_layout,new ArrayList<MarvelDude>());
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,new ArrayList<String>());
        listView.setAdapter(marvelAdapter);

        mQueue = VolleySingleton.getInstance(this).getRequestQueue();
        //new MarvelJson(adapter).execute();
        jsonMarvel(getMarvelString(offset), marvelAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                MarvelDude md = marvelAdapter.getItem(i);
                Toast.makeText(getApplicationContext(),md.id, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainActivity.this, DetalleActivity.class);
                intent.putExtra("id", md.id);
                startActivity(intent);
            }
        });



    }

    private final String LOG_TAG = "MARVEL";

    private static char[] HEXCodes = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    private void jsonMarvel(String url, final MarvelAdapter adapter){
        adapter.clear();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONArray jsonArray = data.getJSONArray("results");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject thumbnail = jsonObject.getJSONObject("thumbnail");
                        String string = thumbnail.getString("path") + "/portrait_small" + "." + thumbnail.getString("extension");
                        MarvelDude marvelDude = new MarvelDude();
                        marvelDude.id = jsonObject.getLong("id") + "";
                        marvelDude.name = jsonObject.getString("name");
                        marvelDude.url = string;
                        adapter.add(marvelDude);
                    }
                    adapter.notifyDataSetChanged(); //actualiza la vista
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);
    }

    private String getMarvelString(int offset){
        String ts = Long.toString(System.currentTimeMillis() / 1000);
        String apikey = "1681a9eefcf8fbf43de66c59727718da";//public key
        String hash = md5(ts + "ede49375699321e3736436b53011574333433f40" + "1681a9eefcf8fbf43de66c59727718da");//private + public key
        ArrayList<String> arrayList = new ArrayList<>();


            /*
                Conexión con el getway de marvel
            */
        final String CHARACTER_BASE_URL =
                "http://gateway.marvel.com/v1/public/characters";

            /*
                Configuración de la petición
            */
        String characterJsonStr = null;
        final String TIMESTAMP = "ts";
        final String API_KEY = "apikey";
        final String HASH = "hash";
        final String ORDER = "orderBy";

        Uri builtUri;
        builtUri = Uri.parse(CHARACTER_BASE_URL+"?").buildUpon()
                .appendQueryParameter(TIMESTAMP, ts)
                .appendQueryParameter(API_KEY, apikey)
                .appendQueryParameter(HASH, hash)
                .appendQueryParameter(ORDER, "name")
                .appendQueryParameter("limit", "100")
                .appendQueryParameter("offset", offset+"")
                .build();
            return builtUri.toString();
    }


    public static String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            String hash = new String(hexEncode(digest.digest()));
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    /*
        Investiga y reporta qué hace esta aplicación
    */
    public static String hexEncode(byte[] bytes) {
        char[] result = new char[bytes.length*2];
        int b;
        for (int i = 0, j = 0; i < bytes.length; i++) {
            b = bytes[i] & 0xff;
            result[j++] = HEXCodes[b >> 4];
            result[j++] = HEXCodes[b & 0xf];
        }
        return new String(result);
    }

    public void atras(View view){
        if(offset > 0){
            offset -= 100;
        }

        String url = getMarvelString(offset);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.lista);
        marvelAdapter = new MarvelAdapter(this,R.layout.marvel_layout,new ArrayList<MarvelDude>());
        listView.setAdapter(marvelAdapter);
        mQueue = VolleySingleton.getInstance(this).getRequestQueue();
        jsonMarvel(getMarvelString(offset), marvelAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                MarvelDude md = marvelAdapter.getItem(i);
                Toast.makeText(getApplicationContext(),md.id, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainActivity.this, DetalleActivity.class);
                intent.putExtra("id", md.id);
                startActivity(intent);
            }
        });


    }
    public void adelante(View view){
        if(offset < 1400){
            offset += 100;
        }

        String url = getMarvelString(offset);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.lista);
        marvelAdapter = new MarvelAdapter(this,R.layout.marvel_layout,new ArrayList<MarvelDude>());
        listView.setAdapter(marvelAdapter);
        mQueue = VolleySingleton.getInstance(this).getRequestQueue();
        jsonMarvel(getMarvelString(offset), marvelAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                MarvelDude md = marvelAdapter.getItem(i);
                Toast.makeText(getApplicationContext(),md.id, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainActivity.this, DetalleActivity.class);
                intent.putExtra("id", md.id);
                startActivity(intent);
            }
        });

        /*
        String url = getMarvelString(offset);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.lista);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,new ArrayList<String>());
        listView.setAdapter(adapter);
        mQueue = VolleySingleton.getInstance(this).getRequestQueue();
        jsonMarvel(getMarvelString(offset), marvelAdapter);*/
    }





}
