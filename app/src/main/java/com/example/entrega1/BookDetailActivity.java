package com.example.entrega1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.entrega1.databinding.ActivityBookDetailBinding;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookDetailActivity extends AppCompatActivity {

    private ActivityBookDetailBinding binding;

    @SuppressLint({"SetTextI18n", "CheckResult"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBookDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

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
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://openlibrary.org/")
                .addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava3CallAdapterFactory
                        .create()).build();
        WorkDataBaseService service = retrofit.create(WorkDataBaseService.class);

        service.getWork(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(work -> {
                    // Crear un nuevo objeto Book y mapear las propiedades

                    if (!work.description.value.trim().isEmpty()) {

                        binding.sinopsisText.setText(work.description.value);
                        binding.sinopsisText.setVisibility(View.VISIBLE);
                        binding.sinopsisBox.setVisibility(View.VISIBLE);
                        binding.sinopsisTitle.setVisibility(View.VISIBLE);
                    }
                })
                .subscribe(
                        work -> {
                        }, // No necesitas acción extra aquí
                        error -> Log.e("Error Work: ", error.toString())
                );

        if (!editionKey.isEmpty()) {
            service.getEdition(editionKey)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(edition -> {
                        // Crear un nuevo objeto Book y mapear las propiedades
                        if (edition.number_of_pages > 0) {
                            binding.pagesText.setText("Pages\n" + Integer.toString(edition.number_of_pages));
                            binding.pagesText.setVisibility(View.VISIBLE);
                        }

                        ArrayList<String> publishers = edition.publishers;

                        if (!publishers.isEmpty()) {
                            binding.editorialText.setText("Editorial\n" + edition.publishers.get(0));
                            binding.editorialText.setVisibility(View.VISIBLE);
                            Log.e("Actualizado=", Integer.toString(binding.editorialText.getVisibility()));
                        }

                        if (!edition.publish_date.trim().isEmpty()) {
                            binding.publishDateText.setText("Publicación\n" + edition.publish_date.trim());
                            binding.publishDateText.setVisibility(View.VISIBLE);
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

        binding.titleText.setText(title);
        if (!subtitle.isEmpty()) {
            Log.e("SUBTITLE", "subtitulo no vacio:" + subtitle);
            binding.subtitle.setText(subtitle);
        } else {
            Log.e("SUBTITLE", "subtitulo vacio");
            binding.subtitle.setVisibility(View.GONE);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.coverDetail.getLayoutParams();
            params.topToBottom = R.id.title_text;
            binding.coverDetail.setLayoutParams(params);
        }

        if (!coverUrl.isEmpty()) {
            Glide.with(this)
                    .load(coverUrl.replace("M.jpg", "L.jpg"))
                    .into(binding.coverDetail);
        }
        if (authors.isEmpty()) {
            binding.autorTitle.setVisibility(View.GONE);
            binding.autorText.setVisibility(View.GONE);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.sinopsisTitle.getLayoutParams();
            params.topToBottom = R.id.first_publication;
            binding.sinopsisTitle.setLayoutParams(params);
        } else {
            binding.autorText.setText(authors.get(0));
        }


        if (first_published_year > 0) {
            binding.firstPublication.setText(Integer.toString(first_published_year));
        } else {
            binding.firstPublication.setVisibility(View.GONE);
        }


    }

}