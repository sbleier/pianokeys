package pianokeys;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PianoSoundTest
{
    MidiChannel channel = mock();
    Synthesizer synthesizer = mock();

    @Test
    void playNote() throws MidiUnavailableException
    {
        // given
        PianoSound piano = new PianoSound(synthesizer, channel);

        // when
        piano.playNote(PianoSound.C4);

        // then
        verify(channel).noteOn(PianoSound.C4, 127);
    }

    @Test
    void stopNote() throws MidiUnavailableException
    {
        // given
        PianoSound piano = new PianoSound(synthesizer, channel);

        // when
        piano.stopNote(PianoSound.C4);

        // then
        verify(channel).noteOff(PianoSound.C4);
    }

    @Test
    void cleanup() throws MidiUnavailableException
    {
        // given
        PianoSound piano = new PianoSound(synthesizer, channel);

        // when
        piano.cleanup();

        // then
        verify(synthesizer).close();
    }


}