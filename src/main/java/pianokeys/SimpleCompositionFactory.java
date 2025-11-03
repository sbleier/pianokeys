package pianokeys;

/**
 * Factory for creating Compositions that are a set of keys with the same time in-between them.
 */
public class SimpleCompositionFactory
{

    public Composition toComposition(int[] keys, double timeIncrement)
    {
        Composition composition = new Composition();
        double time = 0;
        for (int key : keys)
        {
            composition.addNote(new Note(key, time, time + timeIncrement));
            time += timeIncrement;
        }
        return composition;
    }

}
