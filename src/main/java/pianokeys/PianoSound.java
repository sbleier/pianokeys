package pianokeys;

import javax.sound.midi.*;

public class PianoSound
{
    // MIDI note constants
    private final int c4 = 60;
    private final int d4 = 62;
    private final int e4 = 64;
    private final int f4 = 65;
    private final int g4 = 67;
    private final int a4 = 69;
    private final int b4 = 71;
    private final int c5 = 72;

    private Synthesizer synthesizer;
    private MidiChannel channel;

    // Use the constants in your array
    private int[] notes = {c4, d4, e4, f4, g4, a4, b4, c5};

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