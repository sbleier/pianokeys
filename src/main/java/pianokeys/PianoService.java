package pianokeys;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

public interface PianoService
{
    // post
    @POST("/")
    Single<PianoResponse> createComposition(@Body PianoRequest request);

    // get
    @GET("/")
    Single<PianoResponse> getComposition();

    // put
    @PUT("/")
    Single<PianoResponse> updateComposition(@Body PianoRequest request);

    // delete
    @DELETE("/")
    Completable deleteComposition();
}
