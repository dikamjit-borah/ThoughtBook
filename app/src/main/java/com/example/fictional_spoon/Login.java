package com.example.fictional_spoon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {
    Button login, get_otp;
    EditText phone, otp, name;

    String VERIFICATION_ID;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String USER_ID;

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null)
        {
            String USERID = firebaseAuth.getCurrentUser().getUid();
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
           /* DocumentReference documentReference = firestore.collection("USER_DETAILS").document(USERID);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    String USER_TYPE;
                    USER_TYPE = documentSnapshot.getString("USER_TYPE_");
                    // USER_TYPE = USER_TYPE==null?"null":USER_TYPE;
                    if (USER_TYPE.equals("TEACHER"))
                    {
                        startActivity(new Intent(getApplicationContext(), Dashboard_teachers.class));
                        finish();
                    }
                    else if (USER_TYPE.equals("STUDENT"))
                    {
                        startActivity(new Intent(getApplicationContext(), Dashboard_students.class));
                        finish();
                    }
                }
            });*/

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        phone = findViewById(R.id.editText_phone_login);
        otp = findViewById(R.id.editText_otp_login);
        name = findViewById(R.id.editText_name_login);


        get_otp = findViewById(R.id.button_otp_login);
        get_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phone.getText().toString().isEmpty() ||phone.getText().toString().length()!=10){
                    phone.setError("Phone number is incorrect or empty");
                    return;
                }
                else if(name.getText().toString().isEmpty()){
                    name.setError("Username is empty!!!");
                    return;
                }
                else
                {
//Toast.makeText(getApplicationContext(), otp.getText().toString(), Toast.LENGTH_SHORT).show();

                    final String phone_number = "+91" + phone.getText().toString();
                    requestOTP(phone_number) ;
                }

            }
        });

        login = findViewById(R.id.button_login_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString().isEmpty() || otp.getText().toString().length()!=6)
                    otp.setError("Invalid OTP");
                else
                {

                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(VERIFICATION_ID, otp.getText().toString());
                    verifyOTP(phoneAuthCredential);
                }



                //startActivity(new Intent(getApplicationContext(), Dashboard_students.class));
            }
        });
    }

    private void requestOTP(String phone_number) {

        PhoneAuthProvider phoneAuthProvider = PhoneAuthProvider.getInstance();
        phoneAuthProvider.verifyPhoneNumber(phone_number, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                VERIFICATION_ID = s;
                //USER_ID = firebaseAuth.getCurrentUser().getUid();


                Toast.makeText(getApplicationContext(), "OTP has been sent", Toast.LENGTH_SHORT).show();
                get_otp.setEnabled(false);
                otp.setVisibility(View.VISIBLE);
                login.setVisibility(View.VISIBLE);


            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(getApplicationContext(), "Timeout occurred. Try again after sometime", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                verifyOTP(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyOTP(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                   /* startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();*/
                   DocumentReference documentReference = firebaseFirestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid());
               /*     documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.exists())
                            {
                                startActivity(new Intent(getApplicationContext(), Home.class));
                                finish();
                            }
                        }
                    });*/

                    Map<String, Object> users = new HashMap<>();
                    users.put("NAME_",name.getText().toString());
                    users.put("PHONE_", phone.getText().toString());
                    documentReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), "User " + name.getText().toString()+ " created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Home.class));
                                finish();

                            }
                        }
                    });


                }
                   // Toast.makeText(getApplicationContext(), "Phone number verified", Toast.LENGTH_SHORT).show();
                else{
                    Toast.makeText(getApplicationContext(), "Cannot sign in at the moment ", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }
}
