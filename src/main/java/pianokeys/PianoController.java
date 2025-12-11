package pianokeys;

import javax.swing.Timer;
import java.util.*;

public class PianoController
{
    private final CompositionView compositionView;
    private final PianoSound sound;
    private final Composition composition;
    private final PianoView pianoView;
    private final Recorder recorder;
    private CompositionRunnable runnable;

    private boolean recording = true;
    private final Set<Integer> activeNotes = new HashSet<>();

    private final Map<Integer, Double> heldNoteStartTimes = new HashMap<>();
    private final Map<Integer, Long> heldNoteStartTimestamps = new HashMap<>();

    private Timer repaintTimer;

    public PianoController(CompositionView compositionView,
                           PianoSound sound,
                           Composition composition,
                           PianoView pianoView)
    {
        this.compositionView = compositionView;
        this.sound = sound;
        this.composition = composition;
        this.pianoView = pianoView;
        this.recorder = new Recorder(composition);

        repaintTimer = new Timer(50, e ->
        {
            if (!heldNoteStartTimes.isEmpty())
            {
               compositionView.repaint();
           }
       });
    }

    /**
     * Play the note in PianoSound
     *
     * @param note the MIDI note value to play
     */
    public void playNote(int note)
    {
        sound.playNote(note);
        pianoView.showKeyPlayed(note, true);
        if (recording)
        {
            activeNotes.add(note);
            double startTime = compositionView.getCurrentTime();
            recorder.startNote(note, startTime);

            heldNoteStartTimes.put(note, startTime);
            heldNoteStartTimestamps.put(note, System.currentTimeMillis());

            if (!repaintTimer.isRunning())
            {
                repaintTimer.start();
            }

            compositionView.repaint();
        }
    }

    /**
     * Stops the note from being played
     *
     * @param note the MIDI note value to stop
     */
    public void stopNote(int note)
    {
        sound.stopNote(note);
        pianoView.showKeyPlayed(note, false);

        if (recording)
        {
            activeNotes.remove(Integer.valueOf(note));
            recorder.stopNote(note);    // pass the note number
            heldNoteStartTimes.remove(note);
            heldNoteStartTimestamps.remove(note);
            compositionView.setCurrentTime(recorder.getCompositionTimeSeconds());
            compositionView.refreshLayout();

            if (heldNoteStartTimes.isEmpty() && repaintTimer.isRunning())
            {
                repaintTimer.stop();
            }
        }
    }

    public boolean playComposition()
    {
        //going to be using compositionRunnable
        if (runnable == null)
        {
            runnable = new CompositionRunnable(this, composition);
            new Thread(runnable).start();
            return true;
        }
        return false;
    }

    public void stopComposition()
    {
        if (runnable != null)
        {
            runnable.stop();
            runnable = null;
        }
    }

    //clears all notes from the composition
    public void eraseComposition()
    {
        composition.getNoteList().clear();
        recorder.reset();
        heldNoteStartTimes.clear();
        heldNoteStartTimestamps.clear();

        if (repaintTimer.isRunning())
        {
            repaintTimer.stop();
        }

        compositionView.setCurrentTime(0);
        compositionView.repaint();
    }

    //resets the composition playback to the beginning
    public void restartComposition()
    {
        compositionView.setCurrentTime(0);
    }

    //changes the current instrument sound
    public void changeInstrument(String instrument)
    {
        if (instrument != null && sound != null)
        {
            sound.setInstrument(instrument);
        }
    }

    public void setRecording(boolean recording)
    {
        this.recording = recording;
    }

    // updates the timeline position during the playback
    public void updateTimeline(double time)
    {
        compositionView.setCurrentTime(time);
    }

    // get the current timeline position
    public double getCurrentTime()
    {
        return compositionView.getCurrentTime();
    }

    public void setCurrentTime(double time)
    {
        compositionView.setCurrentTime(time);
        recorder.setCompositionTimeSeconds(time);
    }

    public Map<Integer, Double> getHeldNoteStartTimes()
    {
        return heldNoteStartTimes;
    }

    public Map<Integer, Double> getHeldNoteDuration()
    {
        Map<Integer, Double> result = new HashMap<>();
        long now = System.currentTimeMillis();

        for (Map.Entry<Integer, Long> entry : heldNoteStartTimestamps.entrySet())
        {
            int noteKey = entry.getKey();
            long startMs = entry.getValue();
            double elapsedSeconds = (now - startMs) / 1000.0;
            result.put(noteKey, elapsedSeconds);
        }

        return result;
    }

}
