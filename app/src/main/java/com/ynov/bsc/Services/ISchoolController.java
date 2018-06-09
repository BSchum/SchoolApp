package com.ynov.bsc.Services;
import com.google.gson.JsonObject;
import com.ynov.bsc.Model.School;
import com.ynov.bsc.Model.UserCredentialsModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ISchoolController
{
    String ENDPOINT = "https://mbi-school-api.herokuapp.com";

    @POST("/api/v1/users/sign_in")
    Call<JsonObject> authentication(@Body UserCredentialsModel userCredentials);

    @GET("/api/v1/schools")
    Call<JsonObject> getSchool(@Query("status") String status);

    @POST("/api/v1/schools")
    Call<JsonObject> saveSchool(@Body JsonObject school);

    @DELETE("/api/v1/schools/{id}")
    Call<JsonObject> removeSchool(@Path("id") int id);

    @PATCH("/api/v1/schools/{id}")
    Call<JsonObject> updateSchool(@Path("id") int id,@Body JsonObject school);

}