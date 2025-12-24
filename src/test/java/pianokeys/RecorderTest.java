package pianokeys;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static pianokeys.PianoSound.C4;
import static pianokeys.PianoSound.D4;

class RecorderTest
{

    @Test
    void startNote()
    {
        // given
        Composition composition = new Composition();
        Recorder recorder = new Recorder(composition);

        // when
        recorder.startNote(C4, 0.0);

        // then
        assertEquals(0, composition.getNoteList().size());
    }

    @Test
    void stopNote()
    {
        // given
        Composition composition = new Composition();
        Recorder recorder = new Recorder(composition);

        // when
        recorder.startNote(C4, 0.0);
        recorder.stopNote(C4);

        // then
        assertEquals(1, composition.getNoteList().size());
        assertEquals(C4, composition.getNoteList().get(0).key());
    }

    @Test
    void reset()
    {
        // given
        Composition composition = new Composition();
        Recorder recorder = new Recorder(composition);
        recorder.startNote(C4, 0.0);
        recorder.stopNote(C4);

        // when
        recorder.reset();
        recorder.startNote(D4, 0.0);
        recorder.stopNote(D4);

        // then
        assertEquals(0, composition.getNoteList().get(1).startTime());
        assertTrue(composition.getNoteList().get(0).endTime() > 0);
    }
}