package com.example.alfredo.marvel;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.alfredo.marvel.pojo.MarvelDude;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class DetalleActivity extends Activity {

    private TextView id, nombre, fecha, descripcion;
    private NetworkImageView networkImageView;
    private String identifier, name, description, date, image;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        identifier = (String)getIntent().getSerializableExtra("id");

        id = (TextView)findViewById(R.id.idSuperheroe);
        nombre = (TextView)findViewById(R.id.nombreSuperheroe);
        fecha = (TextView)findViewById(R.id.fechaSuperheroe);
        descripcion = (TextView)findViewById(R.id.descripcionSuperheroe);
        networkImageView = (NetworkImageView) findViewById(R.id.imagenSuperheroe);


        id.setText("ID: " + identifier);

        mQueue = VolleySingleton.getInstance(this).getRequestQueue();
        //new MarvelJson(adapter).execute();
        jsonMarvel(getMarvelString(identifier));



    }

    private void jsonMarvel(String url){

        System.out.println("Inicia jsonMarvel: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //System.out.println("continua");
                    JSONObject data = response.getJSONObject("data");
                    //System.out.println(data);
                    JSONArray jsonArray = data.getJSONArray("results");
                    //System.out.println("hola");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    System.out.println(jsonObject.getLong("id") + "hola");
                    identifier = jsonObject.getLong("id") + "";
                    name = jsonObject.getString("name");
                    description = jsonObject.getString("description");
                    date = jsonObject.getString("modified");
                    image = jsonObject.getJSONObject("thumbnail").getString("path") + "/portrait_uncanny" + "." + jsonObject.getJSONObject("thumbnail").getString("extension");
                    id.setText("ID: " +  identifier);
                    nombre.setText("Nombre: " + name);
                    descripcion.setText("Descripción: " + description);
                    fecha.setText("Fecha: " + date);

                    RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
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
                    networkImageView.setImageUrl(image,imageLoader);

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

    private String getMarvelString(String id){
        String ts = Long.toString(System.currentTimeMillis() / 1000);
        String apikey = "1681a9eefcf8fbf43de66c59727718da";//public key
        String hash = md5(ts + "ede49375699321e3736436b53011574333433f40" + "1681a9eefcf8fbf43de66c59727718da");//private + public key
        //ArrayList<String> arrayList = new ArrayList<>();

        final String CHARACTER_BASE_URL =
                "http://gateway.marvel.com/v1/public/characters/" + id;

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
                //.appendQueryParameter(ORDER, "name")
                //.appendQueryParameter("limit", "100")
                //.appendQueryParameter("offset", offset+"")
                .build();
        System.out.println("Link: " + builtUri.toString());
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
    private static char[] HEXCodes = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
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
}
