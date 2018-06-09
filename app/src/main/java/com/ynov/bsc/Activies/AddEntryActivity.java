package com.ynov.bsc.Activies;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brice.messagemanager.R;
import com.google.gson.JsonObject;
import com.ynov.bsc.Services.AddEntryService;
import com.ynov.bsc.Services.AsyncResponce;
import com.ynov.bsc.Services.ISchoolController;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddEntryActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    EditText nom;
    EditText addresse;
    EditText latitude;
    EditText longitude;
    EditText zip_code;
    EditText mail;
    EditText nb_student;
    EditText city;
    EditText openings;
    EditText phone;
    Button annuler;
    Button valider;
    Spinner status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_school);
        GetAllViews();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        prefs = PreferenceManager.getDefaultSharedPreferences(AddEntryActivity.this);
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
                JsonObject json = new JsonObject();
                json.addProperty("id", getIntent().getIntExtra("id", 0));
                json.addProperty("name", nom.getText().toString());
                json.addProperty("address", addresse.getText().toString());
                json.addProperty("postal_code", zip_code.getText().toString());
                json.addProperty("commune", city.getText().toString());
                json.addProperty("horaire", openings.getText().toString());
                json.addProperty("phone_number", phone.getText().toString());
                json.addProperty("email", mail.getText().toString());
                json.addProperty("latitude", Double.parseDouble(latitude.getText().toString()));
                json.addProperty("longitude", Double.parseDouble(longitude.getText().toString()));
                json.addProperty("nb_student", Integer.parseInt(nb_student.getText().toString()));
                if(status.getSelectedItem().toString().toLowerCase().equals("privée")){
                    json.addProperty("status", "priv");
                }
                else{
                    json.addProperty("status", "publ");
                }

                iSchoolController.saveSchool(json).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.errorBody() == null){
                            Toast.makeText(AddEntryActivity.this, "Success", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }
        });
    }
    public void GetAllViews(){
        nom = findViewById(R.id.add_name);
        addresse = findViewById(R.id.add_addresse);
        latitude = findViewById(R.id.add_lat);
        longitude = findViewById(R.id.add_long);
        zip_code = findViewById(R.id.add_zipcode);
        mail = findViewById(R.id.add_email);
        nb_student = findViewById(R.id.add_nbstudent);
        city = findViewById(R.id.add_city);
        openings = findViewById(R.id.add_openings);
        status = findViewById(R.id.status);
        phone = findViewById(R.id.add_phone);
        annuler = findViewById(R.id.add_annuler);
        valider = findViewById(R.id.add_validate);
        status = (Spinner) findViewById(R.id.add_status);
    }
}

