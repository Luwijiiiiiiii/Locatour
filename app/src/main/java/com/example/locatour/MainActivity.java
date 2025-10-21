package com.example.locatour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.github.muddz.styleabletoast.StyleableToast;

public class MainActivity extends AppCompatActivity {
    ImageButton HomeBtn;
    ImageButton MapsBtn;
    ImageButton SearchBtn;
    ImageButton locationsBtn;
    ImageButton PersonBtn;

    //event
    ImageButton eventpop;
    ImageButton event_parade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        HomeBtn = findViewById(R.id.HomeBtn);
        HomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StyleableToast.makeText(MainActivity.this, "Already at Home Page", R.style.homeToast).show();
            }
        });
        MapsBtn = findViewById(R.id.MapsBtn);
        MapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StyleableToast.makeText(MainActivity.this, "Development Underway, feature coming soon!", R.style.comingSoon).show();
            }
        });
        PersonBtn  = findViewById(R.id.PersonBtn);
        PersonBtn.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                StyleableToast.makeText(MainActivity.this, "Welcome to Profile!", R.style.comingSoon).show();
            }

        });
        // Event
        eventpop = findViewById(R.id.eventpop);
        eventpop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StyleableToast.makeText(MainActivity.this, "Development Underway, feature coming soon!", R.style.comingSoon).show();
            }
        });

        event_parade = findViewById(R.id.event_parade);
        event_parade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StyleableToast.makeText(MainActivity.this, "Development Underway, feature coming soon!", R.style.comingSoon).show();
            }
        });
        // endEvent
    }
}