package pianokeys.net;

import io.reactivex.rxjava3.core.Single;
import java.io.IOException;
import java.util.List;
import okhttp3.ResponseBody;

public class BitMidiService
{
    private final BitMidiApi api;
    private final String baseUrl;

    BitMidiService(BitMidiApi api, String baseUrl) {
        this.api = api;
        this.baseUrl = baseUrl;
    }

    public Single<List<BitMidiTrack>> search(String query)
    {
        return search(query, 1);
    }

    public Single<List<BitMidiTrack>> search(String query, int page) {
        if (query == null || query.isBlank()) {
            return Single.error(new IllegalArgumentException("query must not be blank"));
        }
        if (page < 1) {
            return Single.error(new IllegalArgumentException("page must be positive"));
        }
        return api.search(query, page)
                .map(response -> parseSearchResponse(response, baseUrl));
    }

    public Single<BitMidiFile> download(BitMidiTrack track)
    {
        if (track == null || track.pageUrl().isBlank()) {
            return Single.error(new IllegalArgumentException("track must have a page URL"));
        }
        BitMidiHtmlParser htmlParser = new BitMidiHtmlParser();
        Single<String> downloadUrl;
        if (track.downloadUrl().isBlank()) {
            downloadUrl = api.page(track.pageUrl())
                    .map(this::readHtml)
                    .map(html -> htmlParser.downloadUrl(html, track.pageUrl()));
        } else {
            downloadUrl = Single.just(track.downloadUrl());
        }
        return downloadUrl.flatMap(url -> {
            if (url.isBlank()) {
                return Single.error(new IllegalArgumentException("BitMidi page has no MIDI download"));
            }
            return api.download(url).map(response ->
                    new BitMidiFile(fileName(url, track), readBytes(response).clone()));
        });
    }

    private List<BitMidiTrack> parseSearchResponse(ResponseBody response, String baseUrl) {
        try (response) {
            return new BitMidiHtmlParser().searchResults(response.string(), baseUrl);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read BitMidi search response", exception);
        }
    }

    private byte[] readBytes(ResponseBody response) {
        try (response) {
            return response.bytes();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read BitMidi download", exception);
        }
    }

    private String readHtml(ResponseBody response) {
        try (response) {
            return response.string();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read BitMidi page", exception);
        }
    }

    private String fileName(String downloadUrl, BitMidiTrack track) {
        return new BitMidiHtmlParser().fileName(new BitMidiTrack(track.title(), track.pageUrl(), downloadUrl));
    }
}
