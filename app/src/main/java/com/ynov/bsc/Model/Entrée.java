package com.ynov.bsc.Model;

/**
 * Created by Brice on 26/03/2018.
 */

public class Entrée{
    private String nom;
    private String sexe;
    private String description;
    private String espece;
    private int id;

    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEspece(String espece) {
        this.espece = espece;
    }


    public int getId() {

        return id;
    }

    public String getSexe() {
        return sexe;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public String getEspece() {
        return espece;
    }

    public Entrée(String nom, String sexe, String description, String espece, int id) {

        this.nom = nom;
        this.sexe = sexe;
        this.description = description;
        this.espece = espece;
        this.id = id;
    }
}
