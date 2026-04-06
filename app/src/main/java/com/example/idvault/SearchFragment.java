package com.example.idvault;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SearchFragment extends Fragment {

    RecyclerView recyclerView;
    EditText searchInput;
    ArrayList<CardModel> allCards = new ArrayList<>();
    ArrayList<CardModel> filteredCards = new ArrayList<>();
    CardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.searchRecycler);
        searchInput = view.findViewById(R.id.searchInput);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        loadAllCards();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCards(s.toString());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllCards();
    }

    private void loadAllCards() {
        allCards.clear();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(requireActivity().openFileInput("cards.txt")));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) allCards.add(new CardModel(parts[0], parts[1]));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        filteredCards = new ArrayList<>(allCards);
        adapter = new CardAdapter(getContext(), filteredCards, null);
        recyclerView.setAdapter(adapter);
    }

    private void filterCards(String query) {
        filteredCards.clear();
        for (CardModel m : allCards) {
            if (m.name.toLowerCase().contains(query.toLowerCase())) {
                filteredCards.add(m);
            }
        }
        adapter.notifyDataSetChanged();
    }
}