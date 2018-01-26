package com.example.alfredo.marvel;

import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.lista);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());



        listView.setAdapter(arrayAdapter);//el adaptador es para poder interfacer y controlar los datos desde ahi
        new ProcesaJson(arrayAdapter).execute("https://itunes.apple.com/search?term=jack+johnson");

    }

    public class ProcesaJson extends AsyncTask<String,Integer,ArrayList<String>>{

        private ArrayAdapter<String> adapter;

        public ProcesaJson(ArrayAdapter<String> adapter){
            this.adapter = adapter;
        }

        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            Json json = new Json();
            String jsonString = json.serviceCall(urls[0]);
            ArrayList<String> arrayList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject dato = jsonArray.getJSONObject(i);
                    arrayList.add(dato.getString("collectionName"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            adapter.clear();
            adapter.addAll(strings);
            adapter.notifyDataSetChanged();
        }
    }
}
