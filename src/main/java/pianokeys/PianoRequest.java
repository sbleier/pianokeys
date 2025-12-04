package pianokeys;

public class PianoRequest
{
    Composition composition;

    public PianoRequest(Composition composition)
    {
        this.composition = composition;
    }

    public Composition getComposition()
    {
        return composition;
    }
}
