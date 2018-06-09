package com.ynov.bsc.Model;

/**
 * Created by Brice on 03/06/2018.
 */

public class School {
    private int id;
    private String nom;
    private String addresse;
    private double latitude;
    private double longitude;
    private String zip_code;
    private String mail;
    private int nb_student;
    private String city;
    private String openings;
    private String status;
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {

        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getAddresse() {
        return addresse;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getZip_code() {
        return zip_code;
    }

    public String getMail() {
        return mail;
    }

    public int getNb_student() {
        return nb_student;
    }

    public String getCity() {
        return city;
    }

    public String getOpenings() {
        return openings;
    }

    public String getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setAddresse(String addresse) {
        this.addresse = addresse;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setNb_student(int nb_student) {
        this.nb_student = nb_student;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setOpenings(String openings) {
        this.openings = openings;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public School(int id,
                  String nom,
                  String addresse,
                  double latitude,
                  double longitude,
                  String zip_code,
                  String mail,
                  int nb_student,
                  String city,
                  String openings,
                  String status,
                  String phone) {
        this.id = id;
        this.nom = nom;
        this.addresse = addresse;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zip_code = zip_code;
        this.mail = mail;
        this.nb_student = nb_student;
        this.city = city;
        this.openings = openings;
        this.status = status;
        this.phone = phone;
    }
}