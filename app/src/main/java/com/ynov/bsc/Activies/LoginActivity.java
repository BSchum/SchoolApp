package com.ynov.bsc.Activies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.brice.messagemanager.R;
import com.google.gson.JsonObject;
import com.ynov.bsc.Model.UserCredentialsModel;
import com.ynov.bsc.Services.ISchoolController;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    TextView login;
    TextView password;
    CheckBox rememberme;
    Button empty;
    Button validate;
    SharedPreferences prefs;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onResume() {
        super.onResume();
        validate.setEnabled(true);
    }
    public void FillInput(){
        if(prefs.getString("login", null) != null && prefs.getString("password", null) != null){
            login.setText(prefs.getString("login", null));
            password.setText(prefs.getString("password", null));
            rememberme.setChecked(true);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Recuperation de tout les inputs
        login = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
        rememberme = findViewById(R.id.input_rememberme);
        empty = findViewById(R.id.btn_empty);
        validate = findViewById(R.id.btn_validate);

        // Mise en place du listener
        validate.setOnClickListener(genericOnClickListener);
        empty.setOnClickListener(genericOnClickListener);
        rememberme.setOnClickListener(genericOnClickListener);

        // Instanciation des preferences et de sont editeur
        prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        prefEditor = prefs.edit();

        FillInput();
    }

    View.OnClickListener genericOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_validate:
                    validate.setEnabled(false);
                    if(TextUtils.isEmpty(login.getText())){
                        login.setError("login obligatoire");
                    }

                    if(TextUtils.isEmpty(password.getText())){
                        password.setError("password obligatoire");
                    }
                    if(TextUtils.isEmpty(login.getText()) && TextUtils.isEmpty(password.getText())) {
                        validate.setEnabled(true);
                    }
                    if(!TextUtils.isEmpty(login.getText()) && !TextUtils.isEmpty(password.getText())) {

                        UserCredentialsModel userCredentialsModel = new UserCredentialsModel(login.getText().toString(), password.getText().toString());
                        // On crée l'interface pour se connecter
                        ISchoolController iSchoolController = new Retrofit.Builder()
                                .baseUrl(ISchoolController.ENDPOINT)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                                .create(ISchoolController.class);

                        // On authentifie le user
                        iSchoolController.authentication(userCredentialsModel).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.errorBody() == null && response.body().get("success").toString().equals("true")) {
                                    String auth = response.body().get("auth_token").toString();
                                    prefEditor.putString("auth_token", auth)
                                            .putString("login", login.getText().toString())
                                            .putString("password", password.getText().toString())
                                            .apply();

                                    Intent toMenu = new Intent(LoginActivity.this, MenuActivity.class);
                                    startActivity(toMenu);
                                }
                                else{
                                    validate.setEnabled(true);
                                }
                            }
                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        });

                    }
                    break;
                case R.id.btn_empty:
                    //On vide les champs
                    login.setText(null);
                    password.setText(null);
                    validate.setEnabled(true);

                    break;
                case R.id.input_rememberme:
                    // Si on uncheck rememberme, on supprime les SharedPreferences.
                    validate.setEnabled(true);

                    if(!rememberme.isChecked()){
                        prefEditor.remove("login")
                                .remove("password").apply();
                    }
                default:
                    break;
            }
        }
    };


}
