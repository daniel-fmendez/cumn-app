package com.example.entrega1;


import com.example.entrega1.Connection.Edition.EditionRoot;
import com.example.entrega1.Connection.Work.WorkRoot;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WorkDataBaseService {
    //https://covers.openlibrary.org/

    @GET("works/{id}.json")
    Observable<WorkRoot> getWork(@Path("id") String id);

    @GET("books/{id}.json")
    Observable<EditionRoot> getEdition(@Path("id") String id);
}

