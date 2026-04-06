package com.example.idvault;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.*;
import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    Context context;
    ArrayList<CardModel> list;
    HomeFragment homeFragment; // null when used in search

    public CardAdapter(Context context, ArrayList<CardModel> list, HomeFragment fragment) {
        this.context = context;
        this.list = list;
        this.homeFragment = fragment;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img, icon;
        TextView name, share;

        public ViewHolder(View v) {
            super(v);
            img = v.findViewById(R.id.cardImg);
            icon = v.findViewById(R.id.iconImg);
            name = v.findViewById(R.id.cardName);
            share = v.findViewById(R.id.shareBtn);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        CardModel m = list.get(position);
        h.name.setText(m.name);

        h.itemView.setAlpha(0f);
        h.itemView.animate().alpha(1f).setDuration(400)
                .setStartDelay(position * 60L).start();

        try {
            FileInputStream fis = context.openFileInput(m.fileName);
            h.img.setImageBitmap(BitmapFactory.decodeStream(fis));
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Smart icon
        String nameLower = m.name.toLowerCase();
        if (nameLower.contains("aadhar") || nameLower.contains("aadhaar")) {
            h.icon.setImageResource(R.drawable.ic_aadhar);
        } else if (nameLower.contains("college") || nameLower.contains("school")
                || nameLower.contains("student")) {
            h.icon.setImageResource(R.drawable.ic_college);
        } else {
            h.icon.setImageResource(R.drawable.ic_default);
        }

        // Tap → fullscreen
        h.img.setOnClickListener(v -> {
            Intent intent = new Intent(context, CardDetailActivity.class);
            intent.putExtra("file", m.fileName);
            intent.putExtra("name", m.name);
            context.startActivity(intent);
        });

        // Long press → options menu
        h.itemView.setOnLongClickListener(v -> {
            if (homeFragment == null) return true;
            int pos = h.getAdapterPosition();
            new AlertDialog.Builder(context)
                    .setTitle(m.name)
                    .setItems(new String[]{"✏️ Rename", "🗑️ Delete", "📤 Share"},
                            (d, which) -> {
                                if (which == 0) homeFragment.renameCard(pos);
                                else if (which == 1) homeFragment.confirmDelete(pos);
                                else shareCard(m);
                            })
                    .show();
            return true;
        });

        // Share button
        h.share.setOnClickListener(v -> shareCard(m));
    }

    private void shareCard(CardModel m) {
        try {
            File file = new File(context.getFilesDir(), m.fileName);
            Uri uri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".provider", file);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, "Share " + m.name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() { return list.size(); }
}