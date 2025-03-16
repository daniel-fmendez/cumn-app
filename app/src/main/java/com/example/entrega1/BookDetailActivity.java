package com.example.entrega1;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import com.example.entrega1.databinding.BookDetailBinding;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookDetailActivity extends AppCompatActivity {

    private BookDetailBinding binding;

    @SuppressLint({"SetTextI18n", "CheckResult"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = BookDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Bindings
        {
            binding.sinopsisText.setVisibility(View.GONE);
            binding.sinopsisBox.setVisibility(View.GONE);
            binding.sinopsisTitle.setVisibility(View.GONE);

            binding.pagesText.setVisibility(View.GONE);
            binding.pagesTitle.setVisibility(View.GONE);

            binding.isbnTitle.setVisibility(View.GONE);
            binding.isbnText.setVisibility(View.GONE);

            binding.editorialTitle.setVisibility(View.GONE);
            binding.editorialText.setVisibility(View.GONE);

            binding.publicationText.setVisibility(View.GONE);
            binding.publicationTitle.setVisibility(View.GONE);

            binding.detailsBox.setVisibility(View.GONE);
        }
        binding.arrowBack.setOnClickListener(v -> finish());
        //API PART

        String key = getIntent().getStringExtra("key").replace("/works/", "");
        String editionKey = getIntent().getStringExtra("edition_key");

        //String
        String title = getIntent().getStringExtra("title") != null ? getIntent().getStringExtra("title") : "Título no disponible";
        String subtitle = getIntent().getStringExtra("subtitle") != null ? getIntent().getStringExtra("subtitle") : null;
        ArrayList<String> authors = getIntent().getStringArrayListExtra("authors") != null ? getIntent().getStringArrayListExtra("authors") : new ArrayList<>();
        ArrayList<String> authors_keys = getIntent().getStringArrayListExtra("authors_keys") != null ? getIntent().getStringArrayListExtra("authors_keys") : new ArrayList<>();
        String coverUrl = getIntent().getStringExtra("coverUrl") != null ? getIntent().getStringExtra("coverUrl") : "";
        int first_published_year = getIntent().hasExtra("first_publish_year") ? getIntent().getIntExtra("first_publish_year", -1) : -1;
        ArrayList<String> languages = getIntent().getStringArrayListExtra("languages") != null ? getIntent().getStringArrayListExtra("languages") : new ArrayList<>();


        // Configuración de Retrofit

        BooksDataBaseService service = RetrofitClient.getService();

        service.getWork(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(work -> {
                    // Crear un nuevo objeto Book y mapear las propiedades
                    if (!work.description.value.trim().isEmpty()) {
                        binding.sinopsisText.setVisibility(View.VISIBLE);
                        binding.sinopsisBox.setVisibility(View.VISIBLE);
                        binding.sinopsisTitle.setVisibility(View.VISIBLE);

                        binding.sinopsisText.setText(work.description.value);

                    }
                })
                .subscribe(
                        result -> {},
                        error -> Log.e("Error Work: ", error.toString())
                );

        if (!editionKey.isEmpty()) {
            service.getEdition(editionKey)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(edition -> {
                        // Crear un nuevo objeto Book y mapear las propiedades
                        if (edition.number_of_pages > 0) {
                            binding.detailsBox.setVisibility(View.VISIBLE);
                            binding.pagesText.setVisibility(View.VISIBLE);
                            binding.pagesTitle.setVisibility(View.VISIBLE);

                            binding.pagesText.setText(Integer.toString(edition.number_of_pages));

                        }

                        if(!edition.isbn_13.isEmpty()){
                            binding.isbnTitle.setVisibility(View.VISIBLE);
                            binding.isbnText.setVisibility(View.VISIBLE);

                            binding.isbnText.setText(edition.isbn_13.get(0));
                        }

                        ArrayList<String> publishers = edition.publishers;

                        if (!publishers.isEmpty() && !publishers.get(0).isEmpty()) {
                            binding.detailsBox.setVisibility(View.VISIBLE);
                            binding.editorialTitle.setVisibility(View.VISIBLE);
                            binding.editorialText.setVisibility(View.VISIBLE);

                            binding.editorialText.setText(edition.publishers.get(0));
                        }

                        if (!edition.publish_date.trim().isEmpty()) {
                            binding.detailsBox.setVisibility(View.VISIBLE);
                            binding.publicationText.setVisibility(View.VISIBLE);
                            binding.publicationTitle.setVisibility(View.VISIBLE);

                            binding.publicationText.setText(edition.publish_date.trim());
                        }

                    })
                    .subscribe(
                            work -> {
                            }, // No necesitas acción extra aquí
                            error -> {
                                binding.editorialText.setVisibility(View.GONE);
                                binding.pagesText.setVisibility(View.GONE);
                            }
                    );
        }

        binding.bookTitle.setText(title);

        if (!coverUrl.isEmpty()) {
            Glide.with(this)
                    .load(coverUrl.replace("M.jpg", "L.jpg"))
                    .into(binding.cover);

            binding.cover.setOnClickListener( v -> {
                    Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.dialog_image);

                    // Encontrar el ImageView dentro del diálogo
                    ImageView imageFullScreen = dialog.findViewById(R.id.imageFullScreen);
                    Glide.with(dialog.getContext())
                            .load(coverUrl.replace("M.jpg", "L.jpg"))
                            .override(820,820)
                            .into(imageFullScreen);

                        imageFullScreen.setOnClickListener(v1 -> dialog.dismiss());

                        dialog.show();
                    }
            );
        }
        if (authors.isEmpty()) {
            binding.authorName.setVisibility(View.GONE);
        } else {
            SpannableString spannableString = new SpannableString(authors.get(0));
            spannableString.setSpan(new UnderlineSpan(),0,spannableString.length(),0);
            binding.authorName.setText(spannableString);
            binding.authorName.setTextColor(Color.parseColor("#4F46E5"));
            if(!authors_keys.isEmpty()){
                binding.authorName.setOnClickListener(v -> {
                    Intent intent = new Intent(this, AuthorProfileActivity.class);
                    intent.putExtra("author_key",authors_keys.get(0));

                    this.startActivity(intent);
                });
            }

        }


    }

}