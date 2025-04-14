package com.example.entrega1;

import com.example.entrega1.Connection.Author.AuthorRoot;
import com.example.entrega1.Connection.Books.BookRoot;
import com.example.entrega1.Connection.Edition.EditionRoot;
import com.example.entrega1.Connection.Work.Author;
import com.example.entrega1.Connection.Work.WorkRoot;
import com.example.entrega1.Connection.WorksByAuthor.WorkByAuthorRoot;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BooksDataBaseService {
    // public final static String API_KEY = XXXX;
    // Observable<Resultado> listCards(@Query("api_key") String api_key);

    //limitamos el acceso
    @GET("search.json")
    Observable<BookRoot> listBooks(@Query("title") String title, @Query("limit") int limit);

    //https://covers.openlibrary.org/
    @GET("{type}/{key}/{id}-{size}.jpg")
    Observable<BookRoot> getImage(@Path("type") String type, @Path("key") String key,
                                  @Path("id") String id, @Path("size") String size);

    @GET("works/{id}.json")
    Observable<WorkRoot> getWork(@Path("id") String id);

    @GET("authors/{id}.json")
    Observable<AuthorRoot> getAuthor(@Path("id") String id);

    @GET("authors/{id}/works.json")
    Observable<WorkByAuthorRoot> getWorksByAuthor(@Path("id") String id, @Query("limit") int limit);
    @GET("books/{id}.json")
    Observable<EditionRoot> getEdition(@Path("id") String id);
}