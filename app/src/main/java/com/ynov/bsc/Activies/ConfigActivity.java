package com.ynov.bsc.Activies;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.brice.messagemanager.R;

import java.util.Set;

public class ConfigActivity extends AppCompatActivity {
    ImageView publicButton;
    ImageView privateButton;
    boolean publicBool;
    boolean privateBool;
    SharedPreferences prefs;
    SharedPreferences.Editor prefEditor;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                SetSharedPreferences();
                Toast.makeText(ConfigActivity.this, prefs.getString("status", ""), Toast.LENGTH_LONG).show();
                this.onBackPressed();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("Salut", "Salut");
        SetSharedPreferences();
        Toast.makeText(ConfigActivity.this, prefs.getString("status", ""), Toast.LENGTH_LONG).show();
        super.onBackPressed();
        // to do


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        prefs = PreferenceManager.getDefaultSharedPreferences(ConfigActivity.this);
        Toast.makeText(ConfigActivity.this, prefs.getString("status", ""), Toast.LENGTH_LONG).show();

        publicButton = findViewById(R.id.publicbutton);
        privateButton = findViewById(R.id.privatebutton);
        publicBool = true;
        privateBool = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(prefs.getString("status", "").equals("public")){
            publicBool = true;
            privateBool = false;
        }else if(prefs.getString("status", "").equals("private")){
            privateBool = true;
            publicBool = false;
        }else if(prefs.getString("status", "").equals("")){
            privateBool = true;
            publicBool = true;
        }
        if(publicBool){
            publicButton.setAlpha(1f);
        }else{
            publicButton.setAlpha(0.5f);
        }

        if(privateBool){
            privateButton.setAlpha(1f);
        }else{
            privateButton.setAlpha(0.5f);
        }

        SetSharedPreferences();

        publicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicBool = !publicBool;
                if(publicBool){
                    publicButton.setAlpha(1f);
                }else{
                    publicButton.setAlpha(0.5f);
                }
                getSupportActionBar().setDisplayHomeAsUpEnabled(publicBool || privateBool);
            }
        });
        privateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privateBool = !privateBool;
                if(privateBool){
                    privateButton.setAlpha(1f);
                }else{
                    privateButton.setAlpha(0.5f);
                }
                getSupportActionBar().setDisplayHomeAsUpEnabled(publicBool || privateBool);
            }
        });
    }

    private void SetSharedPreferences(){
        prefEditor = prefs.edit();

        if(publicBool && privateBool){
            prefEditor.putString("status", "").apply();
        }
        else if(publicBool && !privateBool){
            prefEditor.putString("status", "public").apply();
        }
        else if(!publicBool && privateBool){
            prefEditor.putString("status", "private").apply();
        }
    }


}
