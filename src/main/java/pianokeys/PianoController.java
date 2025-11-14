package pianokeys;

public class PianoController
{
    private CompositionView compositionView;
    private PianoSound sound;
    private Composition composition;
    private PianoView pianoView;
    private long recordStartTime = -1;
    private CompositionRunnable runnable;

    public PianoController(CompositionView compositionView,
                           PianoSound sound,
                           Composition composition,
                           PianoView pianoView) {
        this.compositionView = compositionView;
        this.sound = sound;
        this.composition = composition;
        this.pianoView = pianoView;
    }

    /**
     * Play the note in PianoSound
     *
     * @param note the MIDI note value to play
     */
    public void playNote(int note)
    {
        sound.playNote(note);
    }

    /**
     * Stops the note from being played
     *
     * @param note the MIDI note value to stop
     */
    public void stopNote(int note)
    {
        sound.stopNote(note);
    }

    public boolean playComposition()
    {
        //going to be using compositionRunnable
        if (runnable == null) {
            runnable = new CompositionRunnable(sound, Composition.ODE_TO_JOY, compositionView, pianoView);
            new Thread(runnable).start();
            return true;
        }
        return false;
    }

    public void stopComposition()
    {
        if (runnable != null) {
            runnable.stop();
            runnable = null;
        }
    }
}
