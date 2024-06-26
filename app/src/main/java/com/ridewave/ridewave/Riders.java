package com.ridewave.ridewave;

public class Riders {
    String rider_id;
    String fname;
    String lname;
    String email;
    String pnumber;
    String gender;
    String status;
    String lat;
    String lng;
    String firebase_key;
    String photo;
    String ref_code;
    String rider_rating;
    String date_x;
    String time_x;

    public Riders() {

    }

    public Riders(String rider_id, String fname, String lname, String email, String pnumber, String gender, String status, String lat, String lng, String firebase_key, String photo, String ref_code, String rider_rating, String date_x, String time_x) {
        this.rider_id = rider_id;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.pnumber = pnumber;
        this.gender = gender;
        this.status = status;
        this.lat = lat;
        this.lng = lng;
        this.firebase_key = firebase_key;
        this.photo = photo;
        this.ref_code = ref_code;
        this.rider_rating = rider_rating;
        this.date_x = date_x;
        this.time_x = time_x;
    }

    public String getRider_id() {
        return rider_id;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getEmail() {
        return email;
    }

    public String getPnumber() {
        return pnumber;
    }

    public String getGender() {
        return gender;
    }

    public String getStatus() {
        return status;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getFirebase_key() {
        return firebase_key;
    }

    public String getPhoto() {
        return photo;
    }

    public String getRef_code() {
        return ref_code;
    }

    public String getRider_rating() {
        return rider_rating;
    }

    public String getDate_x() {
        return date_x;
    }

    public String getTime_x() {
        return time_x;
    }
}
