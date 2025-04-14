package com.example.entrega1.Connection.Author;

import com.example.entrega1.Connection.Work.Created;
import com.example.entrega1.Connection.Work.LastModified;
import com.example.entrega1.Connection.Work.Link;
import com.example.entrega1.Connection.Work.Type;

import java.util.ArrayList;

public class AuthorRoot {
    public String personal_name;
    public String key;
    public String entity_type;
    public String birth_date;
    public ArrayList<Link> links;
    public ArrayList<String> alternate_names;
    public String name;
    public RemoteIds remote_ids;
    public Type type;
    public String title;
    public String bio;
    public String fuller_name;
    public ArrayList<String> source_records;
    public ArrayList<Integer> photos;
    public int latest_revision;
    public int revision;
    public Created created;
    public LastModified last_modified;
}
