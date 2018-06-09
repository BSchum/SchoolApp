package com.ynov.bsc.Model;

/**
 * Created by Brice on 06/06/2018.
 */

public class UserCredentialsModel {
    public String email;
    public String password;

    public UserCredentialsModel(String login, String password) {
        this.email = login;
        this.password = password;
    }
}
