package pianokeys;

public class PianoController
{
    private CompositionView compositionView;
    private PianoSound sound;
    private Composition composition;

    public PianoController(CompositionView compositionView, PianoSound sound, Composition composition) {
        this.compositionView = compositionView;
        this.sound = sound;
        this.composition = composition;
    }

    public void playNote(int note)
    {
        if(sound != null){
            sound.playNote(note);
        }

    }

    public void stopNote(int note)
    {
        if(sound != null){
            sound.stopNote(note);
        }

    }

    public void playComposition()
    {
    }

    public void pauseComposition()
    {
    }
}
