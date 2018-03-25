package com.example.shailesh.bloodbankapp.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.shailesh.bloodbankapp.MainActivity;
import com.example.shailesh.bloodbankapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;
    NavigationView navigationView;

    EditText email,password,confirmPassword;
    Button register;
    //private ProgressBar progressBar;
    //private ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar =(Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");

        firebaseAuth=FirebaseAuth.getInstance();

       // progressBar=(ProgressBar)findViewById(R.id.progressBar);
        email=(EditText)findViewById(R.id.input_username);
        password=(EditText)findViewById(R.id.input_password);
        confirmPassword=(EditText)findViewById(R.id.input_password_confirm);
        register=(Button)findViewById(R.id.button_register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail=email.getText().toString().trim();
                String userPass=password.getText().toString().trim();
                String userCOnfirmpass=confirmPassword.getText().toString().trim();

                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(getApplicationContext(),"enter email to register",Toast.LENGTH_SHORT).show();
                    //stoping the further execution
                    return;
                }
                if(TextUtils.isEmpty(userPass)){
                    Toast.makeText(getApplicationContext(),"enter password",Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(userEmail,userPass);
                Toast.makeText(getApplicationContext(),"Registered Successfuly",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));


            }
        });


        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.nav_login:
                Toast.makeText(Register.this,"login",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Register.this,LoginActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
