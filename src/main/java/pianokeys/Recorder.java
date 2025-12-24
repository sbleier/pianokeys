package pianokeys;

public class Recorder
{
    private final Composition composition;
    private double compositionTimeSeconds;

    // track multiple notes with their start times for chord support
    private Long noteStartMs = null;
    private Double noteStartPosition = null;

    public Recorder(Composition composition)
    {
        this.composition = composition;
    }

    public void startNote(int note, double timelinePosition)
    {
        noteStartMs = System.currentTimeMillis();
        noteStartPosition = timelinePosition;
    }

    public void stopNote(int note)
    {
        if (noteStartMs == null)
        {
            return;
        }

        long startTimeMs = noteStartMs;
        double noteCompositionStart = noteStartPosition;
        long endTimeMs = System.currentTimeMillis();
        double deltaTimeSeconds = Note.roundToNearestEighth((endTimeMs - startTimeMs) / 1000.0);

        if (deltaTimeSeconds == 0)
        {
            deltaTimeSeconds = Note.TIME_STEP;
        }

        composition.addNote(new Note(note,
                noteCompositionStart,
                noteCompositionStart + deltaTimeSeconds));

        compositionTimeSeconds = noteCompositionStart + deltaTimeSeconds;

        // Clear the tracking variables
        noteStartMs = null;
        noteStartPosition = null;

    }

    public void reset()
    {
        compositionTimeSeconds = 0;
        noteStartMs = null;
        noteStartPosition = null;
    }

    public double getCompositionTimeSeconds()
    {
        return compositionTimeSeconds;
    }

    public void setCompositionTimeSeconds(double time)
    {
        this.compositionTimeSeconds = time;
    }
}
