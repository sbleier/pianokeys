package pianokeys.net;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

public interface PianoService
{
    @POST("/")
    Completable createComposition(@Body CreateRequest request);

    @GET("/")
    Single<PlaylistResponse> getComposition();

    @PUT("/")
    Completable updateComposition(@Body UpdateRequest request);

    @HTTP(method = "DELETE", path = "/", hasBody = true)
    Completable deleteComposition(@Body DeleteRequest request);
}
