package com.ridewave.ridewave;

public class VehicleTypes {
    String vehicle_type_id;
    String vehicle_type;
    String base_rate;
    String rate_per_km;
    String capacity;
    String date_x;
    String time_x;

    public VehicleTypes() {

    }

    public VehicleTypes(String vehicle_type_id, String vehicle_type, String base_rate, String rate_per_km, String capacity, String date_x, String time_x) {
        this.vehicle_type_id = vehicle_type_id;
        this.vehicle_type = vehicle_type;
        this.base_rate = base_rate;
        this.rate_per_km = rate_per_km;
        this.capacity = capacity;
        this.date_x = date_x;
        this.time_x = time_x;
    }

    public String getVehicle_type_id() {
        return vehicle_type_id;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public String getBase_rate() {
        return base_rate;
    }

    public String getRate_per_km() {
        return rate_per_km;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getDate_x() {
        return date_x;
    }

    public String getTime_x() {
        return time_x;
    }
}
