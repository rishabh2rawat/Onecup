package com.rishabhrawat.fightforhunger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class distributerActivity extends AppCompatActivity {


    private List<donerdata> donerdataList;
    private doner_card_Adapter adapter;
    private LinearLayoutManager llm;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributer);


        RecyclerView rv = (RecyclerView)findViewById(R.id.Explore_RecyclerView);

        rv.setHasFixedSize(true);

        llm = new LinearLayoutManager(distributerActivity.this);
        rv.setLayoutManager(llm);


        donerdataList = new ArrayList<>();

        /*load data from firebase server*/

       /* reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
        for (int i = 0; i < 10; i++) {

            donerdata donerdata = new donerdata("https://firebasestorage.googleapis.com/v0/b/onecup-ace53.appspot.com/o/All_Image_Uploads%2F1.jpg?alt=media&token=c6f8bd13-80c2-444d-a8a8-947a9ae257fe","Rishabh rawat","9557624276","greater noida","50men servings");

            donerdataList.add(donerdata);

        }


        adapter = new doner_card_Adapter(donerdataList, distributerActivity.this);
        rv.setAdapter(adapter);


    }

}
