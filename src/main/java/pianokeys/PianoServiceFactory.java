package pianokeys;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PianoServiceFactory
{
    private final String lambdaUrl;

    public PianoServiceFactory(String lambdaUrl)
    {
        this.lambdaUrl = lambdaUrl;
    }

    public PianoService create()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(lambdaUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        PianoService service = retrofit.create(PianoService.class);
        return service;
    }
}
