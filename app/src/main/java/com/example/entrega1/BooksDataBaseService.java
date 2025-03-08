package com.example.entrega1;

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


}