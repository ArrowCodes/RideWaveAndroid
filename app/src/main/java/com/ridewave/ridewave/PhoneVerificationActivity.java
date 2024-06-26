package com.ridewave.ridewave;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class PhoneVerificationActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    //private EditText code_et;
    private TextView prompt_text, resend_otpTV;
    private Button resend_codeB, confirmB;
    private String pnumber, code, message_x, country_code;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private PinView code_et;
    DatabaseReference databaseReference;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        getSupportActionBar().hide();
        //initializing objects
        mAuth = FirebaseAuth.getInstance();
        //initialize firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference("riders");

        //Initialize Views
        progressBar = findViewById(R.id.progressbar);
        confirmB = findViewById(R.id.buttonSubmit);
        code_et = findViewById(R.id.pinView);
        prompt_text = findViewById(R.id.prompt_text);

        //initialize views
        resend_otpTV = findViewById(R.id.resend_otpTV);

        //Intent get intent
        Intent intent = getIntent();
        pnumber = intent.getStringExtra("pnumber");
        country_code = intent.getStringExtra("country_code");
        prompt_text.setText("Please enter the 6 digit SMS code we sent to the phone number" + " " + pnumber);
        //pass parameter to function
        sendVerificationCode(pnumber);
        resend_otpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(pnumber);
            }
        });


        confirmB.setOnClickListener(view -> confirm_code(pnumber));

    }

    //Handled manually

    private void confirm_code(final String pnumber) {
        String code = code_et.getText().toString();
        if (code.isEmpty() || code.length() < 6) {
            code_et.setError("Wrong OTP...");
            code_et.requestFocus();
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE);
            verifyCode(code);
        }


    }

    //Handled manually
    private void verifyCode(String codeByUser) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, codeByUser);
        signInTheUserByCredentials(credential);

    }

    //Handled manually
    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            //Toast.makeText(VerifyPhoneActivity.this, "Your Account has been created successfully!", Toast.LENGTH_SHORT).show();

                            //Perform Your required action here to either let the user sign In or do something required
                            verify(pnumber);
                            Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
                            i.putExtra("pnumber",pnumber);
                            startActivity(i);
                            Toasty.info(getApplicationContext(), "Success!", Toast.LENGTH_LONG, false).show();


                        } else {
                            Toast.makeText(PhoneVerificationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                PhoneVerificationActivity.this,
                mCallbacks);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            message_x = code;
            code_et.setText(code);
            Toasty.info(getApplicationContext(), code, Toast.LENGTH_LONG, false).show();
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                code_et.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneVerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            verify(pnumber);
                            Toasty.info(getApplicationContext(), "Success!", Toast.LENGTH_LONG, false).show();

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }
    private void verify(final String pnumber)
    {
        databaseReference.orderByChild("pnumber").equalTo(pnumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Phone number exists, get rider details
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Riders rider = snapshot.getValue(Riders.class);
                        if (rider != null) {
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(rider.rider_id,rider.fname,rider.lname,rider.email,pnumber,rider.status);
                            Intent intent = new Intent(PhoneVerificationActivity.this,LoadingActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {
                    Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
                    i.putExtra("pnumber",pnumber);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Toast.makeText(PhoneVerificationActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}