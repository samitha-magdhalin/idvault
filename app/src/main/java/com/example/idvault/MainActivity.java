package com.example.idvault;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText pinInput;
    Button unlockBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pinInput = findViewById(R.id.pinInput);
        unlockBtn = findViewById(R.id.unlockBtn);

        unlockBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("idvault", Context.MODE_PRIVATE);
            String savedPin = prefs.getString("pin", "1234");

            if (pinInput.getText().toString().equals(savedPin)) {
                startActivity(new Intent(this, MainNavActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Wrong PIN ❌", Toast.LENGTH_SHORT).show();
                pinInput.setText("");
            }
        });
    }
}