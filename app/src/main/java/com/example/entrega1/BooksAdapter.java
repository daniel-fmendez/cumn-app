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

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder>{

    private List<Book> datos;
    private BooksAdapter adapter;

    public BooksAdapter(ArrayList<Book> datos) {
        this.datos = datos;
    }

    @NonNull
    @Override
    public BooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(mView);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull BooksAdapter.ViewHolder holder, int position) {

        Book book = datos.get(position);
        holder.nombre.setText(book.title);
        if (book.coverUrl != null) {
            Log.d("COVER_URL", "Cargando imagen desde cache: " + book.coverUrl);

            Glide.with(holder.itemView.getContext())
                    .load(book.coverUrl)
                    .apply(new RequestOptions().override(600, 200))
                    .into(holder.cover);

        }
        // Configura el click listener para abrir la actividad de detalles
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), BookDetailActivity.class);
            intent.putExtra("key",book.key);
            intent.putExtra("edition_key",book.cover_edition_key);
            intent.putExtra("title", book.title);
            intent.putExtra("subtitle",book.subtitle);
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
        private TextView nombre;
        private ImageView cover;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            cover = itemView.findViewById(R.id.cover);
        }
    }
}