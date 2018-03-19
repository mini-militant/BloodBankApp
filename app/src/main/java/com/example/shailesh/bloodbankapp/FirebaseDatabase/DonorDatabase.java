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
    private EditText name,surname,address,additionalData;
    private Spinner gender,bloodType,rhFactor;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Displaying Toolbar icon
        Toolbar toolbar=(Toolbar)findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Donor Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        name=(EditText)findViewById(R.id.input_name);
        surname=(EditText)findViewById(R.id.input_surname);
        address=(EditText)findViewById(R.id.input_address);
        additionalData=(EditText)findViewById(R.id.input_additional);

        gender=(Spinner)findViewById(R.id.spinner_sex);

        bloodType=(Spinner)findViewById(R.id.spinner_blood_type);
        btnSave=(Button)findViewById(R.id.button_save);

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
                String Firstname =name.getText().toString();
                String Lastname = surname.getText().toString();
                String Address=address.getText().toString();
                String additional=additionalData.getText().toString();

                String blood=(String) bloodType.getSelectedItem();

                String sex=(String) gender.getSelectedItem();

                // Check for already existed userId
                if (TextUtils.isEmpty(userId)) {
                    createUser(Firstname,Lastname,sex,Address,blood,additional);
                } else {
                    updateUser(Firstname,Lastname,sex,Address,blood,additional);
                }
            }
        });

        toggleButton();
    }

    // Changing button text
    private void toggleButton() {
        if (TextUtils.isEmpty(userId)) {
            btnSave.setText("Save");
        } else {
            btnSave.setText("Update");
        }
    }

    private void createUser(String Firstname,String Lastname,String gender,String Address,String bloodType,String additional) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }

        User user = new User(Firstname,Lastname,gender,Address,bloodType,additional);

        mFirebaseDatabase.child(userId).setValue(user);

        addUserChangeListener();
    }

    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.name + ", " + user.surname + ", " + user.gender + ", "
                        + user.address + ", " +","+user.gender+ "," + user.additionalData);



                // clear edit text
                name.setText("");
               surname.setText("");
                additionalData.setText("");
                address.setText("");

                toggleButton();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }
    private void updateUser(String Firstname,String Lastname,String gender,String Address,String bloodType,String additional) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(Firstname))
            mFirebaseDatabase.child(userId).child("Firstname").setValue(Firstname);

        if (!TextUtils.isEmpty(Lastname))
            mFirebaseDatabase.child(userId).child("Lastname").setValue(Lastname);
        if (!TextUtils.isEmpty(gender))
            mFirebaseDatabase.child(userId).child("gender").setValue(gender);
        if (!TextUtils.isEmpty(Address))
            mFirebaseDatabase.child(userId).child("Address").setValue(Address);
        if (!TextUtils.isEmpty(bloodType))
            mFirebaseDatabase.child(userId).child("bloodType").setValue(bloodType);


        if (!TextUtils.isEmpty(additional))
            mFirebaseDatabase.child(userId).child("additional").setValue(additional);
    }
}
