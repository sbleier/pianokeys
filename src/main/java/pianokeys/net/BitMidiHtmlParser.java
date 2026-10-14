package pianokeys.net;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

final class BitMidiHtmlParser
{
    // Parse the HTML and keep only anchor links that point to MIDI files.
    List<BitMidiTrack> searchResults(String html, String baseUrl)
    {
        Document document = Jsoup.parse(html, baseUrl);
        List<BitMidiTrack> tracks = new ArrayList<>();
        for (Element link : document.select("a[href]")) {
            String title = link.text().trim();
            String pageUrl = link.absUrl("href");
            // Match the BitMidi link pattern: text ends in .mid and href looks like a MIDI file URL.
            if (!title.toLowerCase().endsWith(".mid") || !pageUrl.matches(".+/.+-mid(?:[?#].*)?$")) {
                continue;
            }

            tracks.add(new BitMidiTrack(title, pageUrl, ""));
        }
        return List.copyOf(tracks);
    }

    // Pull the final path segment from the download URL so the saved filename is stable.
    String fileName(BitMidiTrack track) {
        String path = java.net.URI.create(track.downloadUrl()).getPath();
        int slash = path.lastIndexOf('/');
        String fileName = slash >= 0 ? path.substring(slash + 1) : path;
        return fileName.isBlank() ? track.title() : fileName;
    }

    // Find the first MIDI download link on the page and return its absolute URL.
    String downloadUrl(String html, String pageUrl) {
        Element link = Jsoup.parse(html, pageUrl).selectFirst("a[href$=.mid]");
        return link == null ? "" : link.absUrl("href");
    }
}
