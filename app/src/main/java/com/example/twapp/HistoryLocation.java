package com.example.twapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Array;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HistoryLocation extends AppCompatActivity {
    ListView list_HistoryLocation;
    OkHttpClient client = new OkHttpClient();
    List<String> values = new ArrayList<>();
    String tsStr = "";
    DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_location);
        list_HistoryLocation = (ListView) findViewById(R.id.list_HistoryLocation);
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
        list_HistoryLocation.setAdapter(adapter);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        class getHistoryLocationTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... Void) {
                Request request = new Request.Builder()
                        .url("https://g8.minouo.eu.org/Condition/get/4")
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.code() == 200) {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject j =  jsonArray.getJSONObject(i);
                            Timestamp ts = new Timestamp(j.getLong("time_stamp"));
                            tsStr = sdf.format(ts);
                            values.add(String.format("longitude:%s    latitude:%s\n%s",
                                    j.getString("longgps"),
                                    j.getString("latigps"),
                                    tsStr

                            ));

                        }
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                ((ArrayAdapter<?>) adapter).notifyDataSetChanged();
            }
        }
        new getHistoryLocationTask().execute();

    }
}