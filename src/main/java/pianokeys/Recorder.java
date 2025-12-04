package pianokeys;

import java.util.HashMap;
import java.util.Map;

public class Recorder
{

    private final Composition composition;
    private double compositionTimeSeconds;

    // track multiple notes with their start times for chord support
    private final Map<Integer, Double> activeNotes = new HashMap<>();
    private final Map<Integer, Double> noteStartTimes = new HashMap<>();

    public Recorder(Composition composition)
    {
        this.composition = composition;
    }

    public void startNote(int note, double timelinePosition)
    {
        double startTimeMs = System.currentTimeMillis();
        activeNotes.put(note, startTimeMs);
        noteStartTimes.put(note, compositionTimeSeconds);
    }

    public void stopNote(int note)
    {
        if (!activeNotes.containsKey(note))
        {
            return;
        }

        double startTimeMs = activeNotes.remove(note);
        double noteCompositionStart = noteStartTimes.remove(note);
        double endTimeMs = System.currentTimeMillis();
        double deltaTimeSeconds = Note.roundToNearestEighth((endTimeMs - startTimeMs) / 1000.0);

        if (deltaTimeSeconds == 0)
        {
            deltaTimeSeconds = Note.TIME_STEP;
        }

        composition.addNote(new Note(note,
                noteCompositionStart,
                noteCompositionStart + deltaTimeSeconds));

        // only advance time if no other notes are active (this allows for chords)
        if (activeNotes.isEmpty())
        {
            compositionTimeSeconds = noteCompositionStart + deltaTimeSeconds;
        }
    }

    public void reset()
    {
        compositionTimeSeconds = 0;
        activeNotes.clear();
        noteStartTimes.clear();
    }
}
