package com.ynov.bsc.Activies;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brice.messagemanager.R;
import com.google.gson.JsonObject;
import com.ynov.bsc.Model.School;
import com.ynov.bsc.Services.ISchoolController;
import com.ynov.bsc.Services.ImageService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailledActivity extends AppCompatActivity {

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
    MenuItem returnViewMode;
    MenuItem modalEdit;
    Menu mMenu;

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.det_modal:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.det_dialog);
                final ImageButton edit = dialog.findViewById(R.id.det_edit_button);
                final ImageButton map = dialog.findViewById(R.id.det_map_button);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OnOffInputs(true);
                        returnViewMode.setVisible(true);
                        modalEdit.setVisible(false);
                        getSupportActionBar().setTitle("Edition " + nom.getText());
                        dialog.hide();
                    }
                });

                map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DetailledActivity.this, MapsActivity.class);
                        intent.putExtra("latitude", Double.parseDouble(latitude.getText().toString()));
                        intent.putExtra("longitude", Double.parseDouble(longitude.getText().toString()));
                        startActivity(intent);
                    }
                });


                dialog.show();
                return true;
            case R.id.det_delete:
                OnOffInputs(false);
                SetInputsData();
                returnViewMode.setVisible(false);
                modalEdit.setVisible(true);

                getSupportActionBar().setTitle("Détails " + nom.getText());


            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.det_menu_view_mode, menu);
        getMenuInflater().inflate(R.menu.det_menu_edit_mode, menu);
        modalEdit = menu.findItem(R.id.det_modal);
        returnViewMode = menu.findItem(R.id.det_delete);

        returnViewMode.setVisible(false);


        mMenu = menu;
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailled);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GetAllViews();
        SetInputsData();
        OnOffInputs(false);
        getSupportActionBar().setTitle("Détails " + nom.getText());


        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        prefs = PreferenceManager.getDefaultSharedPreferences(DetailledActivity.this);
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
                    Toast.makeText(DetailledActivity.this, "priv", Toast.LENGTH_LONG).show();

                    json.addProperty("status", "priv");
                }
                else{
                    Toast.makeText(DetailledActivity.this, "publ", Toast.LENGTH_LONG).show();
                    json.addProperty("status", "publ");

                }

                iSchoolController.updateSchool(getIntent().getIntExtra("id", 0) ,json).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.errorBody() == null){
                            OnOffInputs(false);
                            returnViewMode.setVisible(false);
                            modalEdit.setVisible(true);
                        }
                        else{
                            Toast.makeText(DetailledActivity.this, "Une erreur est survenu dans la mise a jour", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }
        });
    }

    void GetAllViews(){
        nom = findViewById(R.id.det_name);
        addresse = findViewById(R.id.det_addresse);
        latitude = findViewById(R.id.det_lat);
        longitude = findViewById(R.id.det_long);
        zip_code = findViewById(R.id.det_zipcode);
        mail = findViewById(R.id.det_email);
        nb_student = findViewById(R.id.det_nbstudent);
        city = findViewById(R.id.det_city);
        openings = findViewById(R.id.det_openings);
        status = findViewById(R.id.status);
        phone = findViewById(R.id.det_phone);
        annuler = findViewById(R.id.det_annuler);
        valider = findViewById(R.id.det_validate);
        status = (Spinner) findViewById(R.id.status);
    }
    void OnOffInputs(boolean onOff){
        nom.setEnabled(onOff);
        addresse.setEnabled(onOff);
        latitude.setEnabled(onOff);
        longitude.setEnabled(onOff);
        zip_code.setEnabled(onOff);
        mail.setEnabled(onOff);
        nb_student.setEnabled(onOff);
        city.setEnabled(onOff);
        openings.setEnabled(onOff);
        phone.setEnabled(onOff);
        annuler.setEnabled(onOff);
        valider.setEnabled(onOff);
        status.setEnabled(onOff);

    }
    void SetInputsData(){
        //Recupération des données passés via l'intent
        Intent intent = getIntent();
        nom.setText(intent.getStringExtra("nom"));
        addresse.setText(intent.getStringExtra("addresse"));
        latitude.setText(String.valueOf(intent.getDoubleExtra("latitude", 0.00)));
        longitude.setText(String.valueOf(intent.getDoubleExtra("longitude", 0.00)));
        zip_code.setText(intent.getStringExtra("zip_code"));
        mail.setText(intent.getStringExtra("mail"));
        nb_student.setText(String.valueOf(intent.getIntExtra("nb_student", 0)));
        city.setText(intent.getStringExtra("city"));
        openings.setText(intent.getStringExtra("openings"));
        phone.setText(intent.getStringExtra("phone"));

        // Crée un adapter que l'on insère dans le spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);
        if(intent.getStringExtra("status") .equals("publ")){
            status.setSelection(0);
        }
        else{
            status.setSelection(1);
        }
    }
}
