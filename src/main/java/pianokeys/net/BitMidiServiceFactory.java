package pianokeys.net;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

public class BitMidiServiceFactory
{
    private final String baseUrl;

    public BitMidiServiceFactory(String baseUrl)
    {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    public BitMidiService create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(new OkHttpClient.Builder()
                        .build())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        return new BitMidiService(retrofit.create(BitMidiApi.class), baseUrl);
    }
}
