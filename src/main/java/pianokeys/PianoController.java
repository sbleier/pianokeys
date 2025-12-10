package pianokeys;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

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
            recorder.startNote(note, compositionView.getCurrentTime());
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
            compositionView.setCurrentTime(recorder.getCompositionTimeSeconds());
            compositionView.refreshLayout();
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
        compositionView.setCurrentTime(0);
        compositionView.repaint();
        recorder.reset();
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

    // Insert a note or chord at the current timeline position
    public void insertNoteAtTimeline(int[] notes, double duration)
    {
        double currentTime = compositionView.getCurrentTime();

        for (int note : notes)
        {
            composition.addNote(new Note(note, currentTime, currentTime + duration));
        }

        compositionView.refreshLayout();
    }

    public void setCurrentTime(double time)
    {
        compositionView.setCurrentTime(time);
    }
}
