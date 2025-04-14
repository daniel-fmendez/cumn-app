package com.example.entrega1;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.entrega1.Connection.WorksByAuthor.Entry;
import com.example.entrega1.databinding.ActivityAuthorProfileBinding;


import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AuthorProfileActivity extends AppCompatActivity {
    private ActivityAuthorProfileBinding binding;
    private final ArrayList<Entry> entries = new ArrayList<>();
    private ShortWorksAdapter adapter;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityAuthorProfileBinding.inflate(getLayoutInflater()) ;
        View view = binding.getRoot();
        setContentView(view);
        adapter=new ShortWorksAdapter(entries);
        RecyclerView lista = binding.shortWorkList;
        lista.setLayoutManager(new LinearLayoutManager(this));
        lista.setAdapter(adapter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.arrowBack.setOnClickListener(v -> finish());
        //Bindings
        {
            binding.fullname.setVisibility(View.GONE);
            binding.birthday.setVisibility(View.GONE);
            binding.bioBox.setVisibility(View.GONE);
            //binding.detailsBox.setVisibility(View.GONE);
        }

        //Intents
        String authorKey= getIntent().getStringExtra("author_key");

        //Peticion
        BooksDataBaseService service = RetrofitClient.getService();

        //Datos
        service.getAuthor(authorKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(author -> {
                    binding.authorName.setText(author.name);

                    if(!author.birth_date.isEmpty()){
                        binding.birthday.setVisibility(View.VISIBLE);
                        binding.birthday.setText(author.birth_date);
                    }
                    if(!author.fuller_name.isEmpty()){
                        binding.fullname.setVisibility(View.VISIBLE);
                        binding.fullname.setText(author.fuller_name);
                    }
                    if(!author.bio.isEmpty()){
                        binding.bioBox.setVisibility(View.VISIBLE);
                        binding.bioText.setVisibility(View.VISIBLE);
                        binding.bioText.setText(author.bio);
                    }
                })
                .subscribe(
                        result -> {},
                        error -> Log.e("Error Autor: ", error.toString())
                );
        //Foto
        String image = "https://covers.openlibrary.org/a/olid/"+authorKey+"-L.jpg";


        Glide.with(this)
                .load(image)
                .override(350,350)
                .into(binding.photo);

        binding.photo.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_image);

            // Encontrar el ImageView dentro del diálogo
            ImageView imageFullScreen = dialog.findViewById(R.id.imageFullScreen);
            Glide.with(dialog.getContext())
                    .load(image)
                    .override(820,820)
                    .into(imageFullScreen);

            imageFullScreen.setOnClickListener(v1 -> dialog.dismiss());

            dialog.show();
        });



        //Adapter
        service.getWorksByAuthor(authorKey,10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(response -> response.entries != null ? response.entries : new ArrayList<>())
                .filter(Objects::nonNull)
                .map(api -> {
                    Log.e("TITULO",api.title);
                    Entry entry = new Entry();
                    entry.authors = api.authors;
                    entry.created = api.created;
                    entry.key = api.key;
                    entry.title = api.title;
                    entry.covers = api.covers;
                    entry.subject_places = api.subject_places;
                    entry.subjects = api.subjects;
                    entry.subject_people = api.subject_people;
                    entry.subject_times = api.subject_times;
                    entry.type = api.type;
                    entry.latest_revision = api.latest_revision;
                    entry.revision = api.revision;
                    entry.last_modified = api.last_modified;
                    return entry;
                })
                .toList()
                .subscribe(
                        entry -> {
                            entries.addAll(entry);
                            adapter.notifyDataSetChanged();
                        },
                        error -> Log.e("Error Obras: ", error.toString())
                );
    }
    public boolean isImageValid(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            // Verifica si la imagen es demasiado pequeña o completamente blanca
            if (bitmap != null && (bitmap.getWidth() > 1 || bitmap.getHeight() > 1)) {
                return true; // Imagen válida
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Imagen vacía o no válida
    }
}