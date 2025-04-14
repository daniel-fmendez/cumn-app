package com.example.entrega1;

import java.util.ArrayList;

public class Book {
    public ArrayList<String> author_key;
    public ArrayList<String> author_name;
    public String cover_edition_key;
    public int cover_i;
    public String coverUrl;
    public int edition_count;
    public int first_publish_year;
    public boolean has_fulltext;
    public ArrayList<String> ia;
    public String ia_collection_s;
    public String key;
    public ArrayList<String> language;
    public boolean public_scan_b;
    public String subtitle;
    public String title;
    public String lending_edition_s;
    public String lending_identifier_s;


    public Book() {
    }

    public Book(String tituloDelLibro, String url) {
        this.coverUrl = url;
        this.title = tituloDelLibro;
    }
}


