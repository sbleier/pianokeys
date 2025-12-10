package pianokeys;

public class Recorder
{

    private final Composition composition;
    private double compositionTimeSeconds;
    private double startTimeMs;
    private int note;

    public Recorder(Composition composition)
    {
        this.composition = composition;
    }

    public void startNote(int note)
    {
        startTimeMs = System.currentTimeMillis();
        this.note = note;
    }

    public void stopNote()
    {
        double endTimeMs = System.currentTimeMillis();
        double deltaTimeSeconds = Note.roundToNearestEighth((endTimeMs - startTimeMs) / 1000.0);
        if (deltaTimeSeconds == 0)
        {
            deltaTimeSeconds = Note.TIME_STEP;
        }
        composition.addNote(new Note(note,
                compositionTimeSeconds,
                compositionTimeSeconds + deltaTimeSeconds));

        compositionTimeSeconds += deltaTimeSeconds;
    }

    public void reset()
    {
        compositionTimeSeconds = 0;
    }

    public double getCompositionTimeSeconds()
    {
        return compositionTimeSeconds;
    }
}
