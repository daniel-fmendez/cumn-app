package com.example.entrega1.Connection.Edition;


import com.example.entrega1.Connection.Work.Author;
import com.example.entrega1.Connection.Work.Created;
import com.example.entrega1.Connection.Work.Description;
import com.example.entrega1.Connection.Work.LastModified;
import com.example.entrega1.Connection.Work.Type;

import java.util.ArrayList;

public class EditionRoot {
    public Description description;
    public Notes notes;
    public Identifiers identifiers;
    public String title;
    public ArrayList<Author> authors;
    public String publish_date;
    public ArrayList<TableOfContent> table_of_contents;
    public ArrayList<Integer> covers;
    public ArrayList<String> local_id;
    public ArrayList<String> lc_classifications;
    public String ocaid;
    public ArrayList<String> uri_descriptions;
    public ArrayList<Language> languages;
    public ArrayList<String> genres;
    public ArrayList<String> source_records;
    public String edition_name;
    public ArrayList<String> subjects;
    public String publish_country;
    public String by_statement;
    public ArrayList<String> oclc_numbers;
    public Type type;
    public ArrayList<String> uris;
    public ArrayList<String> publishers;
    public String physical_format;
    public ArrayList<String> publish_places;
    public ArrayList<String> lccn;
    public String pagination;
    public ArrayList<String> url;
    public ArrayList<String> isbn_13;
    public ArrayList<String> dewey_decimal_class;
    public ArrayList<String> isbn_10;
    public String copyright_date;
    public String key;
    public int number_of_pages;
    public ArrayList<Work> works;
    public int latest_revision;
    public int revision;
    public Created created;
    public LastModified last_modified;
}
