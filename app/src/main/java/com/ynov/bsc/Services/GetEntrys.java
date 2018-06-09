package com.ynov.bsc.Services;

import android.os.AsyncTask;
import android.util.Log;

import com.ynov.bsc.Services.AsyncResponce;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetEntrys extends AsyncTask<String, String, String> {
    //Se charge de renvoyer les donnees via callback a lactivité
    AsyncResponce delegate;
    public GetEntrys(AsyncResponce delegate){
        this.delegate = delegate;
    }
    @Override
    protected String doInBackground(String...strings){
        URL url = null;
        HttpURLConnection urlConnection = null;
        String response = "";
        try {
            url = new URL("http://thibault01.com:8081/getEcolePrimaire");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while((response = reader.readLine()) != null){
                sb.append(response);
            }
            response = sb.toString();
        }catch(Exception e){
            Log.e("Error", "Erreur de l'acces au WS", e);
        }
        //On envoie les données des ecoles recuperé
        publishProgress(response);
        return response;
    }

    @Override
    protected void onProgressUpdate(String...dataS){
        super.onProgressUpdate(dataS);
        delegate.ComputeResult(dataS[0]);
    }
}