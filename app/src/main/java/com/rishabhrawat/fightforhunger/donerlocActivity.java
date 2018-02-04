package com.rishabhrawat.fightforhunger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class donerlocActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donerloc);

        Toast.makeText(this, "all the data sucsessfully uploaded to firebase", Toast.LENGTH_SHORT).show();


    }
}
