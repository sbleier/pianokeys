package pianokeys;

public class CreateRequest
{
    private final Composition composition;

    public CreateRequest(Composition composition)
    {
        this.composition = composition;
    }

    public Composition getComposition()
    {
        return composition;
    }
}
