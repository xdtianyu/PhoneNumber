package org.xdty.phone.number.net.cloud;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CloudService {

    @GET("caller")
    Call<CloudNumber> get(@Query("number") String number,
            @Header("Authorization") String authorization);

    @GET("caller")
    Call<NumberList> getAll(@Query("uid") String uid,
            @Header("Authorization") String authorization);

    @POST("caller")
    Call<CloudStatus> put(@Body CloudNumber cloudNumber,
            @Header("Authorization") String authorization);

    @PATCH("caller/{id}")
    Call<CloudStatus> patch(@Path("id") String id, @Body CloudNumber cloudNumber,
            @Header("Authorization") String authorization,
            @Header("If-Match") String eTag);

    @DELETE("caller/{id}")
    Call<CloudStatus> delete(@Path("id") String id,
            @Header("Authorization") String authorization,
            @Header("If-Match") String eTag);

}
