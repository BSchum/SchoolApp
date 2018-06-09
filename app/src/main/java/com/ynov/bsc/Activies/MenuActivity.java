package com.ynov.bsc.Activies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.example.brice.messagemanager.R;

public class MenuActivity extends AppCompatActivity {

    ImageButton listEntry;
    ImageButton map;
    ImageButton addEntry;
    ImageButton config;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide action bar for this activity
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        prefs = PreferenceManager.getDefaultSharedPreferences(MenuActivity.this);
        prefsEditor = prefs.edit();
        prefsEditor.putString("status", "").apply();
        setContentView(R.layout.activity_menu);

        listEntry = findViewById(R.id.listbutton);
        map = findViewById(R.id.mapbutton);
        addEntry = findViewById(R.id.addschoolbutton);
        config = findViewById(R.id.configb);

        listEntry.setOnClickListener(genericOnClickListener);
        map.setOnClickListener(genericOnClickListener);
        addEntry.setOnClickListener(genericOnClickListener);
        config.setOnClickListener(genericOnClickListener);
    }

    View.OnClickListener genericOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.listbutton:
                    // On redirige vers la liste des entrées
                    Intent toListEntry = new Intent(MenuActivity.this, ListEntryActivity.class);
                    startActivity(toListEntry);
                    break;
                case R.id.mapbutton:
                    // On redirige vers l'ajout d'entrée
                    Intent toMapActivity = new Intent(MenuActivity.this, MapsActivity.class);
                    startActivity(toMapActivity);
                    break;
                case R.id.addschoolbutton:
                    Intent toAdd = new Intent(MenuActivity.this, AddEntryActivity.class);
                    startActivity(toAdd);
                    break;
                case R.id.configb:
                    Intent toConfig = new Intent(MenuActivity.this, ConfigActivity.class);
                    startActivity(toConfig);
                    break;
                default:
                    break;
            }
        }
    };
}
