package com.eightyvoltbattery.grooveradar;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ArcadeGameListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcade_game_list);

        final TextView tvArcadeGameListLabel = (TextView) findViewById(R.id.tvArcadeGameListLabel);
        final ListView lvArcadeGameList = (ListView) findViewById(R.id.lvArcadeGameList);
        final List<String> gameList = new ArrayList<String>();

        Intent lastIntent = getIntent();
        int id = lastIntent.getIntExtra("ARCADE_ID", 0);

        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if(success) {
                        JSONArray jsonGames = jsonResponse.getJSONArray("game_name");

                        for(int i = 0; i < jsonGames.length(); i ++) {
                            JSONObject jsonGame = jsonGames.getJSONObject(i);
                            String game = jsonGame.getString("game_name");
                            gameList.add(game);
                        }

                        Collections.sort(gameList, new Comparator<String>() {

                            @Override
                            public int compare(String game1, String game2) {
                                String formattedGame1 = game1.replaceAll("\\s+", "").toLowerCase();
                                String formattedGame2 = game2.replaceAll("\\s+", "").toLowerCase();
                                return formattedGame1.compareTo(formattedGame2);
                            }
                        });

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ArcadeGameListActivity.this, android.R.layout.simple_list_item_1, gameList);
                        lvArcadeGameList.setAdapter(arrayAdapter);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ArcadeGameListActivity.this);
                        builder.setMessage("Error communicating with server, try again later.")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        tvArcadeGameListLabel.setText("ALL GAMES LOCATED AT " + lastIntent.getStringExtra("ARCADE_NAME"));

        ArcadeGameListRequest arcadeGameListRequest = new ArcadeGameListRequest(id, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ArcadeGameListActivity.this);
        queue.add(arcadeGameListRequest);
    }
}