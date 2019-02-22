package com.example.osku.fuksipassi;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toolbar;

/**
 * Created by Osku on 9.8.2018.
 */

public class InstructionsActivity extends AppCompatActivity {
    ImageView pedagoLogo;
    boolean imageClicked = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        pedagoLogo = findViewById(R.id.pedago_logo);
       android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Imagebutton onClick listener for dimming the logo if the text overlaps the logo.
        pedagoLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!imageClicked){
                    pedagoLogo.setAlpha(0.5f);
                    imageClicked = true;
                } else{
                    pedagoLogo.setAlpha(1f);
                    imageClicked = false;
                }

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
