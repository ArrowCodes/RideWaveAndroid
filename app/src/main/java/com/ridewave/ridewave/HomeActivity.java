package com.ridewave.ridewave;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maps.route.RouteDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.UUID;

import at.markushi.ui.CircleButton;
import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback{
    LinearLayout linear_header, linear_pay,linear_current_location,linear_enter_location,linear_drag_map;
    private TextView name, email;
    private Timer timer_dest,timer_dest_one,timer_driver;
    ImageView imageView;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private List<LatLng> routePoints;
    private Polyline routePolyline;
    private LatLng origin = new LatLng(-0.547751134111909, 36.94687148123339); // Origin coordinates
    private LatLng destination = new LatLng(-0.5780721815447247, 36.94221516633116); // Destination coordinates
    private RouteDrawer mRouteDrawer;

    private static final String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json";
    private ImageView plus_origin_imageview, plus_destination_imageview;
    MarkerOptions my_origin, my_destination;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int AUTOCOMPLETE_REQUEST_CODE_TWO = 2;
    private TextView pick_up_locationTV, drop_off_locationTV;
    private String pickup_location, dropoff_location;
    private String trip_id, x_fare, van_fare, go_fare, total_fare;
    private LatLng selected_origin;
    private LatLng selected_destination;
    LinearLayout bottom_sheet_initial;
    BottomSheetBehavior sheetBehavior_initial;
    EasyWayLocation easyWayLocation;
    //Global Strings
    private String pickup, requested_by, trip_distance, drop_off, trip_duration, distance_numeric;
    //Bottom sheet initials
    private TextView pickup_initial_tv, requested_by_initial_tv, distance_initial_tv, drop_initial_tv;
    private Button confirm_trip_initialB, cancel_trip_initialB;
    //Bottom sheet vehicle
    LinearLayout bottom_sheet_vehicles, pickup_dialog,where_dialog;
    BottomSheetBehavior sheetBehavior_vehicles;
    private TextView go_txt,ratingLabel,tripTV, go_fare_txt, x_txt, x_fare_txt, van_txt, van_fare_txt, choose_ride_tv;
    private LinearLayout linear_go, linear_x, linear_van;
    private Button confirm_vehicleB;
    private String vehicle_type_id, trip_unique_key,trips;
    private ProgressBar progressBarSearch;
    private CardView map_card_view;
    private double origin_latitude, origin_longitude, destination_latitude, destination_longitude;
    private Handler mHandler = new Handler();
    private Handler handler_draw_map = new Handler();
    //tracking trip bottom sheet
    private String trip_id_track, trip_unique_key_track, destination_track, distance, duration, distance_number, rider, driver;
    private String total_fare_track, driver_distance, status, pick_up, text_date, date_x, sys_time, fname, lname, email_track, gender;
    private String pnumber, photo, pick_lat, pick_lng, dest_lat, dest_lng, driver_lat, driver_lng;
    private TextView date_tv, vehicle_details_tv, fare_tv, pnumber_tv, distance_tv, pick_up_tv, requested_tv, pickup_distance_tv, drop_off_tv, name_tv;
    private ImageView imageView_track;
    private Button pickB, confirm_payB;
    private LinearLayout bottom_sheet_trip;
    private BottomSheetBehavior sheetBehavior, sheetBehaviorPickLocation,sheetBehaviorWhere;
    private CircleButton call_button, sms_button;
    private RatingBar ratingBar;
    private EditText reviewT,edit_text_search;
    private RelativeLayout relative_pick, relative_drop;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private LinearLayout top_sheet;
    private Animation slideUpAnimation;
    private Animation slideDownAnimation;
    private View up_view;
    private DatabaseReference tripsDatabaseReference,vehiclesDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //initialize databases
        tripsDatabaseReference = FirebaseDatabase.getInstance().getReference("trips");
        vehiclesDatabaseReference = FirebaseDatabase.getInstance().getReference("vehicle_types");
        //initialize map card view
        map_card_view = findViewById(R.id.map_card_view);
        slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Check GPS permissions
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            // Permission granted, check if GPS is enabled
            //checkGPSStatus();
        }

        timer_dest = new Timer();
        timer_driver = new Timer();
        timer_dest_one = new Timer();
        //start location foreground service
        //startForegroundService(new Intent(this, LocationForegroundService.class));
        //bottom sheet where
        where_dialog = findViewById(R.id.where_dialog);
        edit_text_search = where_dialog.findViewById(R.id.edit_text_search);
        sheetBehaviorWhere = BottomSheetBehavior.from(where_dialog);
        sheetBehaviorWhere.setPeekHeight(300); // Set your desired peek height in pixels
        sheetBehaviorWhere.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehaviorWhere.setDraggable(true);
        edit_text_search.setInputType(InputType.TYPE_NULL);
        up_view = findViewById(R.id.up_view);
        // Create a SpannableString with the hint text
        SpannableString spannableString = new SpannableString("Where to?");
        // Apply the bold style to the hint text
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Set the bold hint to the EditText
        edit_text_search.setHint(spannableString);
        edit_text_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the top sheet
                map_card_view.setVisibility(View.VISIBLE);
                map_card_view.startAnimation(slideDownAnimation);

                // Slide up the bottom sheet
                where_dialog.startAnimation(slideUpAnimation);
                where_dialog.setVisibility(View.GONE);
            }
        });

        up_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the top sheet
                map_card_view.setVisibility(View.VISIBLE);
                map_card_view.startAnimation(slideDownAnimation);

                // Slide up the bottom sheet
                where_dialog.startAnimation(slideUpAnimation);
                where_dialog.setVisibility(View.GONE);
            }
        });






        //bottom sheet choose pick
        pickup_dialog = findViewById(R.id.pickup_dialog);
        sheetBehaviorPickLocation = BottomSheetBehavior.from(pickup_dialog);
        sheetBehaviorPickLocation.setState(BottomSheetBehavior.STATE_COLLAPSED);
        linear_current_location = pickup_dialog.findViewById(R.id.linear_current_location);
        linear_enter_location = pickup_dialog.findViewById(R.id.linear_enter_location);
        linear_drag_map = pickup_dialog.findViewById(R.id.linear_drag_map);
        //bottom sheet
        bottom_sheet_trip = findViewById(R.id.bottom_sheet_trip);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet_trip);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        linear_pay = bottom_sheet_trip.findViewById(R.id.linear_pay);
        date_tv = bottom_sheet_trip.findViewById(R.id.date_tv);
        fare_tv = bottom_sheet_trip.findViewById(R.id.fare_tv);
        distance_tv = bottom_sheet_trip.findViewById(R.id.distance_tv);
        pick_up_tv = bottom_sheet_trip.findViewById(R.id.pick_up_tv);
        requested_tv = bottom_sheet_trip.findViewById(R.id.requested_tv);
        pickup_distance_tv = bottom_sheet_trip.findViewById(R.id.pickup_distance_tv);
        drop_off_tv = bottom_sheet_trip.findViewById(R.id.drop_off_tv);
        pnumber_tv = bottom_sheet_trip.findViewById(R.id.pnumber_tv);
        name_tv = bottom_sheet_trip.findViewById(R.id.name_tv);
        tripTV = bottom_sheet_trip.findViewById(R.id.tripTV);
        ratingLabel = bottom_sheet_trip.findViewById(R.id.ratingLabel);
        vehicle_details_tv = bottom_sheet_trip.findViewById(R.id.vehicle_details_tv);
        imageView_track = bottom_sheet_trip.findViewById(R.id.imageView_track);
        //ratingBar = bottom_sheet_trip.findViewById(R.id.ratingBar);
        //reviewT = bottom_sheet_trip.findViewById(R.id.reviewT);
        pickB = bottom_sheet_trip.findViewById(R.id.pickB);
        confirm_payB = bottom_sheet_trip.findViewById(R.id.confirm_payB);
        //call_button,sms_button
        call_button = findViewById(R.id.call_button);
        sms_button = findViewById(R.id.sms_button);
        call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + pnumber));
                startActivity(intent);
            }
        });
        sms_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace "message" with the content of your SMS
                String message = "Write a text....";
                // Create an Intent with the ACTION_SENDTO action and the SMS Uri
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + pnumber));  // This ensures only SMS apps respond
                // Add the SMS body
                intent.putExtra("sms_body", message);
                // Start the activity
                startActivity(intent);
            }
        });


        //bottom sheet vehicles
        bottom_sheet_vehicles = findViewById(R.id.bottom_sheet_vehicles);
        sheetBehavior_vehicles = BottomSheetBehavior.from(bottom_sheet_vehicles);
        sheetBehavior_vehicles.setState(BottomSheetBehavior.STATE_COLLAPSED);
        go_txt = findViewById(R.id.go_txt);
        go_fare_txt = findViewById(R.id.go_fare_txt);
        x_txt = findViewById(R.id.x_txt);
        x_fare_txt = findViewById(R.id.x_fare_txt);
        van_txt = findViewById(R.id.van_txt);
        van_fare_txt = findViewById(R.id.van_fare_txt);
        linear_go = findViewById(R.id.linear_go);
        linear_x = findViewById(R.id.linear_x);
        linear_van = findViewById(R.id.linear_van);
        progressBarSearch = findViewById(R.id.progressBarSearch);
        choose_ride_tv = findViewById(R.id.choose_ride_tv);
        confirm_vehicleB = findViewById(R.id.confirm_vehicleB);
        confirm_vehicleB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //confirm_vehicle();
            }
        });

        linear_go.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                linear_go.setBackgroundColor(getResources().getColor(R.color.selected_radio_color));
                linear_x.setBackgroundColor(Color.TRANSPARENT);
                linear_van.setBackgroundColor(Color.TRANSPARENT);
                go_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
                go_fare_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
                x_txt.setTextColor(Color.BLACK);
                x_fare_txt.setTextColor(Color.BLACK);
                van_txt.setTextColor(Color.BLACK);
                van_fare_txt.setTextColor(Color.BLACK);
                confirm_vehicleB.setText("Confirm Go");
                vehicle_type_id = "1";
                total_fare = go_fare;
                //Toasty.info(getApplicationContext(),total_fare,Toast.LENGTH_LONG,false).show();
            }
        });
        linear_x.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                linear_x.setBackgroundColor(getResources().getColor(R.color.selected_radio_color));
                linear_go.setBackgroundColor(Color.TRANSPARENT);
                linear_van.setBackgroundColor(Color.TRANSPARENT);
                go_txt.setTextColor(Color.BLACK);
                go_fare_txt.setTextColor(Color.BLACK);
                x_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
                x_fare_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
                van_txt.setTextColor(Color.BLACK);
                van_fare_txt.setTextColor(Color.BLACK);
                confirm_vehicleB.setText("Confirm X");
                vehicle_type_id = "2";
                total_fare = x_fare;
                //Toasty.info(getApplicationContext(),total_fare,Toast.LENGTH_LONG,false).show();
            }
        });
        linear_van.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                linear_van.setBackgroundColor(getResources().getColor(R.color.selected_radio_color));
                linear_go.setBackgroundColor(Color.TRANSPARENT);
                linear_x.setBackgroundColor(Color.TRANSPARENT);
                //changeBorderColor(linear_van);
                go_txt.setTextColor(Color.BLACK);
                go_fare_txt.setTextColor(Color.BLACK);
                x_txt.setTextColor(Color.BLACK);
                x_fare_txt.setTextColor(Color.BLACK);
                van_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
                van_fare_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
                confirm_vehicleB.setText("Confirm Boda");
                vehicle_type_id = "3";
                total_fare = van_fare;
                //Toasty.info(getApplicationContext(),total_fare,Toast.LENGTH_LONG,false).show();
            }
        });
        //bottom sheet initial
        bottom_sheet_initial = findViewById(R.id.bottom_sheet_initial);
        sheetBehavior_initial = BottomSheetBehavior.from(bottom_sheet_initial);
        sheetBehavior_initial.setState(BottomSheetBehavior.STATE_COLLAPSED);
        pickup_initial_tv = bottom_sheet_initial.findViewById(R.id.pickup_initial_tv);
        requested_by_initial_tv = bottom_sheet_initial.findViewById(R.id.requested_by_initial_tv);
        distance_initial_tv = bottom_sheet_initial.findViewById(R.id.distance_initial_tv);
        drop_initial_tv = bottom_sheet_initial.findViewById(R.id.drop_initial_tv);
        confirm_trip_initialB = bottom_sheet_initial.findViewById(R.id.confirm_trip_initialB);
        cancel_trip_initialB = bottom_sheet_initial.findViewById(R.id.cancel_trip_initialB);
        cancel_trip_initialB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cancel_initial_request();

            }
        });
        confirm_trip_initialB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm_trip_initialB.setEnabled(false);
                confirm_initial_request();
            }
        });

        confirm_payB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pay_via_app();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        Places.initialize(getApplicationContext(), getString(R.string.api_key));

        //initialize
        pick_up_locationTV = findViewById(R.id.pick_up_locationTV);
        drop_off_locationTV = findViewById(R.id.drop_off_locationTV);



        mapFragment.getMapAsync(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Book A Ride");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        name = (TextView) header.findViewById(R.id.header_title);
        name.setText(SharedPrefManager.getInstance(getApplicationContext()).getKeyUserFname() + " " + SharedPrefManager.getInstance(getApplicationContext()).getKeyUserLname());
        email = header.findViewById(R.id.sub_title);
        email.setText(SharedPrefManager.getInstance(getApplicationContext()).getKeyUserEmail());
        imageView = (ImageView) header.findViewById(R.id.imageView);
        linear_header = (LinearLayout) header.findViewById(R.id.linear_header);
        linear_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        //Setting marker to draw route between these two points
        my_origin = new MarkerOptions().position(new LatLng(-0.547751134111909, 36.94687148123339)).title("HSR Layout").snippet("origin");
        my_destination = new MarkerOptions().position(new LatLng(-0.5780721815447247, 36.94221516633116)).title("Bellandur").snippet("destination");

        relative_pick = findViewById(R.id.relative_pick);
        relative_drop = findViewById(R.id.relative_drop);
        plus_origin_imageview = findViewById(R.id.plus_origin_imageview);
        plus_destination_imageview = findViewById(R.id.plus_destination_imageview);
        //easyWayLocation = new EasyWayLocation(this, false, false, this);
        linear_drag_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehaviorPickLocation.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mMap.clear();
                zoomToCurrentLocation();
                Toasty.info(getApplicationContext(),"Long press on the map icon to start dragging",Toast.LENGTH_LONG,false).show();
            }
        });
        linear_enter_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehaviorPickLocation.setState(BottomSheetBehavior.STATE_COLLAPSED);
                plus_origin_imageview.setImageResource(R.drawable.location_off_icon);
                startPlaceAutocomplete();
            }
        });
        linear_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                sheetBehaviorPickLocation.setState(BottomSheetBehavior.STATE_COLLAPSED);
                zoom_my_location();
            }
        });
        plus_origin_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehaviorPickLocation.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            }
        });

        plus_destination_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plus_destination_imageview.setImageResource(R.drawable.location_off_icon);
                startPlaceAutocompleteTwo();
            }
        });
        //relative layout action
        relative_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehaviorPickLocation.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            }
        });

        relative_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plus_destination_imageview.setImageResource(R.drawable.location_off_icon);
                startPlaceAutocompleteTwo();
            }
        });
    }

    private void confirm_initial_request()
    {
        Calendar calendar = Calendar.getInstance();
        GetTimeData getTimeData = new GetTimeData(calendar);
        String trip_id = tripsDatabaseReference.push().getKey();
        String rider = SharedPrefManager.getInstance(getApplicationContext()).getKeyUserId();
        final String text_date = getTimeData.getTextDate();
        final String time_x = getTimeData.getHour();
        final String date_x = getTimeData.getSysDate();
        String trip_unique_key_x = UUID.randomUUID().toString().replace("-", "");
        //trips
        Trips trip = new Trips(trip_id,trip_unique_key_x,pickup,drop_off,String.valueOf(origin_latitude),String.valueOf(origin_longitude),String.valueOf(destination_latitude),String.valueOf(destination_longitude),trip_distance,trip_duration,distance_numeric,rider,"","",distance_numeric,"requested",text_date,date_x,time_x,"","","");
        tripsDatabaseReference.child(trip_id).setValue(trip).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                sheetBehavior_initial.setState(BottomSheetBehavior.STATE_COLLAPSED);
                trip_unique_key = trip_unique_key_x;
                 snip_vehicles(distance_numeric);
            } else {
                Toast.makeText(HomeActivity.this, "Trip initialization failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void snip_vehicles(String trip_distance)
    {
        double calc_distance = Double.valueOf(trip_distance);
        x_fare = String.valueOf(200 + calc_distance * 20);
        van_fare = String.valueOf(300 + calc_distance * 30);
        go_fare = String.valueOf(200 + calc_distance * 20);
        go_fare_txt.setText("Kshs."+go_fare);
        x_fare_txt.setText("Kshs."+x_fare);
        van_fare_txt.setText("Kshs."+van_fare);
        sheetBehavior_vehicles.setState(BottomSheetBehavior.STATE_EXPANDED);
    }





    private void startPlaceAutocomplete() {
        List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void zoom_my_location()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Get the address corresponding to the latitude and longitude
                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                if (!addresses.isEmpty()) {
                                    String address = addresses.get(0).getAddressLine(0);
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName();
                                    pick_up_locationTV.setText(address);
                                    pickup_initial_tv.setText(address);
                                    pickup = address;
                                    pick_up = address;
                                    Toasty.info(getApplicationContext(),address,Toast.LENGTH_LONG,false).show();
                                    // Use the retrieved address as needed
                                    Log.d("Address", "Address: " + address);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            LatLng currentLocation = new LatLng(latitude, longitude);
                            origin_latitude = currentLocation.latitude;
                            origin_longitude = currentLocation.longitude;
                            selected_origin = new LatLng(origin_latitude, origin_longitude);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 18);
                            mMap.moveCamera(cameraUpdate);

                            // Create a custom marker
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pinhundred);

                            // Add custom marker at current location
                            mMap.addMarker(new MarkerOptions()
                                    .position(currentLocation)
                                    .title("Current Location")
                                    .draggable(true)
                                    .icon(icon));
                        }
                    });
        }
    }


    private void startPlaceAutocompleteTwo() {
        List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_TWO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {


            if (resultCode == RESULT_OK) {
                plus_origin_imageview.setImageResource(R.drawable.add_grey_ic);
                Place place = Autocomplete.getPlaceFromIntent(data);
                pick_up_locationTV.setText(place.getName());
                pickup_location = place.getName();
                pickup = place.getName();
                LatLng selectedLatLng = place.getLatLng();
                origin_latitude = selectedLatLng.latitude;
                origin_longitude = selectedLatLng.longitude;
                selected_origin = new LatLng(origin_latitude, origin_longitude);
                position_map_pickup(origin_latitude, origin_longitude);
                //Toasty.info(getApplicationContext(), String.valueOf(selected_origin), Toast.LENGTH_LONG, false).show();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                // Handle the error
            } else if (resultCode == RESULT_CANCELED) {
                // User canceled the operation
            }
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_TWO) {
            if (resultCode == RESULT_OK) {
                plus_destination_imageview.setImageResource(R.drawable.add_grey_ic);
                Place place_two = Autocomplete.getPlaceFromIntent(data);
                drop_off_locationTV.setText(place_two.getName());
                dropoff_location = place_two.getName();
                drop_off = place_two.getName();
                LatLng d_selectedLatLng = place_two.getLatLng();
                destination_latitude = d_selectedLatLng.latitude;
                destination_longitude = d_selectedLatLng.longitude;
                selected_destination = new LatLng(destination_latitude, destination_longitude);

                getRoute(selected_origin, selected_destination);
                //textView.setText(place.getName());
                // You can use other place details like address, latitude, longitude as needed.
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                // Handle the error
            } else if (resultCode == RESULT_CANCELED) {
                // User canceled the operation
            }
        }
    }

    private void position_map_pickup(double origin_latitude, double origin_longitude) {
        LatLng origin = new LatLng(origin_latitude, origin_longitude);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pinblackonehundred);
        mMap.addMarker(new MarkerOptions().position(origin).icon(icon).title("Pick Up"));
        float desiredZoomLevel = 14f; // Adjust as needed
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(origin, desiredZoomLevel);
        mMap.animateCamera(cameraUpdate);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            /*Intent i = new Intent(getApplicationContext(), MyRidesActivity.class);
            startActivity(i);*/
        }  else if (id == R.id.nav_my_notifications) {
            //sheetBehaviorPickLocation.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);



        } else if (id == R.id.nav_sos_contacts) {
            Intent i = new Intent(getApplicationContext(), RatingActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_profile) {
            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            SharedPrefManager.getInstance(getApplicationContext()).logout();
            Intent intent = new Intent(HomeActivity.this, RegisterSignActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Where you initialize your GoogleMap instance, set the custom info window adapter
        //mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));

        //mMap.clear();
        // Example: Draw a route between two points
        LatLng origin = new LatLng(-0.547751134111909, 36.94687148123339); // Origin coordinates
        LatLng destination = new LatLng(-0.5780721815447247, 36.94221516633116); // Destination coordinates


        // Load the custom map style
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            zoomToCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Get route and draw polyline
        //getRoute(origin, destination);
    }

    private void zoomToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 18);
                            mMap.moveCamera(cameraUpdate);

                            // Create a custom marker
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pincurrentlocation);

                            // Add custom marker at current location
                            mMap.addMarker(new MarkerOptions()
                                    .position(currentLocation)
                                    .title("Current Location")
                                    .draggable(true)
                                    .icon(icon));

                            // Set a drag listener for the marker
                            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                @Override
                                public void onMarkerDragStart(Marker marker) {
                                    // Handle drag start if needed
                                    //Toasty.info(getApplicationContext(),"Map dragged",Toast.LENGTH_LONG,false).show();
                                }

                                @Override
                                public void onMarkerDrag(Marker marker) {
                                    // Handle dragging if needed
                                }

                                @Override
                                public void onMarkerDragEnd(Marker marker) {
                                    // Update the title of the marker to dragged location
                                    marker.setTitle("Dragged Location");

                                    // Retrieve latitude and longitude of the dragged location
                                    LatLng draggedLocation = marker.getPosition();
                                    double draggedLatitude = draggedLocation.latitude;
                                    double draggedLongitude = draggedLocation.longitude;

                                    origin_latitude = draggedLocation.latitude;
                                    origin_longitude = draggedLocation.longitude;
                                    selected_origin = new LatLng(origin_latitude, origin_longitude);

                                    // Perform reverse geocoding to get the location name
                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(draggedLatitude, draggedLongitude, 1);
                                        if (!addresses.isEmpty()) {
                                            String locationName = addresses.get(0).getAddressLine(0);
                                            // Update the marker's title with the location name
                                            marker.setTitle(locationName);
                                            pick_up_locationTV.setText(locationName);
                                            pickup = locationName;
                                            pick_up = locationName;
                                            // Optionally, perform any other actions with the location name
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    // Optionally, perform any other actions with the dragged location
                                }
                            });

                            // Enable zoom controls and my location button if necessary
                            mMap.getUiSettings().setZoomControlsEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);

                            // Optionally update your location using your custom method
                            //update_my_location(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                        } else {
                            //checkGPSStatus();
                            Toast.makeText(this, "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }




    // Inside the getRoute() method

    private void getRoute(LatLng origin, LatLng destination) {
        String YOUR_API_KEY = "AIzaSyA6hYnSKAaLYCGom8zX5XG5BhiT2_KMx60";
        String url = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&key=" + YOUR_API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // Parse the JSON response and extract polyline points
                    List<LatLng> points = parsePolylinePoints(response);

                    // Draw polyline on map
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(points)
                            .color(Color.BLACK)  // Set polyline color
                            .width(6);        // Set polyline width
                    mMap.addPolyline(polylineOptions);
                    // Set camera to focus on the origin with a specific zoom level

                    // Define the custom icon
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pineighty);
                    BitmapDescriptor destination_icon = BitmapDescriptorFactory.fromResource(R.drawable.locationpoint);

// Place the custom icon at the specified location
                    mMap.addMarker(new MarkerOptions()
                            .position(origin)  // Set the position where the icon will be placed
                            .icon(icon).title("Pickup Location"));  // Set the custom icon

                    mMap.addMarker(new MarkerOptions().position(destination).icon(destination_icon).title("Destination"));
                    float desiredZoomLevel = 14f; // Adjust as needed
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(origin, desiredZoomLevel);
                    mMap.animateCamera(cameraUpdate);


                    // Parse distance and duration
                    try {
                        JSONArray routes = response.getJSONArray("routes");
                        JSONObject route = routes.getJSONObject(0);
                        JSONArray legs = route.getJSONArray("legs");
                        JSONObject leg = legs.getJSONObject(0);

                        // Distance
                        JSONObject distance = leg.getJSONObject("distance");
                        String distanceText = distance.getString("text");
                        trip_distance = distanceText;
                        Log.d("Distance", "Distance: " + distanceText);
                        //Toasty.info(getApplicationContext(), distanceText, Toast.LENGTH_LONG, false).show();
                        String numericPart = distanceText.replaceAll("[^0-9.]", "");
                        double distance_double = Double.parseDouble(numericPart);
                        distance_numeric = String.valueOf(distance_double);
                        //Toasty.info(getApplicationContext(), distance_numeric, Toast.LENGTH_LONG, false).show();
                        // Duration
                        JSONObject duration = leg.getJSONObject("duration");
                        String durationText = duration.getString("text");
                        Log.d("Duration", "Duration: " + durationText);
                        trip_duration = durationText;
                        //Toasty.info(getApplicationContext(), durationText, Toast.LENGTH_LONG, false).show();
                        pop_initial_bottom_sheet();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle error
                    Log.e("DirectionsAPI", "Error fetching directions: " + error.getMessage());
                });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(request);
    }

    private List<LatLng> parsePolylinePoints(JSONObject response) {
        List<LatLng> points = new ArrayList<>();
        try {
            JSONArray routes = response.getJSONArray("routes");
            JSONObject route = routes.getJSONObject(0);
            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
            String encodedPolyline = overviewPolyline.getString("points");
            points = decodePolyline(encodedPolyline);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return points;
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            double latitude = lat / 1E5;
            double longitude = lng / 1E5;
            poly.add(new LatLng(latitude, longitude));
        }
        return poly;


    }
    @SuppressLint("SetTextI18n")
    private void pop_initial_bottom_sheet() {
        pickup_initial_tv.setText(pickup);
        requested_by_initial_tv.setText("Requested By:" + SharedPrefManager.getInstance(getApplicationContext()).getKeyUserFname() + " " + SharedPrefManager.getInstance(getApplicationContext()).getKeyUserLname());
        drop_initial_tv.setText(drop_off);
        distance_initial_tv.setText(trip_distance + "(" + trip_duration + ")");
        drop_initial_tv.setText(drop_off);
        sheetBehavior_initial = BottomSheetBehavior.from(bottom_sheet_initial);
        sheetBehavior_initial.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

}