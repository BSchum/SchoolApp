package com.ynov.bsc.Services;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Brice on 27/03/2018.
 */

public class AddEntryService extends AsyncTask<String, String, String>{

    AsyncResponce delegate;

    public AddEntryService(AsyncResponce delegate){
        this.delegate = delegate;
    }
    @Override
    protected String doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL("http://thibault01.com:8081/saveEntree");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //Passage de la requete en mode POST
            urlConnection.setRequestMethod("POST");

            //Set des headers de la requete
            urlConnection.setRequestProperty("Accept-Language", "UTF-8");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf8");
            urlConnection.setDoOutput(true);

            //Formattage d'une json correspondant au body de la requete.
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("nom", strings[0]);
            jsonParam.put("espece", strings[1]);
            jsonParam.put("sexe", strings[2]);
            jsonParam.put("description", strings[3]);

            //Permet d'ecrire dans le flux d'envoi a la requete. Correspont au body de la requete.
            DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
            os.writeBytes(jsonParam.toString());
            //Enregistrement des changements
            os.flush();
            //Status code de la requete
            String res = Integer.toString(urlConnection.getResponseCode());
            //Fermeture de l'outputstream et de la connection.
            os.close();
            urlConnection.disconnect();
            return res;
        }catch(Exception e){
            Log.e("Error", "Erreur d'acces au ws");
        }
        return "400";
    }

    @Override
    protected void onPostExecute(String statusCode) {
        super.onPostExecute(statusCode);
        delegate.ComputeResult(statusCode);
    }
}
