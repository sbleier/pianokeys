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

    private Integer currentHeldNote = null;
    private double heldNoteTimelinePosition = 0; // timeline position where it started
    private long heldNoteStartTimeMs = 0; // system time when it is started

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
            if (currentHeldNote != null)
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

            currentHeldNote = note;
            heldNoteTimelinePosition = startTime;
            heldNoteStartTimeMs = System.currentTimeMillis();

            if (!repaintTimer.isRunning())
            {
                repaintTimer.start();
            }
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

            // clear the held note
            currentHeldNote = null;

            compositionView.setCurrentTime(recorder.getCompositionTimeSeconds());
            compositionView.refreshLayout();

            if (repaintTimer.isRunning())
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
        currentHeldNote = null;

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

    public Integer getCurrentHeldNote()
    {
        return currentHeldNote;
    }

    public double getHeldNoteTimelinePosition()
    {
        return heldNoteTimelinePosition;
    }

    public double getHeldNoteDuration()
    {
        if (currentHeldNote == null)
        {
            return 0;
        }
        long now = System.currentTimeMillis();
        return (now - heldNoteStartTimeMs) / 1000.0;
    }

}
