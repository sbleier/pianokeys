package pianokeys;

public class PlaylistResponse
{
    private final Playlist playlist;

    public PlaylistResponse(Playlist playlist)
    {
        this.playlist = playlist;
    }

    public Playlist getPlaylist()
    {
        return playlist;
    }


}
