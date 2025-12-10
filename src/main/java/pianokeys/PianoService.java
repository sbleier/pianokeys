package pianokeys;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

public interface PianoService
{
    @POST("/compositions")
    Single<PianoResponse> createComposition(@Body PianoRequest request);

    @GET("/compositions")
    Single<PianoResponse> getComposition();

    @PUT("/compositions")
    Single<PianoResponse> updateComposition(@Body PianoRequest request);

    @DELETE("/")
    Completable deleteComposition();
}
