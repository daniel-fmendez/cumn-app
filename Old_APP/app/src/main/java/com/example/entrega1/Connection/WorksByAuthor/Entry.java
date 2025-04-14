package com.example.entrega1.Connection.WorksByAuthor;

import com.example.entrega1.Connection.Work.Author;
import com.example.entrega1.Connection.Work.Created;
import com.example.entrega1.Connection.Work.LastModified;
import com.example.entrega1.Connection.Work.Type;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Entry {
    @Expose(deserialize = false)
    public Object description;

    public String title;
    public ArrayList<Integer> covers;
    public ArrayList<String> subject_places;
    public ArrayList<String> subjects;
    public ArrayList<String> subject_people;
    public String key;
    public ArrayList<Author> authors;
    public ArrayList<String> subject_times;
    public Type type;
    public int latest_revision;
    public int revision;
    public Created created;
    public LastModified last_modified;

}
