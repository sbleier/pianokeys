package pianokeys;

import javax.sound.midi.*;

public class PianoSound
{
    // MIDI note constants
    private final int NOTE_C4 = 60;
    private final int NOTE_D4 = 62;
    private final int NOTE_E4 = 64;
    private final int NOTE_F4 = 65;
    private final int NOTE_G4 = 67;
    private final int NOTE_A4 = 69;
    private final int NOTE_B4 = 71;
    private final int NOTE_C5 = 72;

    private Synthesizer synthesizer;
    private MidiChannel channel;

    // Use the constants in your array
    private int[] notes = {NOTE_A4, NOTE_D4, NOTE_E4, NOTE_F4, NOTE_G4, NOTE_A4, NOTE_B4, NOTE_C5};

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