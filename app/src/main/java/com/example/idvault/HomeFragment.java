package com.example.idvault;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    TextView cardCountText;
    LinearLayout emptyState;
    ArrayList<CardModel> list;
    CardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        cardCountText = view.findViewById(R.id.cardCountText);
        emptyState = view.findViewById(R.id.emptyState);

        view.findViewById(R.id.addBtn).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AddCardActivity.class))
        );

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder t) { return false; }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                confirmDelete(pos);
            }
        }).attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    public void loadData() {
        list = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(requireActivity().openFileInput("cards.txt")));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) list.add(new CardModel(parts[0], parts[1]));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new CardAdapter(getContext(), list, this);
        recyclerView.setAdapter(adapter);
        updateUI();
    }

    public void updateUI() {
        int count = list.size();
        cardCountText.setText(count + (count == 1 ? " card stored" : " cards stored"));
        if (count == 0) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void confirmDelete(int pos) {
        if (pos < 0 || pos >= list.size()) {
            adapter.notifyDataSetChanged();
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Card")
                .setMessage("Delete \"" + list.get(pos).name + "\"?")
                .setPositiveButton("Delete", (d, w) -> deleteCard(pos))
                .setNegativeButton("Cancel", (d, w) -> adapter.notifyDataSetChanged())
                .setOnCancelListener(d -> adapter.notifyDataSetChanged())
                .show();
    }

    public void deleteCard(int pos) {
        try {
            requireActivity().deleteFile(list.get(pos).fileName);
            list.remove(pos);
            adapter.notifyItemRemoved(pos);
            rewriteCards();
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renameCard(int pos) {
        android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setText(list.get(pos).name);
        input.setTextColor(0xFFFFFFFF);
        input.setBackground(null);

        new AlertDialog.Builder(requireContext())
                .setTitle("Rename Card")
                .setView(input)
                .setPositiveButton("Save", (d, w) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        list.get(pos).name = newName;
                        adapter.notifyItemChanged(pos);
                        rewriteCards();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void rewriteCards() {
        try {
            FileOutputStream fos = requireActivity()
                    .openFileOutput("cards.txt", android.content.Context.MODE_PRIVATE);
            for (CardModel m : list) {
                fos.write((m.name + "," + m.fileName + "\n").getBytes());
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}