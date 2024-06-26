package com.ridewave.ridewave;

public class Trips {
    String trip_id;
    String trip_unique_key;
    String pick_up;
    String destination;
    String pick_lat;
    String pick_lng;
    String dest_lat;
    String dest_lng;
    String distance;
    String duration;
    String distance_number;
    String rider;
    String driver;
    String total_fare;
    String driver_distance;
    String status;
    String text_date;
    String date_x;
    String sys_time;
    String payment_method;
    String picked_at;
    String dropped_at;

    public Trips() {

    }

    public Trips(String trip_id, String trip_unique_key, String pick_up, String destination, String pick_lat, String pick_lng, String dest_lat, String dest_lng, String distance, String duration, String distance_number, String rider, String driver, String total_fare, String driver_distance, String status, String text_date, String date_x, String sys_time, String payment_method, String picked_at, String dropped_at) {
        this.trip_id = trip_id;
        this.trip_unique_key = trip_unique_key;
        this.pick_up = pick_up;
        this.destination = destination;
        this.pick_lat = pick_lat;
        this.pick_lng = pick_lng;
        this.dest_lat = dest_lat;
        this.dest_lng = dest_lng;
        this.distance = distance;
        this.duration = duration;
        this.distance_number = distance_number;
        this.rider = rider;
        this.driver = driver;
        this.total_fare = total_fare;
        this.driver_distance = driver_distance;
        this.status = status;
        this.text_date = text_date;
        this.date_x = date_x;
        this.sys_time = sys_time;
        this.payment_method = payment_method;
        this.picked_at = picked_at;
        this.dropped_at = dropped_at;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public String getTrip_unique_key() {
        return trip_unique_key;
    }

    public String getPick_up() {
        return pick_up;
    }

    public String getDestination() {
        return destination;
    }

    public String getPick_lat() {
        return pick_lat;
    }

    public String getPick_lng() {
        return pick_lng;
    }

    public String getDest_lat() {
        return dest_lat;
    }

    public String getDest_lng() {
        return dest_lng;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public String getDistance_number() {
        return distance_number;
    }

    public String getRider() {
        return rider;
    }

    public String getDriver() {
        return driver;
    }

    public String getTotal_fare() {
        return total_fare;
    }

    public String getDriver_distance() {
        return driver_distance;
    }

    public String getStatus() {
        return status;
    }

    public String getText_date() {
        return text_date;
    }

    public String getDate_x() {
        return date_x;
    }

    public String getSys_time() {
        return sys_time;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public String getPicked_at() {
        return picked_at;
    }

    public String getDropped_at() {
        return dropped_at;
    }
}
