package com.example.idvault;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.result.*;
import androidx.activity.result.contract.*;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;

public class AddCardActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() != null) {
                        try {
                            Uri uri = result.getData().getData();
                            InputStream is = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);

                            String fileName = "card_" + System.currentTimeMillis() + ".jpg";
                            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();

                            askName(fileName);
                        } catch (Exception e) {
                            e.printStackTrace();
                            finish();
                        }
                    } else {
                        finish();
                    }
                }
        );

        openGallery();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    private void askName(String fileName) {
        EditText input = new EditText(this);
        input.setHint("e.g. Aadhar Card, College ID");

        new AlertDialog.Builder(this)
                .setTitle("Card Name")
                .setView(input)
                .setPositiveButton("Save", (d, w) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) name = "My Card";
                    try {
                        FileOutputStream fos = openFileOutput("cards.txt", MODE_APPEND);
                        fos.write((name + "," + fileName + "\n").getBytes());
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
                })
                .setNegativeButton("Cancel", (d, w) -> finish())
                .setCancelable(false)
                .show();
    }
}