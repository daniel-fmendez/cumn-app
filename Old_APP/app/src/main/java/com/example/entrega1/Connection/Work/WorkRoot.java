package com.example.entrega1.Connection.Work;

import com.example.entrega1.Connection.Edition.Language;
import com.example.entrega1.Connection.Edition.Notes;
import com.example.entrega1.Connection.Edition.TableOfContent;
import com.example.entrega1.Connection.Edition.Work;
import com.example.entrega1.Connection.Books.Identifiers;

import java.util.ArrayList;

public class WorkRoot {

    public Description description;
    public ArrayList<Link> links;
    public String title;
    public ArrayList<Integer> covers;
    public ArrayList<String> subject_places;
    public String first_publish_date;
    public ArrayList<String> subject_people;
    public String key;
    public ArrayList<Author> authors;
    public ArrayList<Excerpt> excerpts;
    public ArrayList<String> subjects;
    public Type type;
    public ArrayList<String> subject_times;
    public CoverEdition cover_edition;
    public int latest_revision;
    public int revision;
    public Created created;
    public LastModified last_modified;
    public Notes notes;
    public Identifiers identifiers;
    public String publish_date;
    public ArrayList<TableOfContent> table_of_contents;
    public ArrayList<String> local_id;
    public ArrayList<String> lc_classifications;
    public String ocaid;
    public ArrayList<String> uri_descriptions;
    public ArrayList<Language> languages;
    public ArrayList<String> genres;
    public ArrayList<String> source_records;
    public String edition_name;
    public String publish_country;
    public String by_statement;
    public ArrayList<String> oclc_numbers;
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
    public int number_of_pages;
    public ArrayList<Work> works;

}
