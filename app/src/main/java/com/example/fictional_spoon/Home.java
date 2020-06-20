package com.example.fictional_spoon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Home extends AppCompatActivity {

    LinearLayout linearLayout1, linearLayout2;
    FirebaseAuth firebaseAuth;
    TextView signout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebaseAuth = FirebaseAuth.getInstance();

        linearLayout1 = findViewById(R.id.ll1_home);
        linearLayout2 = findViewById(R.id.ll2_home);
        signout = findViewById(R.id.signout_home);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignOut(v);
            }
        });

        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(getApplicationContext(), IdeaPad.class));
            }
        });

        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), My_Uploads.class));
            }
        });



    }

    public void create_idea(View view) {

    }

    public void SignOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getApplicationContext(), "Signing out", Toast.LENGTH_SHORT).show();
        finish();
    }
}
