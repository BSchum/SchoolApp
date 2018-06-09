package com.ynov.bsc.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brice.messagemanager.R;
import com.google.gson.JsonObject;
import com.ynov.bsc.Activies.ListEntryActivity;
import com.ynov.bsc.Activies.MapsActivity;
import com.ynov.bsc.Model.School;
import com.ynov.bsc.Services.ISchoolController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.PendingIntent.getActivity;
import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Brice on 26/03/2018.
 */

public class SchoolAdapter extends BaseAdapter {
    private List<School> entryList;
    private Context context;
    private LayoutInflater ly;
    LocationManager lm;
    LocationListener ll;
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    public SchoolAdapter(Context context, List<School> entryList) {
        this.context = context;
        this.entryList = entryList;
        this.ly = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return entryList.size();
    }

    @Override
    public Object getItem(int position) {
        return entryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (entryList.get(position) != null) {
            return position;
        } else {
            return -1;
        }
    }
    public float DistanceTo(School school){
        LocationManager mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double lat = school.getLatitude();
        double lng = school.getLongitude();
        float distance = (float) Math.sqrt(Math.pow((lat - l.getLatitude()), 2) + Math.pow((lng - l.getLongitude()), 2));
        return Math.round(distance * 100);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RelativeLayout layoutItem;
        //J'utilise mon layout ou le layout passé.
        if (convertView == null) {
            //Initialisation de notre item à partir du  layout entry_item
            layoutItem = (RelativeLayout) ly.inflate(R.layout.entry_item, parent, false);
        } else {
            layoutItem = (RelativeLayout) convertView;
        }
        // Je recupere toutes mes views
        TextView name = (TextView) layoutItem.findViewById(R.id.det_name);
        TextView addresse = (TextView) layoutItem.findViewById(R.id.det_addresse);
        TextView nbEleve = (TextView) layoutItem.findViewById(R.id.det_nbeleve);
        ImageView pouce = (ImageView) layoutItem.findViewById(R.id.pouce);
        TextView distanceText = (TextView) layoutItem.findViewById(R.id.distance);
        ImageView map = layoutItem.findViewById(R.id.list_tomap);
        ImageView delete = layoutItem.findViewById(R.id.list_delet);
        RelativeLayout lay = (RelativeLayout) layoutItem.findViewById(R.id.entrylayout);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "toMap", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("latitude", entryList.get(position).getLatitude());
                intent.putExtra("longitude", entryList.get(position).getLongitude());
                context.startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Do you really want to delete this school?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                                httpClient.addInterceptor(new Interceptor() {
                                    @Override
                                    public okhttp3.Response intercept(Chain chain) throws IOException {
                                        Request original = chain.request();
                                        prefs = PreferenceManager.getDefaultSharedPreferences(context);
                                        prefsEditor = prefs.edit();
                                        Request request = original.newBuilder()
                                                .header("AUTHORIZATION", prefs.getString("auth_token", "").substring(1, prefs.getString("auth_token", "").length() - 1))
                                                .method(original.method(), original.body())
                                                .build();

                                        return chain.proceed(request);
                                    }
                                });

                                OkHttpClient client = httpClient.build();
                                ISchoolController iSchoolController = new Retrofit.Builder()
                                        .baseUrl(ISchoolController.ENDPOINT)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .client(client)
                                        .build()
                                        .create(ISchoolController.class);

                                iSchoolController.removeSchool(entryList.get(position).getId()).enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        if((response.errorBody() == null) && (response.body().get("success").toString().equals("true"))){
                                            ((ListEntryActivity)context).recreate();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<JsonObject> call, Throwable t) {

                                    }
                                });
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });
        // Je set les texts de toutes les texts view du layout.
        name.setText(entryList.get(position).getNom());
        addresse.setText(entryList.get(position).getAddresse());
        nbEleve.setText(entryList.get(position).getNb_student() + " élèves");
        float distance = DistanceTo(entryList.get(position));
        distanceText.setText(distance + " KM");
        if (entryList.get(position).getNb_student() >= 50) {
            pouce.setImageResource(R.drawable.ok);
        } else {
            pouce.setImageResource(R.drawable.ko);
        }

        if (entryList.get(position).getNb_student() < 50) {
            lay.setBackgroundColor(Color.RED);
        } else if (entryList.get(position).getNb_student() >= 50 && entryList.get(position).getNb_student() < 200) {
            lay.setBackgroundColor(0xFFFF9900);
        } else {
            lay.setBackgroundColor(Color.GREEN);
        }
        //On renvoie le layout avec les nouveaux objets.
        return layoutItem;
    }
}
