package com.example.idvault;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ProfileFragment extends Fragment {

    TextView statTotal, statCategories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        statTotal = view.findViewById(R.id.statTotal);
        statCategories = view.findViewById(R.id.statCategories);

        loadStats();

        // Change PIN
        view.findViewById(R.id.changePinBtn).setOnClickListener(v -> changePin());

        // Delete all
        view.findViewById(R.id.deleteAllBtn).setOnClickListener(v -> deleteAll());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
    }

    private void loadStats() {
        ArrayList<CardModel> list = new ArrayList<>();
        Set<String> categories = new HashSet<>();

        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(requireActivity().openFileInput("cards.txt")));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    list.add(new CardModel(parts[0], parts[1]));
                    categories.add(getCategory(parts[0]));
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        statTotal.setText(String.valueOf(list.size()));
        statCategories.setText(String.valueOf(categories.size()));
    }

    private String getCategory(String name) {
        String n = name.toLowerCase();
        if (n.contains("aadhar") || n.contains("pan") || n.contains("passport")
                || n.contains("voter") || n.contains("driving")) return "Government";
        if (n.contains("college") || n.contains("school") || n.contains("student")
                || n.contains("library")) return "Education";
        if (n.contains("bank") || n.contains("credit") || n.contains("debit")
                || n.contains("atm")) return "Finance";
        return "Other";
    }

    private void changePin() {
        View pinView = LayoutInflater.from(getContext())
                .inflate(android.R.layout.simple_list_item_1, null);

        EditText currentPin = new EditText(requireContext());
        currentPin.setHint("Current PIN");
        currentPin.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        currentPin.setTextColor(0xFFFFFFFF);

        EditText newPin = new EditText(requireContext());
        newPin.setHint("New PIN");
        newPin.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        newPin.setTextColor(0xFFFFFFFF);

        android.widget.LinearLayout layout = new android.widget.LinearLayout(requireContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 16, 48, 0);
        layout.addView(currentPin);
        layout.addView(newPin);

        new AlertDialog.Builder(requireContext())
                .setTitle("Change PIN")
                .setView(layout)
                .setPositiveButton("Update", (d, w) -> {
                    SharedPreferences prefs = requireActivity()
                            .getSharedPreferences("idvault", Context.MODE_PRIVATE);
                    String savedPin = prefs.getString("pin", "1234");

                    if (!currentPin.getText().toString().equals(savedPin)) {
                        Toast.makeText(getContext(), "Current PIN is wrong ❌",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String np = newPin.getText().toString().trim();
                    if (np.length() < 4) {
                        Toast.makeText(getContext(), "PIN must be at least 4 digits",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    prefs.edit().putString("pin", np).apply();
                    Toast.makeText(getContext(), "PIN updated ✅", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAll() {
        new AlertDialog.Builder(requireContext())
                .setTitle("⚠️ Delete All Cards")
                .setMessage("This will permanently delete ALL cards. This cannot be undone.")
                .setPositiveButton("Delete All", (d, w) -> {
                    try {
                        ArrayList<CardModel> list = new ArrayList<>();
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(
                                        requireActivity().openFileInput("cards.txt")));
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] parts = line.split(",");
                            if (parts.length == 2) list.add(new CardModel(parts[0], parts[1]));
                        }
                        br.close();

                        for (CardModel m : list) requireActivity().deleteFile(m.fileName);

                        FileOutputStream fos = requireActivity()
                                .openFileOutput("cards.txt", Context.MODE_PRIVATE);
                        fos.write("".getBytes());
                        fos.close();

                        Toast.makeText(getContext(), "All cards deleted 🗑️",
                                Toast.LENGTH_SHORT).show();
                        loadStats();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}