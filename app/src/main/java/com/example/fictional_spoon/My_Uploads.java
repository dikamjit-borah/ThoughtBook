package com.example.fictional_spoon;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

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
ImageView refresh;
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
       refresh.setVisibility(View.VISIBLE);
       refresh.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Toast.makeText(My_Uploads.this, "Too fast!Try again", Toast.LENGTH_SHORT).show();
               firebaseFirestore.collection("ALL_IDEAS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {

                           String single_idea_text, single_idea_image;
                           idea_text.clear();
                           idea_image.clear();
                           for (QueryDocumentSnapshot document : task.getResult()) {
                               single_idea_text = document.getString("IDEA_TEXT");
                               single_idea_image = document.getString("IDEA_IMAGE");
                               idea_text.add(single_idea_text);
                               idea_image.add(single_idea_image);
                           }
                   }
               });
               ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_list_item_1, idea_text);
               listView.setAdapter(adapter);
               listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       Uri uri = Uri.parse( idea_image.get(position));
                       Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                       startActivity(intent);
                   }
               });
               if(!idea_text.isEmpty())
                   refresh.setVisibility(View.INVISIBLE);
           }
       });






    }
}