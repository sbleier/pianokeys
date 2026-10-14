package pianokeys.net;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BitMidiServiceTest
{
    private BitMidiService service;

    @BeforeEach
    void setUp()
    {
        service = new BitMidiServiceFactory("https://bitmidi.com/").create();
    }

    @Test
    void searchParsesBitMidiHtml() throws Exception
    {
        List<BitMidiTrack> tracks = service.search("mario").blockingGet();
        assertFalse(tracks.isEmpty());
        BitMidiTrack track = tracks.get(0);

        assertTrue(track.title().toLowerCase(Locale.ROOT).endsWith(".mid"));
        URI pageUri = URI.create(track.pageUrl());
        assertEquals("https", pageUri.getScheme());
        assertEquals("bitmidi.com", pageUri.getHost());
        assertTrue(track.downloadUrl().isBlank());
    }

    @Test
    void downloadReturnsMidiBytes() throws Exception
    {
        List<BitMidiTrack> tracks = service.search("mario").blockingGet();
        assertFalse(tracks.isEmpty());
        BitMidiTrack track = tracks.get(0);

        BitMidiFile file = service.download(track).blockingGet();

        assertFalse(file.fileName().isBlank());
        assertTrue(file.content().length >= 4);
        assertArrayEquals(new byte[] {0x4d, 0x54, 0x68, 0x64},
                java.util.Arrays.copyOf(file.content(), 4));
    }
}
