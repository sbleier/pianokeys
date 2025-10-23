package pianokeys;

import javax.sound.midi.*;

public class PianoSound
{
    // MIDI note constants
    public static final int C4 = 60;
    public static final int D4 = 62;
    public static final int E4 = 64;
    public static final int F4 = 65;
    public static final int G4 = 67;
    public static final int A4 = 69;
    public static final int B4 = 71;
    public static final int C5 = 72;

    // Black key (sharp) note constants
    public static final int C4_SHARP = 61;
    public static final int D4_SHARP = 63;
    public static final int F4_SHARP = 66;
    public static final int G4_SHARP = 68;
    public static final int A4_SHARP = 70;

    private final Synthesizer synthesizer;
    private final MidiChannel channel;

    // Use the constants in your array
    public static final int[] whiteNotes = {C4, D4, E4, F4, G4, A4, B4, C5};
    public static final int[] blackNotes = {C4_SHARP, D4_SHARP, -1, F4_SHARP, G4_SHARP, A4_SHARP, -1};

    public PianoSound(Synthesizer synthesizer, MidiChannel channel) throws MidiUnavailableException
    {
        this.synthesizer = synthesizer;
        this.channel = channel;
    }

    public MidiChannel getChannel()
    {
        return channel;
    }

    public Synthesizer getSynthesizer()
    {
        return synthesizer;
    }

    public void playNote(int note)
    {
            channel.noteOn(note, 127); // velocity 127 (max volume)
    }

    public void stopNote(int note)
    {
            channel.noteOff(note);
    }

    public void cleanup()
    {
        synthesizer.close();
    }

}

