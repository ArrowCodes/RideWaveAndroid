package com.ridewave.ridewave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterSignActivity extends AppCompatActivity {
    private Button registerB,sign_inB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sign);
        getSupportActionBar().hide();

        if(SharedPrefManager.getInstance(this).isLoggedIn())
        {
            Intent myIntent = new Intent(getApplicationContext(), LoadingActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(myIntent);
            finish();
        }

        //initialize views
        registerB = findViewById(R.id.registerB);
        sign_inB = findViewById(R.id.sign_inB);
        registerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),RegisterPhoneActivity.class);
                startActivity(i);
            }
        });

        sign_inB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),RegisterPhoneActivity.class);
                startActivity(i);
                finish();
            }
        });
    }


}