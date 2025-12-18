package pianokeys;

import java.util.HashMap;
import java.util.Map;

public class Recorder
{
    private final Composition composition;
    private double compositionTimeSeconds;

    // track multiple notes with their start times for chord support
    private final Map<Integer, Long> noteStartMs = new HashMap<>();
    private final Map<Integer, Double> noteStartPosition = new HashMap<>();

    public Recorder(Composition composition)
    {
        this.composition = composition;
    }

    public void startNote(int note, double timelinePosition)
    {
        long startTimeMs = System.currentTimeMillis();
        noteStartMs.put(note, startTimeMs);
        noteStartPosition.put(note, timelinePosition);
    }

    public void stopNote(int note)
    {
        if (!noteStartMs.containsKey(note))
        {
            return;
        }

        long startTimeMs = noteStartMs.remove(note);
        double noteCompositionStart = noteStartPosition.remove(note);
        long endTimeMs = System.currentTimeMillis();
        double deltaTimeSeconds = Note.roundToNearestEighth((endTimeMs - startTimeMs) / 1000.0);

        if (deltaTimeSeconds == 0)
        {
            deltaTimeSeconds = Note.TIME_STEP;
        }

        composition.addNote(new Note(note,
                noteCompositionStart,
                noteCompositionStart + deltaTimeSeconds));

        // only advance time if no other notes are active (this allows for chords)
        if (noteStartMs.isEmpty())
        {
            compositionTimeSeconds = noteCompositionStart + deltaTimeSeconds;
        }
    }

    public void reset()
    {
        compositionTimeSeconds = 0;
        noteStartMs.clear();
        noteStartPosition.clear();
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
