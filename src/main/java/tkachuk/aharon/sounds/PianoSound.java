package tkachuk.aharon.sounds;

import javax.sound.midi.*;

public class PianoSound {
    private Synthesizer synthesizer;
    private MidiChannel channel;

    // MIDI note numbers for white keys starting from middle C
    private int[] notes = {60, 62, 64, 65, 67, 69, 71, 72}; // C, D, E, F, G, A, B, C

    public PianoSound() throws MidiUnavailableException {
        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        channel = synthesizer.getChannels()[0];
    }

    public void playNote(int keyIndex) {
        if (keyIndex >= 0 && keyIndex < notes.length) {
            channel.noteOn(notes[keyIndex], 127); // velocity 127 (max volume)
        }
    }

    public void stopNote(int keyIndex) {
        if (keyIndex >= 0 && keyIndex < notes.length) {
            channel.noteOff(notes[keyIndex]);
        }
    }

    public void cleanup() {
        synthesizer.close();
    }

}