package pianokeys;

public class PianoRequest
{
    private Composition composition;

    public PianoRequest(Composition composition)
    {
        this.composition = composition;
    }

    public Composition getComposition()
    {
        return composition;
    }
}
