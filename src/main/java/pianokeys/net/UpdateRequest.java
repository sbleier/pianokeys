package pianokeys.net;

import pianokeys.Composition;

public class UpdateRequest
{
    Composition composition;

    // Constructor
    public UpdateRequest(Composition composition)
    {
        this.composition = composition;
    }

    // Getter
    public Composition getComposition()
    {
        return composition;
    }
}
