package com.ridewave.ridewave;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appz.abhi.simplebutton.SimpleButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {
    private EditText fnameT,lnameT,emailT,pnumberT,referralT;
    private Button registerB;
    private String ref_code,pnumber,status,firebase_key;
    private ImageView imageView;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGE_PICK_CAMERA_REQUEST = 300;
    private static final int IMAGE_PICK_GALLERY_REQUEST = 400;
    Uri imageUri;
    String cameraPermission[];
    String storagePermission[];
    private RadioGroup radioGroup;
    String gender;
    private static final int REQUEST_ENABLE_GPS = 1001;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        //initialize firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference("riders");
        // Initialize permissions
        cameraPermission = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        // Find the RadioGroup
        radioGroup = findViewById(R.id.radioGroupGender);

        // Set an OnCheckedChangeListener to the RadioGroup
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Find which radio button is selected by ID
            RadioButton radioButton = findViewById(checkedId);

            // Display a toast message with the selected radio button's text
            if (radioButton != null) {
                gender = radioButton.getText().toString();
                Toast.makeText(RegisterActivity.this, "Selected gender: " + gender, Toast.LENGTH_SHORT).show();
            }
        });
        //intent
        Intent intent = getIntent();
        //initialize views
        fnameT = findViewById(R.id.fnameT);
        lnameT = findViewById(R.id.lnameT);
        emailT = findViewById(R.id.emailT);
        referralT = findViewById(R.id.referralT);
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePicDialog();
            }
        });
        registerB = findViewById(R.id.registerB);
        //action
        registerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerB.setEnabled(false);
                register();
            }
        });

        ref_code = "N";
    }

    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    private Boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    private Boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    private void pickFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_REQUEST);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //easyLocation.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_GALLERY_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                startCropImageActivity(imageUri);
            }
        } else if (requestCode == IMAGE_PICK_CAMERA_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageUri = getImageUri(imageBitmap);
                startCropImageActivity(imageUri);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Picasso.get().load(resultUri).into(imageView);
                //upload_photo();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                // Handle crop error
                Toast.makeText(this, "Crop failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setAspectRatio(1, 1) // set your desired aspect ratio
                .start(this);
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void register() {

        if(fnameT.getText().toString().equals(""))
        {
            fnameT.setError("This field is required");
            fnameT.requestFocus();
        }
        else if(lnameT.getText().toString().equals(""))
        {
            lnameT.setError("This field is required");
            lnameT.requestFocus();
        }
        else if(emailT.getText().toString().equals(""))
        {
            emailT.setError("This field is required");
            emailT.requestFocus();
        }

        else if(gender==null)
        {
            Toasty.info(getApplicationContext(),"Gender cannot be empty",Toast.LENGTH_LONG,false).show();
        }
        else
        {
            final String fname = fnameT.getText().toString().trim().toUpperCase();
            final String lname = lnameT.getText().toString().trim().toUpperCase();
            final String email = emailT.getText().toString().trim();
            Calendar calendar = Calendar.getInstance();
            GetTimeData getTimeData = new GetTimeData(calendar);
            final String text_date = getTimeData.getTextDate();
            final String sysTime = getTimeData.getHour();
            final String sysDate = getTimeData.getSysDate();
            String rider_id = databaseReference.push().getKey();
            ref_code = referralT.getText().toString().trim().toUpperCase();
            Intent intent = getIntent();
            pnumber = intent.getStringExtra("pnumber");
            status = "Active";
            firebase_key = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
            //Initialize Firebase storage
            Riders rider = new Riders(rider_id, fname, lname, email, pnumber, gender, status,"","",firebase_key,"",ref_code,"",sysDate,sysTime);
            //check if phone number exists
            databaseReference.orderByChild("pnumber").equalTo(pnumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Phone number already exists
                        Toast.makeText(RegisterActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                    } else {

                        databaseReference.child(rider_id).setValue(rider).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(rider_id,fname,lname,email,pnumber,status);
                                Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this,LoadingActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "User registration failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors.
                    Toast.makeText(RegisterActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



        }

    }
}