package pianokeys;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompositionRunnableTest
{

    Synthesizer synthesizer = mock();
    MidiChannel channel = mock();

    @Test
    public void run() throws MidiUnavailableException
    {
        //given
        PianoSound sound = new PianoSound(synthesizer, channel);
        Composition comp = new Composition();
        comp.addNote(new Note(PianoSound.C4, 0.0, 0.25));
        comp.addNote(new Note(PianoSound.D4, 0.25, 0.5));
        CompositionRunnable runnable = new CompositionRunnable(sound, comp, 0.0);


        // when
        runnable.run();

        //then
        verify(channel).noteOn(PianoSound.C4, 127);
        verify(channel).noteOff(PianoSound.C4);
        verify(channel).noteOn(PianoSound.D4, 127);
        verify(channel).noteOff(PianoSound.D4);


    }

}