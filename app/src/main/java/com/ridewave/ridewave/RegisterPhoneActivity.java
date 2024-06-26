package com.ridewave.ridewave;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.appz.abhi.simplebutton.SimpleButton;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

public class RegisterPhoneActivity extends AppCompatActivity {
    private SimpleButton submitB;
    private EditText pnumberT;
    private CountryCodePicker ccp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);
        getSupportActionBar().hide();

        //initialize views
        submitB = findViewById(R.id.submitB);
        pnumberT = findViewById(R.id.pnumberT);
        ccp = findViewById(R.id.ccp);
        //action
        submitB.setOnClickListener(view -> {
            if(pnumberT.getText().toString().trim().equals(""))
            {
                pnumberT.setError("This field cannot be empty!");
                pnumberT.requestFocus();
            }
            else
            {
                final String country_code = ccp.getSelectedCountryCode();
                final String pnumber = "+"+country_code+pnumberT.getText().toString().trim();
                Intent intent = new Intent(getApplicationContext(),PhoneVerificationActivity.class);
                intent.putExtra("pnumber",pnumber);
                startActivity(intent);

            }
        });

    }
}