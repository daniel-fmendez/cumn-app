package com.example.entrega1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {

    private List<Book> datos;

    public BooksAdapter(ArrayList<Book> datos) {
        this.datos = datos;
    }

    @NonNull
    @Override
    public BooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_book, parent, false);
        return new ViewHolder(mView);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull BooksAdapter.ViewHolder holder, int position) {

        Book book = datos.get(position);
        holder.titulo.setText(book.title);
        Log.e("Test",holder.titulo.getText().toString());
        if(!book.author_name.isEmpty()){
            holder.autor.setText(book.author_name.get(0));
        }

        if (book.coverUrl != null) {
            Log.d("COVER_URL", "Cargando imagen desde cache: " + book.coverUrl);

            Glide.with(holder.itemView.getContext())
                    .load(book.coverUrl)
                    .apply(new RequestOptions().override(600, 200))
                    .into(holder.cover);

        }
        // Configura el click listener para abrir la actividad de detalles
       holder.cover.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), BookDetailActivity.class);
            intent.putExtra("key", book.key);
            intent.putExtra("edition_key", book.cover_edition_key);
            intent.putExtra("title", book.title);
            intent.putExtra("subtitle", book.subtitle);
            intent.putStringArrayListExtra("authors", book.author_name);
            intent.putStringArrayListExtra("authors_keys", book.author_key);
            intent.putExtra("coverUrl", book.coverUrl);
            intent.putExtra("first_publish_year", book.first_publish_year);
            intent.putStringArrayListExtra("languages", new ArrayList<>(book.language));

            holder.itemView.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return datos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titulo;
        private TextView autor;
        private ImageView cover;
        private ImageView favorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.short_title); // Título del libro
            autor = itemView.findViewById(R.id.textView3);  // Autor del libro
            cover = itemView.findViewById(R.id.imageView2); // Imagen del libro
            favorite = itemView.findViewById(R.id.imageView3); // Icono de favorito
        }
    }
}