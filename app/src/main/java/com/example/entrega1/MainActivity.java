package com.example.entrega1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<Book> datos = new ArrayList<>();
    private BooksAdapter adapter;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        RecyclerView lista = findViewById(R.id.lista_cards);
        SearchView searchView = findViewById(R.id.searchView);
        adapter = new BooksAdapter(datos);
        lista.setLayoutManager(new LinearLayoutManager(this));
        lista.setAdapter(adapter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configuración de Retrofit


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getBooks(query); // Lógica cuando el usuario pulsa "Buscar"
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }


        });

    }

    @SuppressLint("CheckResult")
    private void getBooks(String title) {
        if (title == null || title.trim().isEmpty()) {
            Log.e("ERROR", "El título no puede estar vacío");
            return;
        }

        datos.clear();  // Limpiar la lista sin eliminar la referencia

        String query = title.trim().replace(" ", "+");
        Log.e("QUERY", query);

        BooksDataBaseService service = RetrofitClient.getService();

        service.listBooks(query, 5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(response -> response.docs != null ? response.docs : new ArrayList<>())
                .filter(Objects::nonNull)
                .map(apiBook -> {
                    Book book = new Book();
                    book.author_key = apiBook.author_key != null ? new ArrayList<>(apiBook.author_key) : new ArrayList<>();
                    book.author_name = apiBook.author_name != null ? new ArrayList<>(apiBook.author_name) : new ArrayList<>();
                    book.cover_edition_key = apiBook.cover_edition_key != null ? apiBook.cover_edition_key : "";
                    book.cover_i = Optional.of(apiBook.cover_i).orElse(0);
                    book.coverUrl = (Objects.nonNull(apiBook.cover_i) && apiBook.cover_i > 0)
                            ? "https://covers.openlibrary.org/b/id/" + apiBook.cover_i + "-M.jpg"
                            : null;
                    book.edition_count = apiBook.edition_count;
                    book.first_publish_year = apiBook.first_publish_year;
                    book.has_fulltext = apiBook.has_fulltext;
                    book.ia = apiBook.ia != null ? new ArrayList<>(apiBook.ia) : new ArrayList<>();
                    book.ia_collection_s = apiBook.ia_collection_s != null ? apiBook.ia_collection_s : "";
                    book.key = apiBook.key != null ? apiBook.key : "";
                    book.language = apiBook.language != null ? new ArrayList<>(apiBook.language) : new ArrayList<>();
                    book.public_scan_b = apiBook.public_scan_b;
                    book.subtitle = apiBook.subtitle != null ? apiBook.subtitle : "";
                    book.title = apiBook.title != null ? apiBook.title : "";
                    book.lending_edition_s = apiBook.lending_edition_s != null ? apiBook.lending_edition_s : "";
                    book.lending_identifier_s = apiBook.lending_identifier_s != null ? apiBook.lending_identifier_s : "";

                    return book;
                })
                .toList()
                .subscribe(
                        bookList -> {
                            datos.addAll(bookList);
                            adapter.notifyDataSetChanged();
                        },
                        error -> Log.e("Error : ", error.toString())
                );
    }


}
