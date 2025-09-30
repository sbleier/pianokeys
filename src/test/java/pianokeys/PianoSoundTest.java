package pianokeys;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PianoSoundTest
{

    @Test
    void playNote() throws MidiUnavailableException
    {
        // given
        PianoSound piano = new PianoSound();

        // when
        piano.playNote(2);

        // then
        assertTrue(piano.getSynthesizer().isOpen());
    }

    @Test
    void stopNote() throws MidiUnavailableException
    {
        // given
        PianoSound piano = new PianoSound();
        piano.playNote(2);

        // when
        piano.stopNote(2);

        // then
        assertTrue(piano.getSynthesizer().isOpen());
    }

    @Test
    void cleanup() throws MidiUnavailableException
    {
        // given
        PianoSound piano = new PianoSound();
        Synthesizer synthesizer = piano.getSynthesizer();

        // when
        assertTrue(synthesizer.isOpen());
        piano.cleanup();

        // then
        assertFalse(synthesizer.isOpen());
    }

}