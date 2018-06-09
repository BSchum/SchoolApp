package com.ynov.bsc.Services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.net.URL;

public class ImageService extends AsyncTask<String, Void, Bitmap> {

    ImageView detImageView;


    public ImageService(ImageView imageView) {
        this.detImageView = imageView;
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        try{
            //Call au webservices pour recuperer l'image
            URL url = new URL("http://thibault01.com:8081/images/"+strings[0]+".png");
            Log.d("URL","http://thibault01.com:8081/images/"+strings[0]+".png");
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bmp;
        }catch(Exception e){
            Log.e("Error", "Erreur de l'acces au WS", e);
            return null;
        }
    }
    @Override
    protected void onPostExecute(Bitmap b){
        super.onPostExecute(b);
        //Set l'image de l'imageview passé en parametre
        detImageView.setImageBitmap(b);
    }
}