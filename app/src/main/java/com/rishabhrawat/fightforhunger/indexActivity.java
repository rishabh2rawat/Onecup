package com.rishabhrawat.fightforhunger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class indexActivity extends AppCompatActivity {

    TextView donate;
    TextView distribute;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);


        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(indexActivity.this, registerActivity.class));
                    finish();
                }
            }
        };

        /*linking to the xml file------------*/
        donate=(TextView) findViewById(R.id.doner);
        distribute=(TextView)findViewById(R.id.distribute);






        /*------------doner  onclick function-----------------*/
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(indexActivity.this,donerActivity.class);
                startActivity(intent);
            }
        });





        /*-----------------distribute onclick function-------------------------*/
        distribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(indexActivity.this,distributerActivity.class);
                startActivity(intent);
            }
        });
    }
}
