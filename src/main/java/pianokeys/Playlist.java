package pianokeys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Playlist
{
    ArrayList<Composition> compositionList = new ArrayList<>();

    public Playlist()
    {
    }

    public void addComposition(Composition composition)
    {
        compositionList.add(composition);
    }

    public void addCompositions(Composition... compositions)
    {
        compositionList.addAll(Arrays.stream(compositions).toList());
    }
}