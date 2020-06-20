package com.example.fictional_spoon;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class My_Uploads extends AppCompatActivity {

RecyclerView recyclerView;
FirebaseStorage firebaseStorage;
ListView listView;
Button refresh;
ArrayList<String> idea_text = new ArrayList<>();
ArrayList<String> idea_image = new ArrayList<>();
FirebaseFirestore firebaseFirestore;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__uploads);


       /* recyclerView = findViewById(R.id.recyclerView_mu);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));*/
       listView = findViewById(R.id.listView_mu);
       firebaseFirestore = FirebaseFirestore.getInstance();
       refresh = findViewById(R.id.refresh_mu);
       refresh.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               firebaseFirestore.collection("ALL_IDEAS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {

                           String single_idea_text, single_idea_image;
                           idea_text.clear();
                           for (QueryDocumentSnapshot document : task.getResult()) {
                               single_idea_text = document.getString("IDEA_TEXT");
                               single_idea_image = document.getString("IDEA_IMAGE");
                               idea_text.add(single_idea_text);
                           }
                   }
               });
               ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_list_item_1, idea_text);
               listView.setAdapter(adapter);
           }
       });






    }
}