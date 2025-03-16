package com.example.entrega1;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.entrega1.Connection.WorksByAuthor.Entry;

import java.util.ArrayList;
import java.util.List;

public class ShortWorksAdapter extends RecyclerView.Adapter<ShortWorksAdapter.ViewHolder> {

    private List<Entry> datos;

    public ShortWorksAdapter(ArrayList<Entry> datos) {
        this.datos = datos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.short_work, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entry entry = datos.get(position);
        holder.titulo.setText(entry.title);

    }

    @Override
    public int getItemCount() {
        return  datos.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titulo;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            titulo = itemView.findViewById(R.id.short_title);
        }
    }
}
