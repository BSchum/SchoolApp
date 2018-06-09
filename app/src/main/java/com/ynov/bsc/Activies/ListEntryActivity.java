package com.ynov.bsc.Activies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.brice.messagemanager.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ynov.bsc.Adapter.SchoolAdapter;
import com.ynov.bsc.Model.School;
import com.ynov.bsc.Services.ISchoolController;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListEntryActivity extends AppCompatActivity {
    ListView entrys;
    ArrayList<School> schoolsArray;
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;
    public void ShowBackButtonOnActionBar(){
         android.support.v7.app.ActionBar actionBar = getSupportActionBar();
         if (actionBar != null) {
             actionBar.setDisplayHomeAsUpEnabled(true);
         }
     }
    public void SetOnItemListener(){
         entrys.setOnItemClickListener(new AdapterView.OnItemClickListener(){
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Jclick", "Jdclick");
                 //Passage des données de l'entré a la vue détaillé.
                 Intent intent = new Intent(ListEntryActivity.this, DetailledActivity.class);
                 intent.putExtra("id", schoolsArray.get(position).getId());
                 intent.putExtra("nom", schoolsArray.get(position).getNom());
                 intent.putExtra("addresse", schoolsArray.get(position).getAddresse());
                 intent.putExtra("latitude", schoolsArray.get(position).getLatitude());
                 intent.putExtra("longitude", schoolsArray.get(position).getLongitude());
                 intent.putExtra("zip_code", schoolsArray.get(position).getZip_code());
                 intent.putExtra("mail", schoolsArray.get(position).getMail());
                 intent.putExtra("nb_student", schoolsArray.get(position).getNb_student());
                 intent.putExtra("city", schoolsArray.get(position).getCity());
                 intent.putExtra("openings", schoolsArray.get(position).getOpenings());
                 intent.putExtra("status", schoolsArray.get(position).getStatus());
                 intent.putExtra("phone", schoolsArray.get(position).getPhone());
                 startActivity(intent);
             }
         });
     }
    public void ComputeResponse(Response<JsonObject> response) {
        JsonArray schools = response.body().get("schools").getAsJsonArray();
        for (int i = 0; i < schools.size(); i++) {
            JsonObject d = schools.get(i).getAsJsonObject();
            schoolsArray.add(new School(
                    d.get("id").isJsonNull() ? -1 : d.get("id").getAsInt(),
                    d.get("name").isJsonNull() ? "" : d.get("name").getAsString(),
                    d.get("address").isJsonNull() ? "" : d.get("address").getAsString(),
                    d.get("latitude").isJsonNull() ? -1 : d.get("latitude").getAsDouble(),
                    d.get("longitude").isJsonNull() ? -1 : d.get("longitude").getAsDouble(),
                    d.get("postal_code").isJsonNull() ? "" : d.get("postal_code").getAsString(),
                    d.get("mail").isJsonNull() ? "" : d.get("mail").getAsString(),
                    d.get("nb_student").isJsonNull() ? -1 : d.get("nb_student").getAsInt(),
                    d.get("commune").isJsonNull() ? "" : d.get("commune").getAsString(),
                    d.get("horaire").isJsonNull() ? "" : d.get("horaire").getAsString(),
                    d.get("status").isJsonNull() ? "" : d.get("status").getAsString(),
                    d.get("phone_number").isJsonNull() ? "" : d.get("phone_number").getAsString()
            ));


        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_entry);
        ShowBackButtonOnActionBar();

        entrys = findViewById(R.id.entry_list);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                prefs = PreferenceManager.getDefaultSharedPreferences(ListEntryActivity.this);
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

        prefs = PreferenceManager.getDefaultSharedPreferences(ListEntryActivity.this);
        prefsEditor = prefs.edit();
        iSchoolController.getSchool(prefs.getString("status", "")).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                schoolsArray = new ArrayList<>();
                ComputeResponse(response);

                SchoolAdapter adapter = new SchoolAdapter(ListEntryActivity.this, schoolsArray);
                entrys.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
        SetOnItemListener();

    }


}

