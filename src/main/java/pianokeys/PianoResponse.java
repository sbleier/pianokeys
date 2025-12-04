package pianokeys;

public class PianoResponse
{
    Composition composition;

    public PianoResponse(Composition composition)
    {
        this.composition = composition;
    }

    public Composition getComposition()
    {
        return composition;
    }
}
