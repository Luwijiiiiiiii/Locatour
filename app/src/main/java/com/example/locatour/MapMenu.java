package com.example.locatour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.github.muddz.styleabletoast.StyleableToast;

public class MapMenu extends AppCompatActivity {
    ImageButton BurnhamBTN;
    ImageButton ToBotanicalBTN;
    ImageButton BotanicalBTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        BurnhamBTN = findViewById(R.id.BurnhamBTN);
        BurnhamBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapMenu.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        ToBotanicalBTN = findViewById(R.id.ToBotanicalBTN);
        ToBotanicalBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapMenu.this, Direction.class);
                startActivity(intent);
            }
        });
        BotanicalBTN = findViewById(R.id.BotanicalBTN);
        BotanicalBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapMenu.this, MinesView.class);
                startActivity(intent);
            }
        });
    }
}