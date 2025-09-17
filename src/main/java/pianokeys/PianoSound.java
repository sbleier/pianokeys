package pianokeys;

import javax.sound.midi.*;

public class PianoSound
{
    // MIDI note constants
    private final int C4 = 60;
    private final int D4 = 62;
    private final int E4 = 64;
    private final int F4 = 65;
    private final int G4 = 67;
    private final int A4 = 69;
    private final int B4 = 71;
    private final int C5 = 72;

    private Synthesizer synthesizer;
    private MidiChannel channel;

    // Use the constants in your array
    private int[] notes = {C4, D4, E4, F4, G4, A4, B4, C5};

    public PianoSound() throws MidiUnavailableException
    {
        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        channel = synthesizer.getChannels()[0];
    }

    public void playNote(int keyIndex)
    {
        if (keyIndex >= 0 && keyIndex < notes.length)
        {
            channel.noteOn(notes[keyIndex], 127); // velocity 127 (max volume)
        }
    }

    public void stopNote(int keyIndex)
    {
        if (keyIndex >= 0 && keyIndex < notes.length)
        {
            channel.noteOff(notes[keyIndex]);
        }
    }

    public void cleanup()
    {
        synthesizer.close();
    }

}