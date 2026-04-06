package com.example.idvault;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileInputStream;

public class CardDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#0D0F1A"));
        root.setGravity(Gravity.CENTER);
        root.setPadding(24, 0, 24, 0);

        String file = getIntent().getStringExtra("file");
        String cardName = getIntent().getStringExtra("name");

        TextView title = new TextView(this);
        title.setText(cardName != null ? cardName : "Card");
        title.setTextColor(Color.parseColor("#D4AF37"));
        title.setTextSize(22f);
        title.setTypeface(android.graphics.Typeface.SERIF,
                android.graphics.Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 32);

        ImageView img = new ImageView(this);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);

        TextView hint = new TextView(this);
        hint.setText("Tap anywhere to close");
        hint.setTextColor(Color.parseColor("#555B75"));
        hint.setTextSize(12f);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, 24, 0, 0);

        try {
            FileInputStream fis = openFileInput(file);
            img.setImageBitmap(BitmapFactory.decodeStream(fis));
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        root.addView(title);
        root.addView(img);
        root.addView(hint);
        setContentView(root);

        root.setOnClickListener(v -> finish());
    }
}