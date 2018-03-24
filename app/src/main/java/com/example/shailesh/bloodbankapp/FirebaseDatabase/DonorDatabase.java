package com.example.shailesh.bloodbankapp.FirebaseDatabase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.shailesh.bloodbankapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by shailesh on 10/3/18.
 */

public class DonorDatabase extends AppCompatActivity {

    private static final String TAG = DonorDatabase.class.getSimpleName();
    private EditText name, donorAddress, additionalData, phoneNo;
    private Spinner gender, bloodType;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private String authenticatedConnectionIdentifier;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Displaying Toolbar icon
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Donor Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        name = (EditText) findViewById(R.id.input_surname);
        donorAddress = (EditText) findViewById(R.id.input_address);
        additionalData = (EditText) findViewById(R.id.input_additional);
        phoneNo = (EditText) findViewById(R.id.input_phone);

        gender = (Spinner) findViewById(R.id.spinner_sex);

        bloodType = (Spinner) findViewById(R.id.spinner_blood_type);
        btnSave = (Button) findViewById(R.id.button_save);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Realtime Database");

        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });
        // Save / update the user
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String donorAddress = DonorDatabase.this.donorAddress.getText().toString();
                String name = DonorDatabase.this.name.getText().toString();
                String additionalData = DonorDatabase.this.additionalData.getText().toString();
                String phoneNo = DonorDatabase.this.phoneNo.getText().toString();

                String bloodType = (String) DonorDatabase.this.bloodType.getSelectedItem();

                String gender = (String) DonorDatabase.this.gender.getSelectedItem();

                // Check for already existed authenticatedConnectionIdentifier
                if (TextUtils.isEmpty(authenticatedConnectionIdentifier)) {
                    createUser(name, gender, donorAddress, bloodType, additionalData, phoneNo);
                } else {
                    updateUser(name, gender, donorAddress, bloodType, additionalData, phoneNo);
                }
            }
        });

        toggleButton();
    }

    // Changing button text
    private void toggleButton() {
        if (TextUtils.isEmpty(authenticatedConnectionIdentifier)) {
            btnSave.setText("Save");
        } else {
            btnSave.setText("Update");
        }
    }

    private void createUser(String name, String gender, String donorAddress, String bloodType, String additionalData, String phoneNo) {

        if (TextUtils.isEmpty(authenticatedConnectionIdentifier)) {
            authenticatedConnectionIdentifier = mFirebaseDatabase.push().getKey();
        }

        User user = new User(name, gender, donorAddress, bloodType, additionalData, phoneNo);

        mFirebaseDatabase.child(authenticatedConnectionIdentifier).setValue(user);

        addUserChangeListener();
    }

    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(authenticatedConnectionIdentifier).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.donorAddress + ", " + user.phoneNo + ", " + user.name + ", " + user.gender + ", " + "," + user.bloodType + "," + user.additionalData);


                // clear edit text
                donorAddress.setText("");
                name.setText("");
                additionalData.setText("");
                donorAddress.setText("");

                toggleButton();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    private void updateUser(String name, String gender, String donorAddress, String bloodType, String additionalData, String phoneNo) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(name))
            mFirebaseDatabase.child(authenticatedConnectionIdentifier).child("name").setValue(name);
        if (!TextUtils.isEmpty(gender))
            mFirebaseDatabase.child(authenticatedConnectionIdentifier).child("gender").setValue(gender);
        if (!TextUtils.isEmpty(donorAddress))
            mFirebaseDatabase.child(authenticatedConnectionIdentifier).child("donorAddress").setValue(donorAddress);
        if (!TextUtils.isEmpty(bloodType))
            mFirebaseDatabase.child(authenticatedConnectionIdentifier).child("bloodType").setValue(bloodType);
        if (!TextUtils.isEmpty(additionalData))
            mFirebaseDatabase.child(authenticatedConnectionIdentifier).child("additionalData").setValue(additionalData);
        if (!TextUtils.isEmpty(phoneNo))
            mFirebaseDatabase.child(authenticatedConnectionIdentifier).child("phoneNo").setValue(phoneNo);
    }
}
