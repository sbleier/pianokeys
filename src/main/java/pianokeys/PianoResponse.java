package pianokeys;

import java.util.ArrayList;

public class PianoResponse
{
    Playlist playlist;

    public PianoResponse(Playlist playlist)
    {
        this.playlist = playlist;
    }

    public Playlist getComposition()
    {
        return playlist;
    }
}
